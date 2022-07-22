package com.badbones69.crazycrates.api.files;

import com.badbones69.crazycrates.api.files.annotations.Comment;
import com.badbones69.crazycrates.api.files.annotations.Key;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class AbstractConfig {

    private YamlConfiguration config;

    public YamlConfiguration getConfig() {
        return this.config;
    }

    public void reload(Path path, Class<? extends AbstractConfig> clazz) {
        this.config = new YamlConfiguration();

        getConfig().options().copyDefaults(true);
        getConfig().options().parseComments(true);
        getConfig().options().width(9999);

        File file = path.toFile();

        try {
            getConfig().load(file);
        } catch (IOException ignore) {
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
            Key key = field.getDeclaredAnnotation(Key.class);
            Comment comment = field.getDeclaredAnnotation(Comment.class);

            if (key == null) return;

            try {
                Object classObj = getClassObject();
                Object value = getValue(key.value(), field.get(classObj));
                field.set(classObj, value instanceof String str ? StringEscapeUtils.unescapeJava(str) : value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if (comment != null) setComments(key.value(), Arrays.stream(comment.value().split("\n")).toList());
        });

        try {
            getConfig().save(file);
        } catch (IOException e) {
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
}