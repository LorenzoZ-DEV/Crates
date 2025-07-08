package dev.lorenz.crates.application.model;

import java.util.ArrayList;
import java.util.List;

public class CrateCreationSession {
    private String id;
    private String block;
    private List<String> hologram = new ArrayList<> ();

    public void setId(String id) { this.id = id; }
    public void setBlock(String block) { this.block = block; }
    public void addHologramLine(String line) { this.hologram.add(line); }

    public String getId() { return id; }
    public String getBlock() { return block; }
    public List<String> getHologram() { return hologram; }

    public boolean isComplete() {
        return id != null && block != null && !hologram.isEmpty();
    }
}
