package us.crazycrew.crazycrates.api.enums.messages;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

@ApiStatus.Internal
@NullMarked
public enum State {

    send_message("send_message"),
    send_actionbar("send_actionbar");

    private final String name;

    State(final String name) {
        this.name = name;
    }

    public final String getName() {
        return this.name;
    }
}