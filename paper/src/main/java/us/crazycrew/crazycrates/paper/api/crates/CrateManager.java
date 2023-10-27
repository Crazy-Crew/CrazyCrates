package us.crazycrew.crazycrates.paper.api.crates;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CrateManager {

    // All the crates that have been loaded.
    private final ArrayList<Crate> crates = new ArrayList<>();

    public void addCrate(Crate crate) {
        if (!hasCrate(crate)) this.crates.add(crate);
    }

    public void removeCrate(Crate crate) {
        if (hasCrate(crate)) this.crates.remove(crate);
    }

    public boolean hasCrate(Crate crate) {
        return this.crates.contains(crate);
    }

    public void clearCrates() {
        if (!this.crates.isEmpty()) this.crates.clear();
    }

    public List<Crate> getCrates() {
        return Collections.unmodifiableList(this.crates);
    }

    public Crate getCrateFromName(String name) {
        for (Crate crate : getCrates()) {
            if (crate.getName().equalsIgnoreCase(name)) {
                return crate;
            }
        }

        return null;
    }
}