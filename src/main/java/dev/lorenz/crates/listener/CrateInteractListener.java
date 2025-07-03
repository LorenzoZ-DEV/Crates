package dev.lorenz.crates.listener;

import dev.lorenz.crates.CratePlugin;
import dev.lorenz.crates.application.crates.CrateManager;
import dev.lorenz.crates.application.model.Crate;
import dev.lorenz.crates.infra.CC;
import dev.lorenz.crates.infra.ConfigFile;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CrateInteractListener implements Listener
{

    private final CrateManager manager;
    public CrateInteractListener() {
        this.manager = CratePlugin.getINSTANCE().getService().get(CrateManager.class);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(!event.hasBlock ( )) return;
        Block block = event.getClickedBlock ( );
        if(block == null) return;
        Player player = event.getPlayer ( );
        Material blockType = block.getType ( );
        Crate crate = manager.getAllCrates().values().stream()
                .filter(c -> Material.matchMaterial(c.getBlock()) == blockType)
                .findFirst().orElse(null);

        if (crate == null) return;

        event.setCancelled(true);
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType() == Material.AIR) {
            player.sendMessage( CC.translate ( CratePlugin.getINSTANCE ().getMessagesFile ().getString ( "crates.no-crate","&cNon disponi della crate %crate%" ).replace ( "%crate%",crate.getDisplayName () ) ));
            return;
        }
        if (!hand.hasItemMeta() || !hand.getItemMeta().hasDisplayName()) {
            player.sendMessage( CC.translate ( CratePlugin.getINSTANCE ().getMessagesFile ().getString ( "crates.crate-invalid","&cSembra che questa crate non sia valida, per favore apri un ticket su Discord.") ));
            return;
        }

        if (!hand.getItemMeta().getDisplayName().contains(crate.getDisplayName())) {
            player.sendMessage(CC.translate ( CratePlugin.getINSTANCE ().getMessagesFile ().getString ( "crates.crate-wrong","&cQuesta crate non è quella giusta, controlla bene!" ) ));
            return;
        }
        hand.setAmount(hand.getAmount() - 1);
        player.sendMessage ( CratePlugin.getINSTANCE ().getMessagesFile ().getString ( "crates.opening","&aStai aprendo la crate %crate%" ).replace ( "%crate%",crate.getDisplayName () ) );
        //TODO Implementare le animazioni di apertura della crate
    }
}
