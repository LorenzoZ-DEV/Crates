package dev.lorenz.crates.infra.model;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

@Getter
public class Crate {

    private final String id;
    private final String displayName;
    private final String block;
    private final List<String> hologram;
    private final ConfigurationSection animationSection;
    private final ConfigurationSection rewardsSection;

    public Crate(String id, String displayName, String block, List<String> hologram,
                 ConfigurationSection animationSection,
                 ConfigurationSection rewardsSection) {
        this.id = id;
        this.displayName = displayName;
        this.block = block;
        this.hologram = hologram;
        this.animationSection = animationSection;
        this.rewardsSection = rewardsSection;
    }

}

