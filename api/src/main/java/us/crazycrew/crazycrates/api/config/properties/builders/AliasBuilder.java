package us.crazycrew.crazycrates.api.config.properties.builders;

import org.jspecify.annotations.NullMarked;
import java.util.HashMap;
import java.util.Map;

@NullMarked
public final class AliasBuilder {

    private final Map<String, Object[]> aliases = new HashMap<>();

    public void addAlias(final String alias, final Object... path) {
        this.aliases.putIfAbsent(alias, path);
    }

    public Object[] getAlias(final String alias) {
        return this.aliases.get(alias);
    }

    public boolean hasAlias(final String alias) {
        return this.aliases.containsKey(alias);
    }
}