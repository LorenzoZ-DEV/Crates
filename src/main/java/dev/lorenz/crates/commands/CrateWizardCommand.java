package dev.lorenz.crates.commands;

import dev.lorenz.crates.bootstrap.CratePlugin;
import dev.lorenz.crates.application.crates.CrateManager;
import dev.lorenz.crates.application.model.CrateCreationSession;
import dev.lorenz.crates.infra.utils.CC;
import dev.lorenz.crates.infra.utils.ConfigFile;
import dev.lorenz.crates.infra.model.Crate;
import revxrsal.commands.annotation.*;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Command("crate wizard")
public class CrateWizardCommand {

    private static final Map<UUID, CrateCreationSession> sessions = new HashMap<>();

    public void start(Player sender) {
        sessions.put(sender.getUniqueId(), new CrateCreationSession());
        sender.sendMessage(CC.translate("&aModalità creazione crate avviata!"));
    }

    @Subcommand("setid")
    public void setId(Player sender, String id) {
        CrateCreationSession session = getSession(sender);
        session.setId(id);
        sender.sendMessage(CC.translate("&aID impostato a &f" + id));
    }

    @Subcommand("setblock")
    public void setBlock(Player sender) {
        CrateCreationSession session = getSession(sender);
        ItemStack inHand = sender.getInventory().getItemInMainHand();

        if (inHand == null || inHand.getType() == Material.AIR) {
            sender.sendMessage(CC.translate("&cTieni in mano un blocco valido."));
            return;
        }

        session.setBlock(inHand.getType().name());
        sender.sendMessage(CC.translate("&aBlocco impostato a &f" + inHand.getType().name()));
    }

    @Subcommand("sethologram")
    public void setHologram(Player sender, String line) {
        CrateCreationSession session = getSession(sender);
        session.addHologramLine(line);
        sender.sendMessage(CC.translate("&aAggiunta linea hologram: &f" + line));
    }

    @Subcommand("save")
    public void save(Player sender) {
        CrateCreationSession session = getSession(sender);
        if (!session.isComplete()) {
            sender.sendMessage(CC.translate("&cCompleta tutti i campi prima di salvare."));
            return;
        }

        ConfigFile config = CratePlugin.getINSTANCE().getCrateFile ();
        String path = "crates." + session.getId();

        config.set(path + ".display-name", "&f" + session.getId());
        config.set(path + ".block", session.getBlock());
        config.set(path + ".hologram", session.getHologram());
        config.set(path + ".animation", null); // Placeholder
        config.set(path + ".rewards", null); // Placeholder
        config.saveFile ();

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
