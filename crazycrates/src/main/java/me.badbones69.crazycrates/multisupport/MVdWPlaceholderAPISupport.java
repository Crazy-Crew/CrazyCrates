package me.badbones69.crazycrates.multisupport;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.CrazyCrates;
import org.bukkit.plugin.Plugin;

import java.text.NumberFormat;

public class MVdWPlaceholderAPISupport
{

    private static CrazyCrates cc = CrazyCrates.getInstance();

    public static void registerPlaceholders(Plugin plugin)
    {
        for (final Crate crate : cc.getCrates())
        {
            if (crate.getCrateType() != CrateType.MENU)
            {
                PlaceholderAPI.registerPlaceholder(plugin, "crazycrates_" + crate, new PlaceholderReplacer()
                {
                    @Override
                    public String onPlaceholderReplace(PlaceholderReplaceEvent e)
                    {
                        return NumberFormat.getNumberInstance().format(CrazyCrates.getInstance().getVirtualKeys(e.getPlayer(), crate));
                    }
                });
            }
        }
    }

}