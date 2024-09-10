package com.badbones69.crazycrates.api.builders.v2.types;

import com.badbones69.crazycrates.api.builders.v2.DynamicInventoryBuilder;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiFiller;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiItem;
import com.ryderbelserion.vital.paper.api.builders.gui.types.PaginatedGui;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CratePreviewMenu extends DynamicInventoryBuilder {

    private final Tier tier;

    public CratePreviewMenu(final Player player, final Crate crate, final Tier tier) {
        super(player, crate, crate.getPreviewName(), crate.getPreviewChestLines());

        this.tier = tier;
    }

    private final Player player = getPlayer();
    private final PaginatedGui gui = getGui();
    private final Crate crate = getCrate();

    @Override
    public void open() {
        if (this.crate == null) return;

        if (this.crate.isBroadcastToggle()) {
            final GuiFiller guiFiller = this.gui.getFiller();

            final ItemStack itemStack = this.crate.getBorderItem().setPlayer(this.player).getStack();

            guiFiller.fillTop(new GuiItem(itemStack));
            guiFiller.fillBottom(new GuiItem(itemStack));
        }

        setBackButton(6, 4, action -> {
            this.crate.playSound(this.player, this.player.getLocation(), "click-sound","ui.button.click", Sound.Source.PLAYER);

            this.gui.previous();
        });

        setNextButton(6, 6, action -> {
            this.crate.playSound(this.player, this.player.getLocation(), "click-sound","ui.button.click", Sound.Source.PLAYER);

            this.gui.next();
        });

        this.crate.getPreviewItems(this.player, this.tier).forEach(itemStack -> {
            this.gui.addPageItem(new GuiItem(itemStack));
        });

        addMenuButton(this.player, this.crate, this.gui, 6, 5);

        this.gui.open(this.player, 1);
    }
}