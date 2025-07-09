package dev.lorenz.crates.application.manager;

import dev.lorenz.crates.application.crates.CrateManager;
import dev.lorenz.crates.application.crates.VirtualKeyManager;
import dev.lorenz.crates.infra.stats.StatsManager;

import java.util.ArrayList;
import java.util.List;

public class ManagerService {

    private static ManagerService INSTANCE;
    private List<Manager> managers;

    public ManagerService() {
        INSTANCE = this;
    }

    public void init() {
        this.managers = new ArrayList<>();
        registerManager();
    }

    public void registerManager() {
        addManager(new CrateManager());
        addManager(new VirtualKeyManager());
        addManager(new StatsManager());
        managers.forEach(Manager::start);
    }

    public void shutdown() {
        managers.forEach(Manager::stop);
        managers.clear();
        INSTANCE = null;
    }

    private void addManager(Manager manager) {
        if (managers.contains(manager)) return;
        managers.add(manager);
    }

    public <T extends Manager> T get(Class<T> clazz) {
        return managers.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst()
                .orElse(null);
    }

    public static ManagerService get() {
        return INSTANCE;
    }
}
