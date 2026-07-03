package us.crazycrew.crazycrates.api.config.types.plugin.types;

import com.ryderbelserion.fusion.core.utils.StringUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.List;

@ApiStatus.Internal
@NullMarked
public final class ButtonConfig {

    private final List<String> lore;
    private final String name;
    private final String item;

    private final int column;
    private final int row;

    private final int customModelData;

    private final String model_namespace;
    private final String model_id;

    private final boolean isOverrideEnabled;
    private final List<String> commands;

    private final boolean isEnabled;

    public ButtonConfig(final CommentedConfigurationNode configuration, final String defaultItem,
                        final String defaultName,
                        final int row,
                        final int column,
                        final List<String> defaultLore
    ) {
        this.item = configuration.node("item").getString(defaultItem);
        this.name = configuration.node("name").getString(defaultName);
        this.lore = StringUtils.getStringList(configuration.node("lore"), defaultLore);

        this.column = configuration.node("placement", "column").getInt(column);
        this.row = configuration.node("placement", "row").getInt(row);

        this.customModelData = configuration.node("custom-model-data").getInt(-1);

        this.model_namespace = configuration.node("model", "namespace").getString("");
        this.model_id = configuration.node("model", "id").getString("");

        this.isOverrideEnabled = configuration.node("override", "toggle").getBoolean(false);
        this.commands = StringUtils.getStringList(configuration.node("override", "list"), List.of(
                "see {player}"
        ));

        this.isEnabled = configuration.node("toggle").getBoolean(false);
    }

    public boolean isOverrideEnabled() {
        return this.isOverrideEnabled;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public List<String> getCommands() {
        return this.commands;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public String getName() {
        return this.name;
    }

    public String getItem() {
        return this.item;
    }

    public String getModelNamespace() {
        return this.model_namespace;
    }

    public String getModelId() {
        return this.model_id;
    }

    public int getCustomModelData() {
        return this.customModelData;
    }

    public int getColumn() {
        return this.column;
    }

    public int getRow() {
        return this.row;
    }
}