package com.badbones69.crazycrates.paper.api.objects.crates;

import com.badbones69.crazycrates.paper.api.objects.buttons.Button;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CrateButton {

    private final Button button;
    private final int slot;

    public CrateButton(final Button button, final int slot) {
        this.button = button;
        this.slot = slot;
    }

    public final Button getButton() {
        return this.button;
    }

    public final int getSlot() {
        return this.slot;
    }
}