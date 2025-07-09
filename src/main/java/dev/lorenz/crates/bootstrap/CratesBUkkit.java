package dev.lorenz.crates.bootstrap;

import com.github.retrooper.packetevents.PacketEvents;
import dev.lorenz.crates.infra.utils.CC;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CratesBUkkit extends JavaPlugin {

    private CratePlugin provider;
    @Override
    public void onLoad() {
        PacketEvents.setAPI( SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }
    @Override
    public void onEnable() {
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
