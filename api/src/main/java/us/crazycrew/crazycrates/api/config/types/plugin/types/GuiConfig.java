package us.crazycrew.crazycrates.api.config.types.plugin.types;

import com.ryderbelserion.fusion.core.utils.StringUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.CommentedConfigurationNode;import java.util.List;

@ApiStatus.Internal
@NullMarked
public final class GuiConfig {

    private final CommentedConfigurationNode configuration;

    public GuiConfig(final CommentedConfigurationNode configuration) {
        this.configuration = configuration;

        init();
    }

    private ButtonConfig fillerButton;
    private ButtonConfig menuButton;
    private ButtonConfig nextButton;
    private ButtonConfig backButton;

    private boolean isGuiCustomizerEnabled;
    private List<String> guiCustomizer;

    public void init() {
        final CommentedConfigurationNode buttons = this.configuration.node("buttons");

        this.fillerButton = new ButtonConfig(buttons.node("filler"), "black_stained_glass_pane", " ", -1, -1, List.of());
        this.menuButton = new ButtonConfig(buttons.node("menu"), "compass", "<bold><gray>» <red>Menu <gray>«</bold>", -1, 5, List.of("<gray>Return to the menu."));
        this.nextButton = new ButtonConfig(buttons.node("next"), "feather", "<bold><gold>Next »</bold>", -1, 6, List.of("<bold><gray>Page:</bold> <blue>{page}"));
        this.backButton = new ButtonConfig(buttons.node("back"), "feather", "<bold><gold>« Back</bold>", -1, 4, List.of("<bold><gray>Page:</bold> <blue>{page}"));

        this.isGuiCustomizerEnabled = this.configuration.node("inventory", "buttons", "customizer", "toggle").getBoolean(true);
        this.guiCustomizer = StringUtils.getStringList(this.configuration.node("inventory", "buttons", "customizer", "items"), List.of(
                "slot:0, item:red_stained_glass_pane, name: ",
                "slot:1, item:red_stained_glass_pane, name: ",
                "slot:2, item:red_stained_glass_pane, name: ",
                "slot:3, item:red_stained_glass_pane, name: ",
                "slot:4, item:red_stained_glass_pane, name: ",
                "slot:5, item:red_stained_glass_pane, name: ",
                "slot:6, item:red_stained_glass_pane, name: ",
                "slot:7, item:red_stained_glass_pane, name: ",
                "slot:8, item:red_stained_glass_pane, name: ",
                "slot:36, item:red_stained_glass_pane, name: ",
                "slot:37, item:red_stained_glass_pane, name: ",
                "slot:38, item:red_stained_glass_pane, name: ",
                "slot:39, item:red_stained_glass_pane, name: ",
                "slot:40, item:red_stained_glass_pane, name: ",
                "slot:41, item:red_stained_glass_pane, name: ",
                "slot:42, item:red_stained_glass_pane, name: ",
                "slot:43, item:red_stained_glass_pane, name: ",
                "slot:44, item:red_stained_glass_pane, name: ",
                "slot:9, item:blue_stained_glass_pane, name: ",
                "slot:18, item:blue_stained_glass_pane, name: ",
                "slot:27, item:blue_stained_glass_pane, name: ",
                "slot:17, item:blue_stained_glass_pane, name: ",
                "slot:26, item:blue_stained_glass_pane, name: ",
                "slot:35, item:blue_stained_glass_pane, name: ",
                "slot:10, item:cyan_stained_glass_pane, name: ",
                "slot:12, item:cyan_stained_glass_pane, name: ",
                "slot:14, item:cyan_stained_glass_pane, name: ",
                "slot:24, item:cyan_stained_glass_pane, name: ",
                "slot:16, item:cyan_stained_glass_pane, name: ",
                "slot:19, item:cyan_stained_glass_pane, name: ",
                "slot:20, item:cyan_stained_glass_pane, name: ",
                "slot:21, item:cyan_stained_glass_pane, name: ",
                "slot:22, item:cyan_stained_glass_pane, name: ",
                "slot:23, item:cyan_stained_glass_pane, name: ",
                "slot:24, item:cyan_stained_glass_pane, name: ",
                "slot:25, item:cyan_stained_glass_pane, name: ",
                "slot:28, item:cyan_stained_glass_pane, name: ",
                "slot:30, item:cyan_stained_glass_pane, name: ",
                "slot:31, item:cyan_stained_glass_pane, name: ",
                "slot:32, item:cyan_stained_glass_pane, name: ",
                "slot:33, item:cyan_stained_glass_pane, name: ",
                "slot:34, item:cyan_stained_glass_pane, name: "
        ));
    }

    public boolean isCrateMenuEnabled() {
        return this.configuration.node("toggle").getBoolean(true);
    }

    public String getCrateMenuName() {
        return this.configuration.node("inventory", "name").getString("<bold><gradient:#e91e63:blue>Crazy Crates</gradient></bold>");
    }

    public int getCrateMenuRows() {
        return this.configuration.node("inventory", "rows").getInt(5);
    }

    public boolean isGuiCustomizerEnabled() {
        return this.isGuiCustomizerEnabled;
    }

    public List<String> getGuiCustomizer() {
        return this.guiCustomizer;
    }

    public ButtonConfig getFillerButton() {
        return this.fillerButton;
    }

    public ButtonConfig getMenuButton() {
        return this.menuButton;
    }

    public ButtonConfig getNextButton() {
        return this.nextButton;
    }

    public ButtonConfig getBackButton() {
        return this.backButton;
    }
}