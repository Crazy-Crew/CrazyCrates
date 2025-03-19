package com.badbones69.crazycrates.core.enums;

import java.util.List;

public enum Comments {

    name(List.of(
            "The name of the item."
    )),
    base64(List.of(
            "The base64 representation of the ItemStack."
    )),
    material(List.of(
            "The material of the item."
    )),
    lore(List.of(
            "The lore of the item."
    )),
    damage(List.of(
            "The damage to apply to the item."
    )),
    glowing(List.of(
            "Should the item glow?"
    )),
    amount(List.of(
            "The amount of the item to give."
    )),
    mob_type(List.of(
            "The type of mob to apply to the spawner/egg"
    )),
    color(List.of(
            "The color of the item."
    )),
    rgb(List.of(
            "The rgb of the item."
    )),
    hide_tool_tip(List.of(
            "Hides everything but lore/display name."
    )),
    trim_pattern(List.of(
            "The trim pattern of the armor."
    )),
    trim_material(List.of(
            "The trim material of the armor."
    )),
    unbreakable(List.of(
            "Should the item be unbreakable?"
    )),
    custom_model_data(List.of(
            "Minecraft no longer recognizes items this way however it may still work, an alternative will be provided.",
            "The custom model data of the item used to represent custom textures.",
            "",
            "Depending on the plugin used, The option for custom model data will be in one of their configuration files."
    )),
    skull(List.of(
            "This only works if HeadDatabaseAPI is on the server.",
            "The value you input is the id for the skull which is a string of numbers."
    )),
    player(List.of(
            "The player name/texture to display.",
            "https://minecraft-heads.com/"
    )),
    patterns(List.of(
            "The configuration section for patterns, minecraft: is not needed in front of the pattern.",
            "A list of enchantments https://jd.papermc.io/paper/1.21.4/io/papermc/paper/registry/keys/BannerPatternKeys.html",
            "pattern: color"
    )),
    enchantments(List.of(
            "The configuration section for enchantments, minecraft: is not needed in front of the enchantment.",
            "A list of enchantments https://jd.papermc.io/paper/1.21.4/io/papermc/paper/registry/keys/EnchantmentKeys.html",
            "enchantment: amount"
    )),
    potions(List.of(
            "The configuration section for potions, minecraft: is not needed in front of the potion.",
            "A list of potion effects https://jd.papermc.io/paper/1.21.4/io/papermc/paper/registry/keys/MobEffectKeys.html"
    ));

    private final List<String> comments;

    Comments(final List<String> comments) {
        this.comments = comments;
    }

    public List<String> getComments() {
        return this.comments;
    }
}