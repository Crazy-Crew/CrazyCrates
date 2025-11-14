package com.badbones69.crazycrates.core.databases.interfaces;

import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.sql.Connection;

public interface IConnector {

    IConnector init(@NotNull final Path path);

    void start();

    void stop();

    boolean isRunning();

    String url();

    Connection getConnection();

    Path getPath();

    boolean tableExists(@NotNull final Connection connection, @NotNull final String table);

}