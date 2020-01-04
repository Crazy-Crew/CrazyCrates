package me.badbones69.crazycrates.multisupport.converters;

import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.objects.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class CrateConvertInfo {
    
    private String crateName;
    private CrateType crateType;
    private String guiName;
    private int startingKeys;
    private boolean inGUI;
    private int slot;
    private boolean openingBrodcastEnabled;
    private String broadcast;
    private ItemBuilder displayItem;
    private String previewName;
    private boolean previewEnabled;
    private int previewLines;
    private boolean boarderEnabled;
    private ItemBuilder boarderItem;
    private ItemBuilder physicalKey;
    private List<PrizeConvertInfo> prizes;
    
    public CrateConvertInfo() {
        this.crateName = "Converted Crate";
        this.crateType = CrateType.CSGO;
        this.guiName = "&7Converted Crate";
        this.startingKeys = 0;
        this.inGUI = true;
        this.slot = 10;
        this.openingBrodcastEnabled = false;
        this.broadcast = "Converted Crate Broadcast";
        this.displayItem = new ItemBuilder();
        this.previewName = "&7Converted Crate";
        this.previewEnabled = true;
        this.previewLines = 6;
        this.boarderEnabled = true;
        this.boarderItem = new ItemBuilder();
        this.physicalKey = new ItemBuilder();
        this.prizes = new ArrayList<>();
    }
    
    public String getCrateName() {
        return this.crateName;
    }
    
    public CrateConvertInfo setCrateName(String crateName) {
        this.crateName = crateName;
        return this;
    }
    
    public CrateType getCrateType() {
        return crateType;
    }
    
    public CrateConvertInfo setCrateType(CrateType crateType) {
        this.crateType = crateType;
        return this;
    }
    
    public String getGUIName() {
        return guiName;
    }
    
    public CrateConvertInfo setGUIName(String guiName) {
        this.guiName = guiName;
        return this;
    }
    
    public int getStartingKeys() {
        return startingKeys;
    }
    
    public CrateConvertInfo setStartingKeys(int startingKeys) {
        this.startingKeys = startingKeys;
        return this;
    }
    
    public boolean isInGUI() {
        return inGUI;
    }
    
    public CrateConvertInfo setInGUI(boolean inGUI) {
        this.inGUI = inGUI;
        return this;
    }
    
    public int getSlot() {
        return slot;
    }
    
    public CrateConvertInfo setSlot(int slot) {
        this.slot = slot;
        return this;
    }
    
    public boolean isOpeningBrodcastEnabled() {
        return openingBrodcastEnabled;
    }
    
    public CrateConvertInfo setOpeningBrodcastEnabled(boolean openingBrodcastEnabled) {
        this.openingBrodcastEnabled = openingBrodcastEnabled;
        return this;
    }
    
    public String getBroadcast() {
        return broadcast;
    }
    
    public CrateConvertInfo setBroadcast(String broadcast) {
        this.broadcast = broadcast;
        return this;
    }
    
    public ItemBuilder getDisplayItem() {
        return displayItem;
    }
    
    public CrateConvertInfo setDisplayItem(ItemBuilder displayItem) {
        this.displayItem = displayItem;
        return this;
    }
    
    public String getPreviewName() {
        return previewName;
    }
    
    public CrateConvertInfo setPreviewName(String previewName) {
        this.previewName = previewName;
        return this;
    }
    
    public boolean isPreviewEnabled() {
        return previewEnabled;
    }
    
    public CrateConvertInfo setPreviewEnabled(boolean previewEnabled) {
        this.previewEnabled = previewEnabled;
        return this;
    }
    
    public int getPreviewLines() {
        return previewLines;
    }
    
    public CrateConvertInfo setPreviewLines(int previewLines) {
        this.previewLines = previewLines;
        return this;
    }
    
    public boolean isBoarderEnabled() {
        return boarderEnabled;
    }
    
    public CrateConvertInfo setBoarderEnabled(boolean boarderEnabled) {
        this.boarderEnabled = boarderEnabled;
        return this;
    }
    
    public ItemBuilder getBoarderItem() {
        return boarderItem;
    }
    
    public CrateConvertInfo setBoarderItem(ItemBuilder boarderItem) {
        this.boarderItem = boarderItem;
        return this;
    }
    
    public ItemBuilder getPhysicalKey() {
        return physicalKey;
    }
    
    public CrateConvertInfo setPhysicalKey(ItemBuilder physicalKey) {
        this.physicalKey = physicalKey;
        return this;
    }
    
    public List<PrizeConvertInfo> getPrizes() {
        return prizes;
    }
    
    public CrateConvertInfo setPrizes(List<PrizeConvertInfo> prizes) {
        this.prizes = prizes;
        return this;
    }
    
}