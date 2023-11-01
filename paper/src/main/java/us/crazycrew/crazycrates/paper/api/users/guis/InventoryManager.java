package us.crazycrew.crazycrates.paper.api.users.guis;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InventoryManager {

    private final HashMap<UUID, Boolean> viewersInMenu = new HashMap<>();

    public Map<UUID, Boolean> getViewersInMenu() {
        return Collections.unmodifiableMap(this.viewersInMenu);
    }

    private final HashMap<UUID, Integer> viewerPages = new HashMap<>();

    private final HashMap<UUID, Crate> viewerCrates = new HashMap<>();

    public void nextPage(UUID uuid) {
        setPage(uuid, getPage(uuid) + 1);
    }

    public void backPage(UUID uuid) {
        setPage(uuid, getPage(uuid) - 1);
    }

    public int getPage(UUID uuid) {
        return this.viewerPages.getOrDefault(uuid, 1);
    }

    public void setPage(UUID uuid, int page) {
        int max = this.viewerCrates.get(uuid).getMaxPage();

        if (page < 1) {
            page = 1;
        } else if (page >= max) {
            page = max;
        }

        this.viewerPages.put(uuid, page);
    }

    public Map<UUID, Integer> getPages() {
        return Collections.unmodifiableMap(this.viewerPages);
    }

    public Map<UUID, Crate> getViewerCrates() {
        return Collections.unmodifiableMap(this.viewerCrates);
    }

    private final ArrayList<UUID> viewers = new ArrayList<>();

    public void addViewer(UUID uuid) {
        this.viewers.add(uuid);
    }

    public void removeViewer(UUID uuid) {
        this.viewers.remove(uuid);
    }

    public List<UUID> getViewers() {
        return Collections.unmodifiableList(this.viewers);
    }
}