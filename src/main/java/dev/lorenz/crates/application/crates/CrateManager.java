package dev.lorenz.crates.application.crates;

import dev.lorenz.crates.CratePlugin;
import dev.lorenz.crates.application.model.Crate;
import dev.lorenz.crates.application.manager.Manager;
import dev.lorenz.crates.infra.CC;
import dev.lorenz.crates.infra.ConfigFile;

import java.util.HashMap;
import java.util.Map;

public class CrateManager implements Manager
{
    private final Map<String, Crate> crates = new HashMap<>();

    @Override
    public void start() {
        loadcrates();
    }
    public void stop() {;
        crates.clear();
    }
    private void loadcrates() {
        ConfigFile configFile = CratePlugin.getINSTANCE ( ).getCratesFile ( );

        if(!configFile.contains ( "crates" )) {
            CC.warning ( "No crates found in crates.yml" );
            return;
        }
        for (String id : configFile.getConfigurationSection("crates").getKeys(false)) {
            String path = "crates." + id;
            Crate crate = new Crate(id,
                    configFile.getString(path + ".display-name"),
                    configFile.getString(path + ".block"),
                    configFile.getStringList(path + ".hologram"),
                    configFile.getConfigurationSection(path + ".animation"),
                    configFile.getConfigurationSection(path + ".rewards")
            );

            crates.put(id.toLowerCase(), crate);
            CC.info("Loaded crate: " + id);
        }
    }
    public Crate getCrate(String id) {
        return crates.get(id.toLowerCase());
    }

    public Map<String, Crate> getAllCrates() {
        return crates;
    }
}
