package com.ryderbelserion.crazycrates.common.storage.impl;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class ConnectionFactory {

    protected final String create_crate_worlds_table = "create table if not exists crate_worlds(" +
            "world varchar(36) primary key, " +
            "name varchar(36) not null)";

    protected final String create_crate_locations_table = "create table if not exists crate_locations(" +
            "id varchar(16) primary key, " +
            "world_id varchar(36) not null, " +
            "crate_id varchar(36) not null, " +
            "x bigint not null, " +
            "y bigint not null, " +
            "z bigint not null, " +
            "foreign key(world_id) references crate_worlds(world) on delete cascade)";

    public abstract Connection getConnection() throws SQLException;

    public abstract String getImplementation();

    public abstract boolean isRunning();

    public abstract void init();

    public abstract void stop();

}