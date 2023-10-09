package us.crazycrew.crazycrates.paper.api.enums;

import us.crazycrew.crazycrates.paper.CrazyCrates;

public enum PluginSupport {

    decent_holograms("DecentHolograms"),
    holographic_displays("HolographicDisplays"),
    cmi("CMI"),
    placeholderapi("PlaceholderAPI"),
    oraxen("Oraxen"),
    items_adder("ItemsAdder");
    
    private final String name;

    PluginSupport(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }

    public boolean isPluginEnabled(CrazyCrates plugin) {
        return plugin.getServer().getPluginManager().isPluginEnabled(this.name);
    }
}