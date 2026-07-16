package us.crazycrew.crazycrates.api.storage;

import org.jspecify.annotations.NullMarked;
import us.crazycrew.crazycrates.api.interfaces.ICrateLocation;
import us.crazycrew.crazycrates.api.interfaces.ICrateWorld;
import us.crazycrew.crazycrates.api.storage.enums.DataState;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@NullMarked
public abstract class IStorageHolder<CL extends ICrateLocation, W extends ICrateWorld<CL>> {

    public abstract IStorageHolder init();

    public abstract boolean tableExists(final String table);

    public abstract DataState addLocation(final UUID world, final UUID crate, final int x, final int y, final int z);

    public abstract DataState removeLocation(final UUID location);

    public abstract boolean hasWorld(final W world);

    public abstract void addWorld(final W world);

    public abstract void stop();

    protected List<String> getTables(final Connection connection) throws SQLException {
        final List<String> tables = new ArrayList<>();

        try (final ResultSet result = connection.getMetaData().getTables(connection.getCatalog(), null, "%", null)) {
            while (result.next()) {
                tables.add(result.getString(3).toLowerCase(Locale.ROOT));
            }
        }

        return tables;
    }
}