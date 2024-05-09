package com.badbones69.crazycrates;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

public class LibraryLoader implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        resolver.addDependency(new Dependency(new DefaultArtifact("dev.triumphteam:triumph-cmd-bukkit:2.0.0-SNAPSHOT"), null));

        resolver.addDependency(new Dependency(new DefaultArtifact("ch.jalu:configme:1.4.1"), null));

        resolver.addRepository(new RemoteRepository.Builder("triumphteam", "default", "https://repo.triumphteam.dev/snapshots/").build());
        resolver.addRepository(new RemoteRepository.Builder("paper", "default", "https://repo.papermc.io/repository/maven-public/").build());

        classpathBuilder.addLibrary(resolver);
    }
}