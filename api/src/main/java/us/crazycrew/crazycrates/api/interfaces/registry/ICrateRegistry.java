package us.crazycrew.crazycrates.api.interfaces.registry;

import org.jspecify.annotations.NullMarked;
import us.crazycrew.crazycrates.api.interfaces.ICrateWorld;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public interface ICrateRegistry<E extends ICrateWorld> {

    void addWorld(final E world);

    void removeWorld(final UUID identifier);

    boolean hasWorld(final UUID identifier);

    Optional<E> getWorld(final UUID identifier);

}