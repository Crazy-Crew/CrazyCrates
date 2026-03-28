package com.badbones69.crazycrates.core.constants;

import com.ryderbelserion.fusion.core.FusionKey;
import java.util.List;

public class PluginSupport {

    public static final FusionKey decent_holograms = FusionKey.key("crazycrates", "DecentHolograms");

    public static final FusionKey fancy_holograms = FusionKey.key("crazycrates", "FancyHolograms");

    public static final FusionKey cmi = FusionKey.key("crazycrates", "CMI");

    public static final List<FusionKey> dependencies = List.of(
            decent_holograms,
            fancy_holograms,
            cmi
    );
}