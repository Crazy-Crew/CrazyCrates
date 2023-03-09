package us.crazycrew.crazycrates.loader;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

/**
 * @author RyderBelserion
 * @author BadBones69
 *
 * Created: 2/19/2023
 * Time: Unknown
 * Last Edited: 3/1/2023 @ 12:42 PM
 *
 * Description: The class path resolver where we download dependencies at run-time
 */
@SuppressWarnings("UnstableApiUsage")
public class CrazyLoader implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        resolver.addDependency(new Dependency(new DefaultArtifact("us.crazycrew.crazycore:crazycore-paper:1.1.0.0"), null));

        // Configs
        resolver.addDependency(new Dependency(new DefaultArtifact("ch.jalu:configme:1.3.0"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("me.carleslc.Simple-YAML:Simple-Yaml:1.8.3"), null));

        resolver.addRepository(new RemoteRepository.Builder("maven2", "default", "https://repo1.maven.org/maven2").build());
        resolver.addRepository(new RemoteRepository.Builder("crazycrew-libraries", "default", "https://repo.crazycrew.us/libraries").build());
        resolver.addRepository(new RemoteRepository.Builder("jitpack", "default", "https://jitpack.io").build());

        classpathBuilder.addLibrary(resolver);
    }
}