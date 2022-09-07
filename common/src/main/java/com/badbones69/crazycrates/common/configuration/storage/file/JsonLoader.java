package com.badbones69.crazycrates.common.configuration.storage.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;

public class JsonLoader {

    private final File file;

    public JsonLoader(File file) {
        this.file = file;
    }

    private final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .enableComplexMapKeySerialization()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
            .create();

    private JsonLoader loadClass(Class<? extends JsonLoader> jsonLoader) {
        if (file.exists()) {
            try {
                FileInputStream fileStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileStream, StandardCharsets.UTF_8);

                return gson.fromJson(inputStreamReader, jsonLoader);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return null;
    }
}