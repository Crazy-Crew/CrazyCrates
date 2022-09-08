package com.badbones69.crazycrates.common.configuration;

import com.badbones69.crazycrates.common.utilities.AdventureUtils;
import com.badbones69.crazycrates.common.utilities.logger.CrazyLogger;
import net.kyori.adventure.audience.Audience;
import org.apache.commons.lang3.StringEscapeUtils;
import org.simpleyaml.configuration.comments.CommentType;
import org.simpleyaml.configuration.file.YamlFile;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Path;
import java.util.Arrays;

public class AbstractConfig {

    protected YamlFile config;

    public YamlFile getConfig() {
        return this.config;
    }

    public void handle(Path path, Class<? extends AbstractConfig> clazz, CrazyLogger logger, Audience audience, AdventureUtils adventureUtils) {
        config = new YamlFile(path.toFile());

        File file = path.toFile();
        String fileName = path.getFileName().toString();

        try {
            config.createOrLoadWithComments();
        } catch (IOException e) {
            logger.debug("<red>Failed to load</red> <gold>" + fileName + "</gold><red>...</red>", audience, adventureUtils);

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
                logger.debug("<red>Failed to write to</red> <gold>" + fileName + "</gold><red>...</red>", audience, adventureUtils);

                e.printStackTrace();
            }

            if (comment != null) setComments(key.value(), comment.value());
        });

        try {
            config.save(file);
        } catch (IOException e) {
            logger.debug("<red>Failed to save</red> <gold>" + fileName + "</gold><red>...</red>", audience, adventureUtils);

            e.printStackTrace();
        }
    }

    protected Object getClassObject() {
        return null;
    }

    protected Object getValue(String path, Object def) {
        if (config.get(path) == null) config.set(path, def);

        return config.get(path);
    }

    protected void setComments(String path, String comment) {
        config.setComment(path, comment, CommentType.BLOCK);
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