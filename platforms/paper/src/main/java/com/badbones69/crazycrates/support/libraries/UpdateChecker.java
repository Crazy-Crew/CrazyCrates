package com.badbones69.crazycrates.support.libraries;

import com.badbones69.crazycrates.CrazyCrates;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final int project;

    private URL apiPage;

    private String newVersion;

    public UpdateChecker(int project) {
        this.project = project;

        try {
            this.apiPage = new URL("https://api.spiget.org/v2/resources/" + project + "/versions/latest");
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Get the project ID of the Spigot Plugin.
     *
     * @return The project id.
     */
    public int getProjectID() {
        return project;
    }

    /**
     * Gets the new version from SpigotMC.
     *
     * @return The new version.
     */
    public String getNewVersion() {
        return newVersion;
    }

    /**
     * Get the resource page of the project.
     *
     * @return The resource project url.
     */
    public String getResourcePage() {
        return "https://www.spigotmc.org/resources/" + project;
    }

    /**
     * Check if the plugin has an update.
     *
     * @return True if yes otherwise false.
     */
    public boolean hasUpdate() throws IOException {
        // Open the connection.
        HttpURLConnection connection = (HttpURLConnection) apiPage.openConnection();

        // Set a user agent.
        connection.addRequestProperty("User-Agent", "CrazyCrew");

        InputStream inputStream = connection.getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream);

        Gson gson = new Gson();

        int currentVersion = Integer.parseInt(plugin.getDescription().getVersion().replace(".", "").replace("+Beta", ""));

        String apiValue = gson.fromJson(reader, JsonObject.class).get("name").getAsString();

        this.newVersion = apiValue;

        int spigotVersion = Integer.parseInt(apiValue.replace(".", "").replace("v", "").replace("+Beta", ""));

        connection.disconnect();
        inputStream.close();
        reader.close();

        return spigotVersion > currentVersion;
    }
}