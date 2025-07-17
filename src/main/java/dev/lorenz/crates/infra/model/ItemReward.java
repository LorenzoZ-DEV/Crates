package dev.lorenz.crates.infra.model;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class ItemReward implements Reward {
    private final ItemStack item;
    private final int chance;

    public ItemReward(ItemStack item, int chance) {
        this.item = item;
        this.chance = chance;
    }

    @Override
    public void give(Player player) {
        player.getInventory().addItem(item);
    }
}
