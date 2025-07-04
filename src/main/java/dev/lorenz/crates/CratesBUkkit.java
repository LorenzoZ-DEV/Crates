package dev.lorenz.crates;

import dev.lorenz.crates.infra.CC;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CratesBUkkit extends JavaPlugin {

    private CratePlugin provider;
    @Override
    public void onEnable() {2
        try{
         this.provider = new CratePlugin(this); 
         this.provider.start(); 
        } catch (Exception e) {
            CC.line ();
          CC.error("Si e verificato un problema durante l'avvio del plugin : ");
            e.printStackTrace();
            CC.line ();
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (this.provider != null) {
            this.provider.stop();
            this.provider = null;
        }
    }
}
