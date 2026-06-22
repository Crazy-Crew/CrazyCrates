package com.badbones69.crazycrates.paper.support.placeholders.api;

import org.jspecify.annotations.NonNull;
import java.util.Optional;

public interface AbstractPlaceholder {

    @NonNull Optional<String> get(@NonNull final String placeholder);

}