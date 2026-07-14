package us.crazycrew.crazycrates.api.config.impl.types.config.gui.beans;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import java.util.List;

@ConfigSerializable
public class ButtonConfig {

    @Setting("placement")
    private ButtonPlacement placement;

    @Setting("toggle")
    private boolean isEnabled;

    @Setting("lore")
    private List<String> lore;
    @Setting("item")
    private String item;
    @Setting("name")
    private String name;

    @Setting("custom-model-data")
    private String model_data = "-1";

    @Setting("namespace")
    private String namespace = "";
    @Setting("id")
    private String id = "";

    public ButtonConfig initialize(final String name, final List<String> lore, final String item, final int column, final int row) {
        this.placement = new ButtonPlacement();
        this.placement.initialize(column, row);

        this.isEnabled = false;
        this.name = name;
        this.lore = lore;
        this.item = item;

        return this;
    }

    public ButtonPlacement getPlacement() {
        return this.placement;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public void setLore(final List<String> lore) {
        this.lore = lore;
    }

    public String getModelData() {
        return this.model_data;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getItem() {
        return this.item;
    }

    public String getId() {
        return this.id;
    }
}