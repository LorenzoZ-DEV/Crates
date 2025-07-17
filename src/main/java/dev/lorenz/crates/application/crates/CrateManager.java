package dev.lorenz.crates.application.crates;

import dev.lorenz.crates.bootstrap.CratePlugin;
import dev.lorenz.crates.infra.model.Crate;
import dev.lorenz.crates.application.manager.Manager;
import dev.lorenz.crates.infra.model.ItemReward;
import dev.lorenz.crates.infra.model.Reward;
import dev.lorenz.crates.infra.utils.CC;
import dev.lorenz.crates.infra.utils.ConfigFile;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        ConfigFile configFile = CratePlugin.getINSTANCE().getCrateFile();

        if (!configFile.contains("crates")) {
            CC.warning("No crates found in crates.yml");
            return;
        }

        for (String id : configFile.getConfigurationSection("crates").getKeys(false)) {
            String path = "crates." + id;

            // Carica i rewards da ConfigurationSection
            var rewardsSection = configFile.getConfigurationSection(path + ".rewards");
            List<Reward> rewards = new ArrayList<> ();
            if (rewardsSection != null) {
                for (String key : rewardsSection.getKeys(false)) {
                    String rewardPath = path + ".rewards." + key;
                    int chance = configFile.getInt(rewardPath + ".chance", 1);
                    ItemStack item = configFile.getItemStack(rewardPath + ".item");
                    if (item != null) {
                        rewards.add(new ItemReward (item, chance));
                    } else {
                        CC.warning("Invalid item in reward " + key + " of crate " + id);
                    }
                }
            }

            Crate crate = new Crate(
                    id,
                    configFile.getString(path + ".display-name"),
                    configFile.getString(path + ".block"),
                    configFile.getStringList(path + ".hologram"),
                    configFile.getConfigurationSection(path + ".animation"),
                    rewards
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
