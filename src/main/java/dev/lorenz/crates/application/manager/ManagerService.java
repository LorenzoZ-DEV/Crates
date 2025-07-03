package dev.lorenz.crates.application.manager;

import dev.lorenz.crates.application.crates.CrateManager;

import java.util.ArrayList;
import java.util.List;

public class ManagerService

{
    private static ManagerService INSTANCE;
    private List<Manager> managers;

    public ManagerService() {
        INSTANCE = this;
    }

    public void init() {
        this.managers = new ArrayList<> ();
        registerManager();
    }

    public void registerManager() {
        addManager ( new CrateManager () );
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

    public static ManagerService get(Class<CrateManager> crateManagerClass) {
        return INSTANCE;
    }
}
