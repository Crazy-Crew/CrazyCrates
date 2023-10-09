package us.crazycrew.crazycrates.paper.crates.object;

import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.crates.config.CrateConfig;
import java.util.List;

public class Crate {

    private final CrazyCrates plugin;

    private final String crateType;
    private final String crateName;

    private final String previewName;

    private final int startingKeys;
    private final int requiredKeys;
    private final int maxMassOpen;

    private final boolean isCrateInMenu;

    private final int crateMenuSlot;

    private final boolean isOpeningBroadcast;
    private final String openingBroadcast;

    private final List<String> prizeMessages;

    private final String crateItemType;
    private final boolean isCrateItemGlowing;
    private final String crateItemName;
    private final List<String> crateItemLore;

    private final boolean isPreviewEnabled;
    private final int previewRows;
    private final boolean isPreviewGlassEnabled;
    private final String previewGlassName;
    private final String previewGlassType;

    private final String physicalKeyName;
    private final List<String> physicalKeyLore;
    private final String physicalKeyItem;
    private final boolean isPhysicalKeyGlowing;

    private final boolean isHologramEnabled;
    private final double hologramHeight;
    private final List<String> hologramMessages;

    public Crate(CrazyCrates plugin, CrateConfig crateConfig) {
        this.plugin = plugin;

        this.crateType = crateConfig.getCrateType();
        this.crateName = crateConfig.getCrateName();

        this.previewName = crateConfig.getPreviewName();

        this.startingKeys = crateConfig.getStartingKeys();
        this.requiredKeys = crateConfig.getRequiredKeys();
        this.maxMassOpen = crateConfig.getMaxMassOpen();

        this.isCrateInMenu = crateConfig.isCrateInMenu();
        this.crateMenuSlot = crateConfig.getCrateSlot();

        this.isOpeningBroadcast = crateConfig.isOpeningBroadcast();
        this.openingBroadcast = crateConfig.getOpeningBroadcast();

        this.prizeMessages = crateConfig.getPrizeMessages();

        this.crateItemType = crateConfig.getCrateItem();
        this.isCrateItemGlowing = crateConfig.isCrateItemGlowing();
        this.crateItemName = crateConfig.getCrateItemName();
        this.crateItemLore = crateConfig.getCrateItemLore();

        this.isPreviewEnabled = crateConfig.isPreviewEnabled();
        this.previewRows = crateConfig.getPreviewRows();
        this.isPreviewGlassEnabled = crateConfig.isPreviewGlassEnabled();
        this.previewGlassName = crateConfig.getPreviewGlassName();
        this.previewGlassType = crateConfig.getPreviewGlassType();

        this.physicalKeyName = crateConfig.getPhysicalKeyName();
        this.physicalKeyLore = crateConfig.getPhysicalKeyLore();
        this.physicalKeyItem = crateConfig.getPhysicalKeyItem();
        this.isPhysicalKeyGlowing = crateConfig.isPhysicalKeyGlowing();

        this.isHologramEnabled = crateConfig.isHologramEnabled();
        this.hologramHeight = crateConfig.getHologramHeight();
        this.hologramMessages = crateConfig.getHologramMessage();
    }

    public String getCrateType() {
        return this.crateType;
    }

    public String getCrateName() {
        return this.crateName;
    }

    public String getPreviewName() {
        return this.previewName;
    }

    public int getStartingKeys() {
        return this.startingKeys;
    }

    public int getRequiredKeys() {
        return this.requiredKeys;
    }

    public int getMaxMassOpen() {
        return this.maxMassOpen;
    }

    public boolean isCrateInMenu() {
        return this.isCrateInMenu;
    }

    public int getCrateMenuSlot() {
        return this.crateMenuSlot;
    }

    public boolean isOpeningBroadcast() {
        return this.isOpeningBroadcast;
    }

    public String getOpeningBroadcast() {
        return this.openingBroadcast;
    }

    public List<String> getPrizeMessages() {
        return this.prizeMessages;
    }

    public String getCrateItemType() {
        return this.crateItemType;
    }

    public boolean isCrateItemGlowing() {
        return this.isCrateItemGlowing;
    }

    public String getCrateItemName() {
        return this.crateItemName;
    }

    public List<String> getCrateItemLore() {
        return this.crateItemLore;
    }

    public boolean isPreviewEnabled() {
        return this.isPreviewEnabled;
    }

    public int getPreviewRows() {
        return this.previewRows;
    }

    public boolean isPreviewGlassEnabled() {
        return this.isPreviewGlassEnabled;
    }

    public String getPreviewGlassName() {
        return this.previewGlassName;
    }

    public String getPreviewGlassType() {
        return this.previewGlassType;
    }

    public String getPhysicalKeyName() {
        return this.physicalKeyName;
    }

    public List<String> getPhysicalKeyLore() {
        return this.physicalKeyLore;
    }

    public String getPhysicalKeyItem() {
        return this.physicalKeyItem;
    }

    public boolean isPhysicalKeyGlowing() {
        return this.isPhysicalKeyGlowing;
    }

    public boolean isHologramEnabled() {
        return this.isHologramEnabled;
    }

    public double getHologramHeight() {
        return this.hologramHeight;
    }

    public List<String> getHologramMessages() {
        return this.hologramMessages;
    }
}