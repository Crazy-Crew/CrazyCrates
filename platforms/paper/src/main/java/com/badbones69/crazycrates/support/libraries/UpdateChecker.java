package com.badbones69.crazycrates.support.libraries;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    private final int project;

    private URL apiPage;

    private String newVersion;

    private PluginProviderContext context;

    public UpdateChecker(int project, PluginProviderContext context) {
        this.project = project;

        try {
            this.apiPage = new URL("https://api.spiget.org/v2/resources/" + project + "/versions/latest");

            this.context = context;
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
        return this.project;
    }

    /**
     * Gets the new version from SpigotMC.
     *
     * @return The new version.
     */
    public String getNewVersion() {
        return this.newVersion;
    }

    /**
     * Get the resource page of the project.
     *
     * @return The resource project url.
     */
    public String getResourcePage() {
        return "https://www.spigotmc.org/resources/" + this.project;
    }

    /**
     * Check if the plugin has an update.
     *
     * @return True if yes otherwise false.
     */
    public boolean update() throws IOException {
        // Open the connection.
        HttpURLConnection connection = (HttpURLConnection) this.apiPage.openConnection();

        // Set a user agent.
        connection.addRequestProperty("User-Agent", "CrazyCrew");

        InputStream inputStream = connection.getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream);

        Gson gson = new Gson();

        int currentVersion = Integer.parseInt(context.getConfiguration().getVersion().replace(".", "").replace("+Beta", ""));

        String apiValue = gson.fromJson(reader, JsonObject.class).get("name").getAsString();

        this.newVersion = apiValue;

        int spigotVersion = Integer.parseInt(apiValue.replace(".", "").replace("v", "").replace("+Beta", ""));

        connection.disconnect();
        inputStream.close();
        reader.close();

        return spigotVersion > currentVersion;
    }
}