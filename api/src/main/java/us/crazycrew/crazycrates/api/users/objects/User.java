package us.crazycrew.crazycrates.api.users.objects;

import net.kyori.adventure.text.Component;
import java.util.Locale;
import java.util.UUID;

public interface User {

    Component getDisplayName();

    Locale getLocale();

    String getName();

    UUID getUUID();

}