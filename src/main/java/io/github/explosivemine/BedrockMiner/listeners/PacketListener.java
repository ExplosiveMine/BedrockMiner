package io.github.explosivemine.BedrockMiner.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import io.github.explosivemine.BedrockMiner.config.parser.impl.BlockSettingsParser;
import io.github.explosivemine.BedrockMiner.config.parser.impl.ToolParser;
import io.github.explosivemine.BedrockMiner.util.Executor;
import io.github.explosivemine.BedrockMiner.BPlugin;
import io.github.explosivemine.BedrockMiner.config.parser.Lang;
import io.github.explosivemine.BedrockMiner.util.Logging;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class PacketListener extends PacketAdapter {
    private final BPlugin plugin;

    private final Map<UUID, BukkitRunnable> players = new HashMap<>();

    public PacketListener(BPlugin plugin) {
        super(plugin, PacketType.Play.Client.BLOCK_DIG);
        this.plugin = plugin;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE))
            return;

        BlockPosition blockPosition = event.getPacket().getBlockPositionModifier().read(0);
        EnumWrappers.PlayerDigType type = event.getPacket().getPlayerDigTypes().read(0);

        UUID playerUUID = player.getUniqueId();
        switch (type) {
            case STOP_DESTROY_BLOCK, ABORT_DESTROY_BLOCK -> stopDigging(blockPosition, playerUUID);
            case START_DESTROY_BLOCK -> {
                Location location = blockPosition.toLocation(player.getWorld());
                if (!location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4))
                    return;

                Material blockType = location.getBlock().getType();
                BlockSettingsParser parser = plugin.getConfigSettings().defaultParser.getBlockSettings().get(blockType);
                if (parser == null)
                    return;

                ItemStack tool = player.getInventory().getItemInMainHand();
                ToolParser toolParser = parser.getTool(tool.getType());
                if (toolParser == null)
                    return;

                double ticksPerStage = Math.round(toolParser.getTicks() / Math.pow(1.3D, tool.getEnchantmentLevel(Enchantment.DIG_SPEED)) / 9D);
                BukkitRunnable runnable = new BukkitRunnable() {
                    int ticks = 0;

                    @Override
                    public void run() {
                        if (!player.isOnline() || parser.getTool(player.getInventory().getItemInMainHand().getType()) == null) {
                            stopDigging(blockPosition, playerUUID);
                            return;
                        }

                        ticks += 5;
                        int stage = (int) (ticks / ticksPerStage);
                        if (ticksPerStage != 0D && stage <= 9) {
                            broadcastBlockBreakAnimationPacket(blockPosition, stage);
                        } else {
                            breakBlock(location.getBlock(), player);
                            stopDigging(blockPosition, playerUUID);
                        }
                    }
                };
                Executor.sync(plugin, runnable, 0L, 5L);
                players.put(playerUUID, runnable);
            }
        }
    }

    private void stopDigging(BlockPosition blockPosition, UUID uuid) {
        if (!players.containsKey(uuid))
            return;

        players.remove(uuid).cancel();
        Executor.sync(plugin, (runnable) -> broadcastBlockBreakAnimationPacket(blockPosition, -1), 1L);
    }

    private void breakBlock(Block block,Player player) {
        BlockBreakEvent breakEvt = new BlockBreakEvent(block, player);
        plugin.getServer().getPluginManager().callEvent(breakEvt);
        if (breakEvt.isCancelled())
            return;

        Material blockType = block.getType();
        BlockSettingsParser parser = plugin.getConfigSettings().defaultParser.getBlockSettings().get(block.getType());
        if (parser == null)
            return;

        ItemStack hand = player.getInventory().getItemInMainHand();
        Material toolType = hand.getType();
        ToolParser tool = parser.getTool(toolType);
        if (tool == null)
            return;

        Optional<String> permission = tool.getPermission();
        if (permission.isEmpty() || !player.hasPermission(permission.get())) {
            Lang.NO_PERMISSION.send(player);
            return;
        }

        Logging.debug(plugin, "PacketListener#breakBlock:" +  block.getType());
        if (tool.isDrop()) {
            if (block.getDrops().isEmpty())
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(blockType, 1));

            block.breakNaturally();
        } else if (tool.isAddToInventory()) {
            block.setType(Material.AIR);
            player.getInventory().addItem(new ItemStack(blockType, 1));
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 0.5f);
        }

        if (!(hand.getItemMeta() instanceof Damageable damageable))
            return;

        int calculatedDamage = damageable.getDamage() + tool.getDamage();
        damageable.setDamage(calculatedDamage);
        hand.setItemMeta(damageable);
        if (calculatedDamage >= hand.getType().getMaxDurability()) {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            Location location = player.getLocation();
            player.playSound(location, Sound.ENTITY_ITEM_BREAK, 0.5f, 0.5f);
            location.getWorld().spawnParticle(Particle.ITEM_CRACK, location,1);
        }
    }

    private void broadcastBlockBreakAnimationPacket(BlockPosition position, int stage) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
        packet.getIntegers().write(0, 0).write(1, stage);
        packet.getBlockPositionModifier().write(0, position);
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(packet);
    }

}