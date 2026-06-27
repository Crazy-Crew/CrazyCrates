package com.ryderbelserion.crazycrates.paper.api.objects.crate;

import com.ryderbelserion.crazycrates.paper.api.objects.buttons.Button;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CrateButton {

    private final Button button;
    private final int slot;

    public CrateButton(final Button button, final int slot) {
        this.button = button;
        this.slot = slot;
    }

    public Button getButton() {
        return button;
    }

    public int getSlot() {
        return slot;
    }
}