package com.badbones69.crazycrates;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

public class CrazyLoader implements PluginLoader {

    @Override
    public void classloader(PluginClasspathBuilder builder) {
        final MavenLibraryResolver resolver = new MavenLibraryResolver();

        resolver.addDependency(new Dependency(new DefaultArtifact("ch.jalu:configme:1.4.1"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("com.zaxxer:HikariCP:6.2.1"), null));

        resolver.addRepository(new RemoteRepository.Builder("paper", "default", "https://repo.papermc.io/repository/maven-public/").build());

        builder.addLibrary(resolver);
    }
}