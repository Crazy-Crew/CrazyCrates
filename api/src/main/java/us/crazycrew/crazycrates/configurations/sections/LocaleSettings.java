package us.crazycrew.crazycrates.configurations.sections;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;

/**
 * @author RyderBelserion
 * @author BadBones69
 *
 * Description: The locale files.
 */
public class LocaleSettings implements SettingsHolder {

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/crazycrew",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Issues: https://github.com/Crazy-Crew/CrazyCrates/issues",
                "Features: https://github.com/Crazy-Crew/CrazyCrates/discussions",
                "Translations: https://github.com/Crazy-Crew/CrazyCrates/discussions/categories/translations"
        };

        conf.setComment("settings", header);
    }

}