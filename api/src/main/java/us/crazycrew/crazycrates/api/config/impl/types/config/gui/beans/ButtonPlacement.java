package us.crazycrew.crazycrates.api.config.impl.types.config.gui.beans;

import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
@ApiStatus.Internal
public class ButtonPlacement {

    @Setting("column")
    private int column = 5;
    @Setting("row")
    private int row = 5;

    public void initialize(final int column, final int row) {
        this.column = column;
        this.row = row;
    }

    public int getColumn() {
        return this.column;
    }

    public int getRow() {
        return this.row;
    }
}