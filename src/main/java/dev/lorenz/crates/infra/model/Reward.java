package dev.lorenz.crates.infra.model;

import org.bukkit.entity.Player;

public interface Reward {
    int getChance();
    void give(Player player);
}
