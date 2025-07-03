package dev.lorenz.crates;

import dev.lorenz.crates.application.CratePlaceholder;
import dev.lorenz.crates.application.manager.ManagerService;
import dev.lorenz.crates.infra.CC;
import dev.lorenz.crates.infra.ConfigFile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CratePlugin
{
    @Getter
    private CratePlugin INSTANCE;
    private JavaPlugin plugin;
    private ManagerService service;

    private ConfigFile configFile, messagesFile,cratesSettingsFile, cratesFile, cratesRewardsFile, cratesKeysFile, cratesCratesFile;

    public CratePlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    public void start() {
        if (INSTANCE == null) {
            INSTANCE = this;
            CC.info("&aAvvio del plugin Crates...");
            registerService ();
            registerConfig();
            registerListeners();
            registerHooks();
            CC.info("&aPlugin Crates avviato con successo.");
        }
    }

    public void stop() {
        if (INSTANCE != null) {
            this.service.shutdown();
            INSTANCE = null;

        }
    }
    private void registerHooks() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new CratePlaceholder (this.plugin).register();
            CC.info("&7[&aOK&7] &fHookked to &5PlaceholderAPI.");
        } else {
            CC.warning("&7[&cERROR&7] &fPlaceholderAPI not found. Some features may not work.");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("DecentHolograms")) {
            CC.info("&7[&aOK&7] &FHookked to &bDecentHolograms.");
        } else{
            CC.warning("&7[&cERROR&7] &fDecentHolograms not found. Some features may not work.");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            CC.info("&7[&aOK&7] &fProtocolLib Found. Using it for crate animations.");
        } else {
            CC.warning("&7[&cERROR&7] &fProtocolLib not found. Some features may not work.");
        }

    }
    private void registerConfig(){
        this.configFile = new ConfigFile(plugin, "config.yml");
        this.messagesFile = new ConfigFile(plugin, "messages.yml");
        this.cratesSettingsFile = new ConfigFile(plugin, "crates/settings.yml");
        this.cratesFile = new ConfigFile(plugin, "crates/crates.yml");
        this.cratesRewardsFile = new ConfigFile(plugin, "crates/rewards.yml");
        this.cratesKeysFile = new ConfigFile(plugin, "crates/keys.yml");
        this.cratesCratesFile = new ConfigFile(plugin, "crates/crates.yml");
    }
    private void registerService() {
        CC.info("Registering service...");
        this.service = new ManagerService();
        this.service.init ();
    }
    private void registerListeners() {
        CC.info("Registering listeners...");
        List.of (
                // listeners da registrare
        ).forEach (listener -> plugin.getServer ().getPluginManager().registerEvents(listener, plugin));
    }


}
