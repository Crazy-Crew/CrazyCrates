package us.crazycrew.crazycrates.api.config.impl.types.config;

import org.jspecify.annotations.NonNull;
import us.crazycrew.crazycrates.api.config.annotations.Comment;
import us.crazycrew.crazycrates.api.config.properties.builders.CommentsBuilder;
import us.crazycrew.crazycrates.api.config.properties.interfaces.IPropertyHolder;
import us.crazycrew.crazycrates.api.config.properties.objects.interfaces.Property;
import java.util.List;
import static us.crazycrew.crazycrates.api.config.properties.PropertyBuilder.newProperty;

public class DatabaseKeys implements IPropertyHolder {

    @Override
    public void registerComments(@NonNull final CommentsBuilder builder) {
        builder.setComment("Configuration options related to the plugin database!", "root", "storage");

        builder.setComment(List.of(
                "Allows you to customize, or enter credentials for external providers!",
                "If you do not know what you are doing, do not touch without asking questions."
        ), "root", "storage", "connection");

        builder.setComment(List.of(
                "These settings apply to the MySQL connection pool.",
                " - The default values will be suitable for the majority of users.",
                "- Do not change these settings unless you know what you're doing!"
        ), "root", "storage", "pool-settings");

        builder.setComment(List.of(
                "This setting allows you to define extra properties for connections.",
                " ",
                "By default, the following options are set to enable utf8 encoding. (you may need to remove",
                "these if you are using PostgreSQL)",
                "   use-unicode: true",
                "   character-encoding: utf8",
                " ",
                " You can also use this section to disable SSL connections, by uncommenting the 'use-ssl' and",
                " 'verify-server-certificate' options below."
        ), "root", "storage", "pool-settings", "properties");
    }

    @Comment({
            "Available Types:",
            "  ⤷ sqlite (PREFERRED FOR SINGLE SERVER PERFORMANCE)",
            "  ⤷ postgres (N/A)",
            "  ⤷ yaml (DEFAULT)"
    })
    public static final Property<String> storage_type = newProperty("yaml", "root", "storage", "type");

    @Comment({
            "This is only if using cloud providers like Postgres, and must not be empty!",
            "This database must exist *before* you turn the server on."
    })
    public static final Property<String> database_name = newProperty("", "root", "storage", "connection", "database");

    @Comment({
            "Available Ports:",
            "  ⤷ postgres (5432)",
            "  ⤷ sqlite (N/A)",
            "  ⤷ yaml (N/A)"
    })
    public static final Property<Integer> database_port = newProperty(5432, "root", "storage", "connection", "port");

    @Comment("The username to connect to the database.")
    public static final Property<String> database_username = newProperty("", "root", "storage", "connection", "username");

    @Comment("The password to connect to the database.")
    public static final Property<String> database_password = newProperty("", "root", "storage", "connection", "password");

    @Comment({
            "Define the address and port for the database.",
            " - The standard DB engine port is used by default",
            "   (MySQL: 3306, PostgreSQL: 5432, MongoDB: 27017)",
            " - Specify as host:port if differs"
    })
    public static final Property<String> database_address = newProperty("", "root", "storage", "connection", "address");

    @Comment({
            "Sets the maximum size of the MySQL connection pool.",
            " - Basically this value will determine the maximum number of actual",
            "   connections to the database backend.",
            " - More information about determining the size of connection pools can be found here:",
            "   https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing"
    })
    public static final Property<Integer> maximum_pool_size = newProperty(10, "root", "storage", "pool-settings", "maximum-pool-size");

    @Comment({
            "Sets the minimum number of idle connections that the pool will try to maintain.",
            " - For maximum performance and responsiveness to spike demands, it is recommended to not set",
            "   this value and instead allow the pool to act as a fixed size connection pool.",
            "   (set this value to the same as 'maximum-pool-size')"
    })
    public static final Property<Integer> minimum_pool_size = newProperty(10, "root", "storage", "pool-settings", "minimum-idle");

    @Comment({
            "This setting controls the maximum lifetime of a connection in the pool in milliseconds.",
            " - The value should be at least 30 seconds less than any database or infrastructure imposed connection time limit.",
            " ",
            "The default time is 5 minutes, or 900,000"
    })
    public static final Property<Integer> maximum_lifetime = newProperty(900000, "root", "storage", "pool-settings", "maximum-lifetime");

    @Comment({
            "This setting controls the maximum number of milliseconds that the plugin will wait for a",
            "connection from the pool, before timing out.",
            " ",
            "The default time is 5 seconds, or 5,000"
    })
    public static final Property<Integer> connection_timeout = newProperty(5000, "root", "storage", "pool-settings", "connection-timeout");

    @Comment({
            "This setting controls how frequently the pool will 'ping' a connection in order to prevent it",
            "from being timed out by the database or network infrastructure, measured in milliseconds.",
            " - The value should be less than maximum-lifetime and greater than 30000 (30 seconds).",
            " - Setting the value to zero will disable the keepalive functionality."
    })
    public static final Property<Integer> connection_heartbeat = newProperty(0, "root", "storage", "pool-settings", "heartbeat");

    public static final Property<Boolean> unicode_property = newProperty(true, "root", "storage", "pool-settings", "properties", "use-unicode");

    public static final Property<String> character_encoding = newProperty("utf8", "root", "storage", "pool-settings", "properties", "character-encoding");

}