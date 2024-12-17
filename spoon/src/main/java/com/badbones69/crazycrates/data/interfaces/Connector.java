package com.badbones69.crazycrates.data.interfaces;

import java.io.File;
import java.sql.Connection;

public interface Connector {

    Connector init(final File file);

    void start();

    void stop();

    boolean isRunning();

    String url();

    Connection getConnection();

    File getFile();

    boolean tableExists(final Connection connection, final String table);

}