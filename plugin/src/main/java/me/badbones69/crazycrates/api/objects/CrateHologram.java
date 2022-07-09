package me.badbones69.crazycrates.api.objects;

import java.util.ArrayList;
import java.util.List;

public class CrateHologram {
    
    private final boolean enabled;
    private final double height;
    private final List<String> messages;
    
    public CrateHologram() {
        this.enabled = false;
        this.height = 0.0;
        this.messages = new ArrayList<>();
    }
    
    public CrateHologram(boolean enabled, double height, List<String> messages) {
        this.enabled = enabled;
        this.height = height;
        this.messages = messages;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public double getHeight() {
        return height;
    }
    
    public List<String> getMessages() {
        return messages;
    }
    
}