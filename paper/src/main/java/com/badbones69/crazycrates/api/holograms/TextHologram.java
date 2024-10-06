package com.badbones69.crazycrates.api.holograms;

import com.ryderbelserion.vital.common.util.AdvUtil;
import com.ryderbelserion.vital.common.util.StringUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class TextHologram {

    private Display.Billboard billboard;
    private List<String> text;
    private Location location;
    private Color color;
    private int height;

    /**
     * Builds a display object with a location
     *
     * @param location {@link Location}
     */
    public TextHologram(final Location location) {
        this.location = location;
    }

    /**
     * Builds a blanket display object
     */
    public TextHologram() {
        this.location = null;
    }

    private TextDisplay textDisplay;

    /**
     * Creates the text display
     *
     * @return {@link TextHologram}
     */
    public TextHologram create() {
        final World world = location.getWorld();

        this.textDisplay = world.spawn(this.location.clone().add(0.0, this.height, 0.0), TextDisplay.class, display -> {
            display.text(AdvUtil.parse(StringUtil.convertList(this.text)));

            display.setBillboard(org.bukkit.entity.Display.Billboard.CENTER);

            display.setBackgroundColor(this.color);

            display.setPersistent(true);
        });

        //todo() add to cache

        return this;
    }

    /**
     * Gets the text display
     *
     * @return {@link TextDisplay}
     */
    public TextDisplay getTextDisplay() {
        return this.textDisplay;
    }

    /**
     * Sets the billboard
     *
     * @param billboard the billboard
     */
    public void setBillboard(Display.Billboard billboard) {
        this.billboard = billboard;
    }

    /**
     * Gets the billboard
     *
     * @return the billboard
     */
    public Display.Billboard getBillboard() {
        return this.billboard;
    }

    /**
     * Sets the y-level height of the hologram
     *
     * @param height the y-level
     *
     * @return {@link TextHologram}
     */
    public TextHologram setHeight(int height) {
        this.height = height;

        return this;
    }

    /**
     * Gets the y-level height of the hologram
     *
     * @return the y-level
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Sets the hologram text
     *
     * @param text the text to show
     *
     * @return {@link TextHologram}
     */
    public TextHologram setTexts(@NotNull final List<String> text) {
        this.text = text;

        return this;
    }

    /**
     * Adds a text to existing list of text
     *
     * @param line the new text
     *
     * @return {@link TextHologram}
     */
    public TextHologram addText(@NotNull final String line) {
        this.text.add(line);

        return this;
    }

    /**
     * Gets a list of text for the hologram
     *
     * @return list of text
     */
    public List<String> getText() {
        return this.text;
    }

    /**
     * Sets the location
     *
     * @param location {@link Location}
     *
     * @return {@link TextHologram}
     */
    public TextHologram setLocation(Location location) {
        this.location = location;

        return this;
    }

    /**
     * Gets the location
     *
     * @return {@link Location}
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * Sets the background as transparent.
     *
     * @return {@link TextHologram}
     */
    public TextHologram setTransparent() {
        this.color = Color.fromARGB(0);

        return this;
    }

    /**
     * Sets the background color
     *
     * @param color {@link Color}
     *
     * @return {@link TextHologram}
     */
    public TextHologram setColor(Color color) {
        this.color = color;

        return this;
    }

    /**
     * Gets the background color
     *
     * @return {@link Color}
     */
    public Color getColor() {
        return this.color;
    }
}