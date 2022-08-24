package com.badbones69.crazycrates.modules.config.types.json;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.modules.config.types.json.adapters.EnumTypeAdapter;
import com.badbones69.crazycrates.utilities.logger.CrazyLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class PersistEngine {

    private final CrazyCrates crazyCrates = CrazyCrates.getInstance();

    private final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .enableComplexMapKeySerialization()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
            .registerTypeAdapterFactory(EnumTypeAdapter.ENUM_FACTORY)
            .create();

    public File getFile(String fileName) {
        return new File(crazyCrates.DATA_DIRECTORY.toFile(), fileName);
    }

    public void handle(Class<? extends PersistEngine> classObject, CrazyLogger crazyLogger) {
        Arrays.stream(classObject.getDeclaredFields()).forEach(field -> {
            field.setAccessible(true);

            try {
                crazyLogger.debug(gson.toJson(field.get(classObject)));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}