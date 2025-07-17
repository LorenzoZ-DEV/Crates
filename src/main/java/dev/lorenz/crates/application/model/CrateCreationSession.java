package dev.lorenz.crates.application.model;

import java.util.ArrayList;
import java.util.List;

public class CrateCreationSession {

    private String id;
    private String block;
    private final List<String> hologram = new ArrayList<>();
    private final List<String> rewards = new ArrayList<>();
    private String animation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public List<String> getHologram() {
        return hologram;
    }

    public void addHologramLine(String line) {
        hologram.add(line);
    }

    public void clearHologram() {
        hologram.clear();
    }

    public List<String> getRewards() {
        return rewards;
    }

    public void addReward(String reward) {
        rewards.add(reward);
    }

    public void clearRewards() {
        rewards.clear();
    }

    public String getAnimation() {
        return animation;
    }

    public void setAnimation(String animation) {
        this.animation = animation;
    }

    public void clearAnimation() {
        this.animation = null;
    }

    public boolean isComplete() {
        return id != null && !id.isEmpty()
                && block != null && !block.isEmpty();
    }
}
