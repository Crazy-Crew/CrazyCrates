package us.crazycrew.crazycrates.paper.api.users.guis;

import java.util.ArrayList;
import java.util.UUID;

public class InventoryManager {

    private final ArrayList<UUID> inventoryViewers = new ArrayList<>();

    public void addInventoryViewer(UUID uuid) {
        this.inventoryViewers.add(uuid);
    }

    public void removeInventoryViewer(UUID uuid) {
        this.inventoryViewers.remove(uuid);
    }

    public ArrayList<UUID> getInventoryViewers() {
        return this.inventoryViewers;
    }
}