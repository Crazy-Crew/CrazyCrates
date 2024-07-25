package com.badbones69.crazycrates.config.impl.messages;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class ErrorKeys implements SettingsHolder {

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "All messages related to errors."
        };

        conf.setComment("errors", header);
    }

    @Comment("A list of available placeholders: {crate}")
    public static final Property<String> no_prizes_found = newProperty("errors.no-prizes-found", "{prefix}<red>This crate contains no prizes that you can win.");

    public static final Property<String> no_schematics_found = newProperty("errors.no-schematics-found", "{prefix}<red>No schematics were found. Please make sure files ending in .nbt exist in the schematics folder. If not delete the folder so they regenerate.");

    public static final Property<String> internal_error = newProperty("errors.internal-error", "{prefix}<red>An internal error has occurred. Please check the console for the full error.");

    @Comment("A list of available placeholders: {crate}")
    public static final Property<String> key_refund = newProperty("errors.key-refund", "{prefix}<red>An error has occurred with the crate {crate} that you were opening, A refund for your key has been given.");

    @Comment("A list of available placeholders: {value}")
    public static final Property<String> cannot_be_empty = newProperty("errors.cannot-be-empty", "{prefix}<red>{value} cannot be empty!");

    public static final Property<String> cannot_be_air = newProperty("errors.cannot-be-air", "{prefix}<red>You can't use air silly!~");

    @Comment("A list of available placeholders: {crate}")
    public static final Property<String> prize_error = newProperty("errors.prize-error", "{prefix}<red>An error has occurred while trying to give you the prize in crate called <gold>{crate}<red>. Please contact the server owner and show them this error.");
}