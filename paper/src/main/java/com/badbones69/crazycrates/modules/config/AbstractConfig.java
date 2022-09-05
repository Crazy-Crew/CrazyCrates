package com.badbones69.crazycrates.modules.config;

import com.badbones69.crazycrates.utilities.logger.CrazyLogger;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * @author Billygalbreath
 * Thanks billy ^_^
 */
public class AbstractConfig {

    private YamlConfiguration config;

    public YamlConfiguration getConfig() {
        return this.config;
    }

    public void handle(Path path, Class<? extends AbstractConfig> clazz, CrazyLogger logger) {
        this.config = new YamlConfiguration();

        getConfig().options().copyDefaults(true);
        getConfig().options().parseComments(true);
        getConfig().options().width(9999);

        File file = path.toFile();
        String fileName = path.getFileName().toString();

        try {
            getConfig().load(file);
        } catch (IOException | InvalidConfigurationException e) {
            logger.debug("<red>Failed to load</red> <gold>" + fileName + "</gold><red>...</red>");

            e.printStackTrace();
        }

        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
            field.setAccessible(true);

            Key key = field.getDeclaredAnnotation(Key.class);
            Comment comment = field.getDeclaredAnnotation(Comment.class);

            if (key == null) return;

            try {
                Object classObj = getClassObject();
                Object value = getValue(key.value(), field.get(classObj));

                field.set(classObj, value instanceof String str ? StringEscapeUtils.unescapeJava(str) : value);
            } catch (IllegalAccessException e) {
                logger.debug("<red>Failed to write to</red> <gold>" + fileName + "</gold><red>...</red>");

                e.printStackTrace();
            }

            if (comment != null) setComments(key.value(), Arrays.stream(comment.value().split("\n")).toList());
        });

        try {
            getConfig().save(file);
        } catch (IOException e) {
            logger.debug("<red>Failed to save</red> <gold>" + fileName + "</gold><red>...</red>");

            e.printStackTrace();
        }
    }

    protected Object getClassObject() {
        return null;
    }

    protected Object getValue(String path, Object def) {
        if (getConfig().get(path) == null) getConfig().set(path, def);

        return getConfig().get(path);
    }

    protected void setComments(String path, List<String> comments) {
        getConfig().setComments(path, comments);
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Key {
        String value();
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Comment {
        String value();
    }
}