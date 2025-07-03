package dev.lorenz.crates.application.manager;

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
        // aggiunta del metodo del addmanager

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

    public static ManagerService get() {
        return INSTANCE;
    }
}
