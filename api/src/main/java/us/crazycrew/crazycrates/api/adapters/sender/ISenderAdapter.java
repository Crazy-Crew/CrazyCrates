package us.crazycrew.crazycrates.api.adapters.sender;

import com.ryderbelserion.fusion.core.api.FusionKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApiStatus.Internal
public abstract class ISenderAdapter<C, S> {

    public abstract UUID getUniqueId(@NotNull final S sender);

    public abstract String getName(@NotNull final S sender);

    public abstract void sendActionBar(@NotNull final S sender, @NotNull final FusionKey id, @NotNull final Map<String, String> placeholders);

    public abstract void sendMessage(@NotNull final S sender, @NotNull final FusionKey id, @NotNull final Map<String, String> placeholders);

    public abstract C getComponent(@NotNull final S sender, @NotNull final FusionKey id, @NotNull final Map<String, String> placeholders);

    public abstract String getMessage(@NotNull final S sender, @NotNull final FusionKey id, @NotNull final Map<String, String> placeholders);

    public String getMessage(@NotNull final S sender, @NotNull final FusionKey id) {
        return getMessage(sender, id, Map.of());
    }

    public C getComponent(@NotNull final S sender, @NotNull final FusionKey id) {
        return getComponent(sender, id, new HashMap<>());
    }

    public void sendActionBar(@NotNull final S sender, @NotNull final FusionKey id) {
        sendActionBar(sender, id, new HashMap<>());
    }

    public void sendMessage(@NotNull final S sender, @NotNull final FusionKey id) {
        sendMessage(sender, id, new HashMap<>());
    }

    public abstract boolean isConsole(S sender);

}