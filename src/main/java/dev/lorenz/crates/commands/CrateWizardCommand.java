package dev.lorenz.crates.commands;

import dev.lorenz.crates.bootstrap.CratePlugin;
import dev.lorenz.crates.application.crates.CrateManager;
import dev.lorenz.crates.application.model.CrateCreationSession;
import dev.lorenz.crates.infra.utils.CC;
import dev.lorenz.crates.infra.utils.ConfigFile;
import revxrsal.commands.annotation.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.*;

@CommandPermission("crateadmin.use")
@Command("crateadmin")
public class CrateWizardCommand {

    private static final Map<UUID, CrateCreationSession> sessions = new HashMap<>();

    public void start(Player sender) {
        sessions.put(sender.getUniqueId(), new CrateCreationSession());
        sender.sendMessage(CC.translate("&aModalità creazione crate avviata! Usa /crateadmin help per i comandi."));
    }

    @Subcommand("help")
    public void help(Player sender) {
        sender.sendMessage(CC.translate("&6Comandi crateadmin disponibili:"));
        sender.sendMessage(CC.translate("&e/crateadmin start &7- Avvia creazione crate"));
        sender.sendMessage(CC.translate("&e/crateadmin setid <id> &7- Imposta ID crate"));
        sender.sendMessage(CC.translate("&e/crateadmin setblock &7- Imposta blocco crate tenendo un blocco in mano"));
        sender.sendMessage(CC.translate("&e/crateadmin addhologram <line> &7- Aggiungi linea all'hologram"));
        sender.sendMessage(CC.translate("&e/crateadmin clearhologram &7- Pulisci linee hologram"));
        sender.sendMessage(CC.translate("&e/crateadmin addreward <reward> &7- Aggiungi ricompensa (stringa di esempio)"));
        sender.sendMessage(CC.translate("&e/crateadmin listrewards &7- Lista ricompense"));
        sender.sendMessage(CC.translate("&e/crateadmin clearrewards &7- Pulisci ricompense"));
        sender.sendMessage(CC.translate("&e/crateadmin setanimation <animation> &7- Imposta animazione crate"));
        sender.sendMessage(CC.translate("&e/crateadmin clearanimation &7- Rimuovi animazione"));
        sender.sendMessage(CC.translate("&e/crateadmin save &7- Salva crate"));
        sender.sendMessage(CC.translate("&e/crateadmin cancel &7- Annulla creazione"));
    }

    @Subcommand("start")
    public void startCmd(Player sender) {
        start(sender);
    }

    @Subcommand("setid")
    public void setId(Player sender, String id) {
        CrateCreationSession session = getSession(sender);
        session.setId(id.toLowerCase(Locale.ROOT));
        sender.sendMessage(CC.translate("&aID crate impostato a &f" + id));
    }

    @Subcommand("setblock")
    public void setBlock(Player sender) {
        CrateCreationSession session = getSession(sender);
        ItemStack inHand = sender.getInventory().getItemInMainHand();

        if (inHand == null || inHand.getType() == Material.AIR) {
            sender.sendMessage(CC.translate("&cDevi tenere in mano un blocco valido."));
            return;
        }

        session.setBlock(inHand.getType().name());
        sender.sendMessage(CC.translate("&aBlocco crate impostato a &f" + inHand.getType().name()));
    }

    @Subcommand("addhologram")
    public void addHologramLine(Player sender, String line) {
        CrateCreationSession session = getSession(sender);
        session.addHologramLine(line);
        sender.sendMessage(CC.translate("&aLinea aggiunta all'hologram: &f" + line));
    }

    @Subcommand("clearhologram")
    public void clearHologram(Player sender) {
        CrateCreationSession session = getSession(sender);
        session.clearHologram();
        sender.sendMessage(CC.translate("&aLinee hologram pulite."));
    }

    @Subcommand("addreward")
    public void addReward(Player sender, String reward) {
        CrateCreationSession session = getSession(sender);
        session.addReward(reward);
        sender.sendMessage(CC.translate("&aRicompensa aggiunta: &f" + reward));
    }

    @Subcommand("listrewards")
    public void listRewards(Player sender) {
        CrateCreationSession session = getSession(sender);
        if (session.getRewards().isEmpty()) {
            sender.sendMessage(CC.translate("&cNessuna ricompensa aggiunta."));
            return;
        }
        sender.sendMessage(CC.translate("&6Ricompense crate:"));
        int i = 1;
        for (String reward : session.getRewards()) {
            sender.sendMessage(CC.translate("&7#" + i + " &f" + reward));
            i++;
        }
    }

    @Subcommand("clearrewards")
    public void clearRewards(Player sender) {
        CrateCreationSession session = getSession(sender);
        session.clearRewards();
        sender.sendMessage(CC.translate("&aRicompense pulite."));
    }

    @Subcommand("setanimation")
    public void setAnimation(Player sender, String animation) {
        CrateCreationSession session = getSession(sender);
        session.setAnimation(animation);
        sender.sendMessage(CC.translate("&aAnimazione impostata: &f" + animation));
    }

    @Subcommand("clearanimation")
    public void clearAnimation(Player sender) {
        CrateCreationSession session = getSession(sender);
        session.clearAnimation();
        sender.sendMessage(CC.translate("&aAnimazione rimossa."));
    }

    @Subcommand("save")
    public void save(Player sender) {
        CrateCreationSession session = getSession(sender);
        if (!session.isComplete()) {
            sender.sendMessage(CC.translate("&cCompleta tutti i campi (id e blocco) prima di salvare."));
            return;
        }

        ConfigFile config = CratePlugin.getINSTANCE().getCrateFile();
        String path = "crates." + session.getId();

        config.set(path + ".display-name", "&f" + session.getId());
        config.set(path + ".block", session.getBlock());
        config.set(path + ".hologram", session.getHologram());
        config.set(path + ".animation", session.getAnimation());
        config.set(path + ".rewards", session.getRewards());
        config.saveFile();

        CrateManager manager = CratePlugin.getINSTANCE().getService().get(CrateManager.class);
        if (manager != null) {
            manager.stop();
            manager.start();
        }

        sender.sendMessage(CC.translate("&aCrate salvata con successo!"));
        sessions.remove(sender.getUniqueId());
    }

    @Subcommand("cancel")
    public void cancel(Player sender) {
        sessions.remove(sender.getUniqueId());
        sender.sendMessage(CC.translate("&cCreazione crate annullata."));
    }

    private CrateCreationSession getSession(Player player) {
        return sessions.computeIfAbsent(player.getUniqueId(), uuid -> new CrateCreationSession());
    }
}
