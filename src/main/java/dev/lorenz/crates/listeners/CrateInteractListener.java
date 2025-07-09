package dev.lorenz.crates.listeners;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import dev.lorenz.crates.application.crates.CrateManager;
import dev.lorenz.crates.application.crates.VirtualKeyManager;
import dev.lorenz.crates.bootstrap.CratePlugin;
import dev.lorenz.crates.infra.model.Crate;
import dev.lorenz.crates.infra.utils.CC;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CrateInteractListener implements Listener {

    private final CrateManager manager;
    private final VirtualKeyManager virtualKeyManager;

    public CrateInteractListener() {
        this.manager = CratePlugin.getINSTANCE().getService().get(CrateManager.class);
        this.virtualKeyManager = CratePlugin.getINSTANCE().getService().get(VirtualKeyManager.class);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasBlock()) return;
        Block block = event.getClickedBlock();
        if (block == null) return;

        Player player = event.getPlayer();
        Material blockType = block.getType();

        Crate crate = manager.getAllCrates().values().stream()
                .filter(c -> Material.matchMaterial(c.getBlock()) == blockType)
                .findFirst().orElse(null);

        if (crate == null) return;

        event.setCancelled(true);

        ItemStack hand = player.getInventory().getItemInMainHand();

        boolean hasPhysicalKey = false;

        if (hand != null && hand.getType() != Material.AIR && hand.hasItemMeta() && hand.getItemMeta().hasDisplayName()) {
            if (hand.getItemMeta().getDisplayName().contains(crate.getDisplayName())) {
                hasPhysicalKey = true;
            }
        }

        if (hasPhysicalKey) {
            hand.setAmount(hand.getAmount() - 1);
            player.sendMessage(CC.translate(
                    CratePlugin.getINSTANCE().getMessagesFile().getString("crates.opening",
                            "&aStai aprendo la crate %crate%").replace("%crate%", crate.getDisplayName())));
            startCrateAnimation(player, block);
            return;
        }

        if (virtualKeyManager != null && virtualKeyManager.hasKeys(player.getUniqueId(), crate.getId(), 1)) {
            virtualKeyManager.removeKeys(player.getUniqueId(), crate.getId(), 1);
            player.sendMessage( CC.translate(
                    CratePlugin.getINSTANCE().getMessagesFile().getString("crates.opening_virtual",
                            "&aStai aprendo la crate %crate% con una chiave virtuale").replace("%crate%", crate.getDisplayName())));
            startCrateAnimation(player, block);
            return;
        }

        player.sendMessage(CC.translate(
                CratePlugin.getINSTANCE().getMessagesFile().getString("crates.no-keys",
                                "&cNon disponi della chiave fisica o virtuale per aprire questa crate %crate%")
                        .replace("%crate%", crate.getDisplayName())));
    }

    private void startCrateAnimation(Player player, Block crateBlock) {
        var originalBlockData = crateBlock.getBlockData();

        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 40;

            @Override
            public void run() {
                if (ticks == 0 || ticks == 20) {
                    Vector3i position = new Vector3i(crateBlock.getX(), crateBlock.getY(), crateBlock.getZ());
                    WrappedBlockState glowstoneState = WrappedBlockState.getByGlobalId( StateTypes.GLOWSTONE.createBlockState ( ).getGlobalId ());
                    int blockId = glowstoneState.getGlobalId ();
                    WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(position, blockId);
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
                } else if (ticks == 10 || ticks == 30) {
                    Vector3i position = new Vector3i(crateBlock.getX(), crateBlock.getY(), crateBlock.getZ());
                    WrappedBlockState originalState = WrappedBlockState.getByString(originalBlockData.getAsString());
                    int blockId = originalState.getGlobalId ();
                    WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(position, blockId);
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
                }
                ticks++;
                if (ticks > maxTicks) this.cancel();
            }
        }.runTaskTimer( (Plugin) CratePlugin.getINSTANCE(), 0L, 1L);
    }
}
