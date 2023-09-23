package us.crazycrew.crazycrates.api.platforms;

import org.jetbrains.annotations.NotNull;

public interface Platform {

    @NotNull type getPlatform();

    enum type {

        paper("paper"),
        fabric("fabric"),
        folia("folia");

        private final String platform;

        type(String platform) {
            this.platform = platform;
        }

        public @NotNull String getPlatform() {
            return this.platform;
        }
    }
}