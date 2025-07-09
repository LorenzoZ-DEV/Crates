    package dev.lorenz.crates.bootstrap;

    import com.github.retrooper.packetevents.PacketEvents;
    import dev.lorenz.crates.application.crates.VirtualKeyManager;
    import dev.lorenz.crates.application.hooks.CratePlaceholder;
    import dev.lorenz.crates.application.manager.ManagerService;
    import dev.lorenz.crates.commands.CrateCommand;
    import dev.lorenz.crates.commands.CrateWizardCommand;
    import dev.lorenz.crates.infra.utils.CC;
    import dev.lorenz.crates.infra.utils.ConfigFile;
    import dev.lorenz.crates.infra.sql.DatabaseManager;
    import dev.lorenz.crates.listeners.CrateInteractListener;
    import lombok.Getter;
    import org.bukkit.Bukkit;
    import org.bukkit.plugin.java.JavaPlugin;
    import revxrsal.commands.bukkit.BukkitLamp;

    import java.nio.file.Path;
    import java.util.List;

    public class CratePlugin
    {
        @Getter
        private static CratePlugin INSTANCE;
        @Getter
        private JavaPlugin plugin;
        @Getter
        private ManagerService service;
        @Getter
        private ConfigFile configFile, messagesFile, storageFile, crateFile;
        @Getter
        private final DatabaseManager databaseManager = new DatabaseManager();

        public CratePlugin(JavaPlugin plugin) {
            this.plugin = plugin;
        }
        public void start() {
            if (INSTANCE == null) {
                INSTANCE = this;
                CC.info("&aAvvio del plugin Crates...");

                registerConfig();
                databaseManager.start(storageFile);
                registerService();
                hookPacketEvents();
                registerCommands();
                registerListeners();
                registerHooks();

                CC.info("&aPlugin Crates avviato con successo.");
            }
        }



        public void stop() {
            if (INSTANCE != null) {
                databaseManager.stop();
                service.shutdown();

                if (configFile != null) configFile.saveFile();
                if (messagesFile != null) messagesFile.saveFile();
                if (storageFile != null) storageFile.saveFile();
                if (crateFile != null) crateFile.saveFile();

                PacketEvents.getAPI().terminate();
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
            this.storageFile = new ConfigFile(plugin, "storage.yml");
            this.crateFile = new ConfigFile(plugin, "crates.yml");
        }
        private void registerService() {
            CC.info("Registering service...");
            this.service = new ManagerService();
            this.service.init ();
        }
        private void registerListeners() {
            CC.info("Registering listeners...");
            List.of (
                    new CrateInteractListener ()
            ).forEach (listener -> plugin.getServer ().getPluginManager().registerEvents(listener, plugin));
        }

        private void registerCommands() {
            var lamp = BukkitLamp.builder(this.getPlugin()).build();

            VirtualKeyManager keyManager = CratePlugin.getINSTANCE()
                    .getService()
                    .get(VirtualKeyManager.class);

            lamp.register(
                    new CrateCommand(keyManager),
                    new CrateWizardCommand()
            );
        }

        private void hookPacketEvents(){

            try{
                CC.info("Hooking PacketEvents...");
                PacketEvents.getAPI().init();
            } catch (Exception e) {
                CC.line ();
                CC.error("Si e verificato un problema durante l'hook di PacketEvents : ");
                e.printStackTrace();
                CC.line ();
                PacketEvents.getAPI().terminate();
                Bukkit.getServer().getPluginManager().disablePlugin(this.plugin);
            }

        }

    }
