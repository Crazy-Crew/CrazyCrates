package us.crazycrew.crazycrates.paper.api.crates;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.CrateLocation;
import org.bukkit.Location;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CrateManager {

    // All the crates that have been loaded.
    private final ArrayList<Crate> crates = new ArrayList<>();
    private final ArrayList<CrateLocation> crateLocations = new ArrayList<>();

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

    /**
     * Checks to see if the location is a physical crate.
     *
     * @param location you are checking.
     * @return true if it is a physical crate and false if not.
     */
    public boolean isCrateLocation(Location location) {
        for (CrateLocation crateLocation : getCrateLocations()) {
            if (crateLocation.getLocation().equals(location)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the physical crate of the location.
     *
     * @param location you are checking.
     * @return a crate location if the location is a physical crate otherwise null if not.
     */
    public CrateLocation getCrateLocation(Location location) {
        for (CrateLocation crateLocation : this.crateLocations) {
            if (crateLocation.getLocation().equals(location)) {
                return crateLocation;
            }
        }

        return null;
    }

    /**
     * @return An unmodifiable list of crate locations.
     */
    public List<CrateLocation> getCrateLocations() {
        return Collections.unmodifiableList(this.crateLocations);
    }
}