package dev.lorenz.crates.commands;

import dev.lorenz.crates.bootstrap.CratePlugin;
import dev.lorenz.crates.application.crates.VirtualKeyManager;
import dev.lorenz.crates.application.stats.LeaderboardEntry;
import dev.lorenz.crates.infra.stats.StatsManager;
import dev.lorenz.crates.infra.utils.CC;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

@Command({"crate", "crates", "lcrate", "coralcrate"})
@CommandPermission("crates.admin")
public class CrateCommand {

    private final VirtualKeyManager keyManager;

    public CrateCommand(VirtualKeyManager keyManager) {
        this.keyManager = keyManager;
    }

    public void execute(Player sender) {
        sender.sendMessage(CC.translate("&6&lCrates &7- &fLista comandi disponibili:"));
        sender.sendMessage(CC.translate("&e/crate givekey <player> <crate> <amount> [--virtual] &7- Dai chiavi crate (fisiche o virtuali)"));
        sender.sendMessage(CC.translate("&e/crate reload &7- Ricarica le configurazioni"));
        sender.sendMessage(CC.translate("&e/crate stats <player> &7- Mostra le statistiche di un player"));
        sender.sendMessage(CC.translate("&e/crate leaderboard <crate> <page> &7- Mostra leaderboard (PlaceholderAPI)"));
    }

    @Subcommand("givekey")
    public void giveKey(Player sender, Player target, String crateId, int amount, @Optional String flag) {
        boolean virtual = flag != null && flag.equalsIgnoreCase("--virtual");

        if (virtual) {
            keyManager.addKeys(target.getUniqueId(), crateId.toLowerCase(), amount);
            sender.sendMessage(CC.translate("&aHai dato &f" + amount + " &achiavi virtuali di &e" + crateId + " &aa &f" + target.getName()));
            target.sendMessage(CC.translate("&aHai ricevuto &f" + amount + " &achiavi virtuali di &e" + crateId));
        } else {
            ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK);
            key.setAmount(amount);
            // Qui si potrebbe aggiungere lore e nome chiave con crateId
            target.getInventory().addItem(key);
            sender.sendMessage(CC.translate("&aHai dato &f" + amount + " &achiavi fisiche di &e" + crateId + " &aa &f" + target.getName()));
            target.sendMessage(CC.translate("&aHai ricevuto &f" + amount + " &achiavi fisiche di &e" + crateId));
        }
    }

    @Subcommand("reload")
    public void reload(Player sender) {
        CratePlugin.getINSTANCE().getMessagesFile().reload();
        CratePlugin.getINSTANCE().getConfigFile().reload();
        CratePlugin.getINSTANCE().getStorageFile().reload();
        sender.sendMessage(CC.translate("&aConfigurazioni ricaricate con successo."));
    }

    @Subcommand("stats")
    public void stats(Player sender, Player target) {
        StatsManager statsManager = CratePlugin.getINSTANCE().getService().get(StatsManager.class);
        if (statsManager == null) {
            sender.sendMessage(CC.translate("&cGestore statistiche non disponibile."));
            return;
        }
        int openedCrates = statsManager.getOpenedCrates(target.getUniqueId());
        int keysUsed = statsManager.getKeysUsed(target.getUniqueId());
        sender.sendMessage(CC.translate("&6Statistiche di &f" + target.getName() + ":"));
        sender.sendMessage(CC.translate("&7- Crate aperte: &e" + openedCrates));
        sender.sendMessage(CC.translate("&7- Chiavi usate: &e" + keysUsed));
    }

    @Subcommand("leaderboard")
    public void leaderboard(Player sender, String crateId, @Optional Integer page) {
        int p = (page == null) ? 1 : page;
        String placeholder = "%crates_leaderboard_" + crateId.toLowerCase() + "_" + p + "_1%";
        String output = PlaceholderAPI.setPlaceholders(sender, placeholder);
        sender.sendMessage(CC.translate("&6Leaderboard di crate &e" + crateId + " &7pagina &e" + p));
        sender.sendMessage(CC.translate(output));
    }
}
