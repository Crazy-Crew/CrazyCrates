package us.crazycrew.crazycrates.common.api;

import com.ryderbelserion.cluster.api.adventure.FancyLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.crazycrates.api.CrazyCrates;
import us.crazycrew.crazycrates.api.CrazyCratesService;
import us.crazycrew.crazycrates.api.platforms.Platform;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import java.io.File;
import java.util.List;

public abstract class AbstractPlugin implements CrazyCrates {

    @Nullable
    public abstract String identifyClassLoader(ClassLoader classLoader) throws Exception;

    @NotNull
    public abstract ConfigManager getConfigManager();

    private final Platform.type platform;
    private final File dataFolder;

    public AbstractPlugin(File dataFolder, Platform.type platform) {
        this.dataFolder = dataFolder;
        this.platform = platform;
    }

    public void enablePlugin() {
        CrazyCratesService.setService(this);
    }

    public void disablePlugin() {
        CrazyCratesService.stopService();
    }

    /**
     * Made by lucko (luckperms)
     */
    public void apiWasLoadedByOurPlugin() {
        ClassLoader classLoader = this.platform.getClass().getClassLoader();

        for (Class<?> apiClass : new Class[]{CrazyCrates.class, CrazyCratesService.class}) {
            ClassLoader apiClassLoader = apiClass.getClassLoader();

            if (!apiClassLoader.equals(classLoader)) {
                String guilty = "unknown";

                try {
                    guilty = identifyClassLoader(apiClassLoader);
                } catch (Exception exception) {
                    // ignore
                }

                List.of(
                        "It seems that CrazyCrates API has been class-loaded by a plugin other than CrazyCrates!",
                        "The API seems to have been loaded by " + apiClassLoader + " (" + guilty + ") and the ",
                        "CrazyCrates plugin was loaded by " + classLoader.toString() + ".",
                        "Another plugin must've incorrectly shaded CrazyCrates API into the jar file which can cause runtime errors."
                ).forEach(FancyLogger::warn);

                return;
            }
        }
    }

    @NotNull
    @Override
    public Platform.type getPlatform() {
        return this.platform;
    }

    @NotNull
    @Override
    public File getDataFolder() {
        return this.dataFolder;
    }
}