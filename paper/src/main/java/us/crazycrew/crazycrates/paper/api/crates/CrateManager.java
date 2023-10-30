package us.crazycrew.crazycrates.paper.api.crates;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CrateManager {

    // All the crates that have been loaded.
    private final ArrayList<Crate> crates = new ArrayList<>();

    /**
     * Nukes all data.
     */
    public void purge() {
        this.crates.clear();
    }

    /**
     * Adds a crate to the arraylist
     *
     * @param crate object
     */
    public void addCrate(Crate crate) {
        if (!hasCrate(crate)) this.crates.add(crate);
    }

    /**
     * Removes a crate from the arraylist
     *
     * @param crate object
     */
    public void removeCrate(Crate crate) {
        if (hasCrate(crate)) this.crates.remove(crate);
    }

    /**
     * @return true if the arraylist has a crate object otherwise false
     */
    public boolean hasCrate(Crate crate) {
        return this.crates.contains(crate);
    }

    /**
     * @return An unmodifiable list of crate objects.
     */
    public List<Crate> getCrates() {
        return Collections.unmodifiableList(this.crates);
    }

    /**
     * Gets a crate object using the crate name.
     *
     * @param name of the crate
     * @return crate object
     */
    public Crate getCrateFromName(String name) {
        for (Crate crate : this.crates) {
            if (crate.getName().equalsIgnoreCase(name)) {
                return crate;
            }
        }

        return null;
    }
}