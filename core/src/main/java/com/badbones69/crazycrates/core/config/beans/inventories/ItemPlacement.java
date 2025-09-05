package com.badbones69.crazycrates.core.config.beans.inventories;

import ch.jalu.configme.Comment;
import ch.jalu.configme.beanmapper.ExportName;

public class ItemPlacement {

    @Comment("The row for the item. You cannot use a number greater than the available rows of the inventory, -1 uses a fallback value.")
    private int row = 5;

    @Comment("The slot for the item. -1 uses a fallback value.")
    @ExportName("column")
    private int column = 5;

    public void setColumn(final int column) {
        this.column = column;
    }

    public void setRow(final int row) {
        this.row = row;
    }

    public int getColumn() {
        return this.column;
    }

    public int getRow() {
        return this.row;
    }

    public ItemPlacement init(final int column) {
        this.column = column;
        this.row = -1;

        return this;
    }
}