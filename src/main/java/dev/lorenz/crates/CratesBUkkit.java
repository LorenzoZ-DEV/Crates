package dev.lorenz.crates;

import org.bukkit.plugin.java.JavaPlugin;

public final class CratesBUkkit extends JavaPlugin {

    private CratePlugin provider;
    @Override
    public void onEnable() {

        this.provider = new CratePlugin (this);
        this.provider.start();

    }

    @Override
    public void onDisable() {
        if (this.provider != null) {
            this.provider.stop();
            this.provider = null;
        }
    }
}
