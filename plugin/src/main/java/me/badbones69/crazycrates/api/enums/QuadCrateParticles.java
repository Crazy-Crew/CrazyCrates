package me.badbones69.crazycrates.api.enums;

public enum QuadCrateParticles {
    
    FLAME("Flame"),
    VILLAGER_HAPPY("Villager Happy"),
    SPELL_WITCH("Spell Witch"),
    REDSTONE("Redstone");
    
    private final String name;
    
    QuadCrateParticles(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
}