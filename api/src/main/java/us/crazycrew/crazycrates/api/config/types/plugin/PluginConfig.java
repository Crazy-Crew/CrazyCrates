package us.crazycrew.crazycrates.api.config.types.plugin;

import com.ryderbelserion.fusion.core.utils.StringUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import us.crazycrew.crazycrates.api.config.types.plugin.types.GuiConfig;
import us.crazycrew.crazycrates.api.enums.Files;
import us.crazycrew.crazycrates.api.enums.messages.State;
import us.crazycrew.crazycrates.platform.ISettings;
import java.util.List;

@ApiStatus.Internal
public final class PluginConfig implements ISettings {

    private CommentedConfigurationNode configuration;

    private GuiConfig guiConfig;

    public void init() {
        this.configuration = Files.config.getConfiguration();

        this.guiConfig = new GuiConfig(this.configuration.node("gui"));
    }

    public GuiConfig getGuiConfig() {
        return this.guiConfig;
    }

    public boolean isGiveVirtualKeysIfInventoryFull() {
        return this.configuration.node("crate", "keys", "inventory-settings", "give-virtual-keys").getBoolean(false);
    }

    public boolean isNotifyPlayerWhenInventoryFull() {
        return this.configuration.node("crate", "keys", "inventory-settings", "send-message").getBoolean(false);
    }

    public boolean isKeySoundEnabled() {
        return this.configuration.node("crate", "keys", "key-sound", "toggle").getBoolean(true);
    }

    public String getKeySound() {
        return this.configuration.node("crate", "keys", "key-sound", "name").getString("entity.villager.no");
    }

    public boolean isDifferentItemLayoutEnabled() {
        return this.configuration.node("root", "use-different-items-layout").getBoolean(true);
    }

    public boolean isUpdatingExampleFolders() {
        return this.configuration.node("root", "update-examples-folder").getBoolean(true);
    }

    public boolean isNewEditorEnabled() {
        return this.configuration.node("root", "use-new-editor").getBoolean(false);
    }

    public boolean isMetricsEnabled() {
        return this.configuration.node("root", "toggle-metrics").getBoolean(true);
    }

    public @NonNull String getHologramPlugin() {
        return this.configuration.node("root", "hologram-plugin").getString("");
    }

    public @NonNull State getMessageState() {
        return switch (this.configuration.node("root", "message-state").getString("send_message")) {
            case "send_actionbar" -> State.send_actionbar;
            default -> State.send_message;
        };
    }

    public @NonNull String getPrefix() {
        return this.configuration.node("root", "command_prefix").getString(" <gradient:#e91e63:blue>CrazyCrates</gradient> | ");
    }

    public boolean isPhysicalInteractionSwapped() {
        return this.configuration.node("crate", "interaction", "physical").getBoolean(true);
    }

    public boolean isVirtualInteractionSwapped() {
        return this.configuration.node("crate", "interaction", "virtual").getBoolean(false);
    }

    public boolean isDisplayItemVisible() {
        return this.configuration.node("crate", "quickcrate-display-item").getBoolean(true);
    }

    public boolean isPreviewExitMessageSent() {
        return this.configuration.node("crate", "preview", "send-message").getBoolean(false);
    }

    public boolean isPreviewForceExit() {
        return this.configuration.node("crate", "preview", "force-exit").getBoolean(false);
    }

    public boolean isCosmicTimeoutEnabled() {
        return this.configuration.node("crate", "cosmic-crate-timeout").getBoolean(true);
    }

    public boolean hasKnockback() {
        return this.configuration.node("crate", "knock-back").getBoolean(true);
    }

    public int getQuadCrateTimer() {
        return this.configuration.node("crate", "quad-crate", "timer").getInt(300);
    }

    public boolean isUsingRequiredKeys() {
        return this.configuration.node("crate", "use-required-keys").getBoolean(false);
    }

    public String getCyclingMaterial() {
        return this.configuration.node("crate", "types", "csgo", "cycling-material").getString("gold_ingot");
    }

    public String getCyclingFinishedMaterial() {
        return this.configuration.node("crate", "types", "csgo", "finished-material").getString("iron_ingot");
    }

    public boolean isLoggingConsole() {
        return this.configuration.node("crate", "log-to-console").getBoolean(false);
    }

    public boolean isLoggingFile() {
        return this.configuration.node("crate", "log-to-file").getBoolean(false);
    }

    @Override
    public boolean isPhysicalAcceptsVirtual() {
        return this.configuration.node("crate", "keys", "physical-crate-accepts-virtual-keys").getBoolean(true);
    }

    @Override
    public boolean isPhysicalAcceptsPhysical() {
        return this.configuration.node("crate", "keys", "physical-crate-accepts-physical-keys").getBoolean(true);
    }

    @Override
    public boolean isVirtualAcceptsPhysical() {
        return this.configuration.node("crate", "keys", "virtual-crate-accepts-physical-keys").getBoolean(true);
    }

    @Override
    public @NonNull List<String> getDisabledWorlds() {
        return StringUtils.getStringList(this.configuration.node("crate", "disabled-worlds"), List.of());
    }
}