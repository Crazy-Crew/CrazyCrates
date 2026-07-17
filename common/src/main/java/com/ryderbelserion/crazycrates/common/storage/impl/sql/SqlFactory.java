package com.ryderbelserion.crazycrates.common.storage.impl.sql;

import com.ryderbelserion.crazycrates.common.CrazyCratesPlugin;
import com.ryderbelserion.crazycrates.common.storage.impl.file.FlatFactory;
import org.jspecify.annotations.NullMarked;
import java.sql.Connection;
import java.sql.SQLException;

@NullMarked
public abstract class SqlFactory extends FlatFactory {

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

    public SqlFactory(final CrazyCratesPlugin plugin, final String impl) {
        super(plugin, impl);
    }

    public abstract Connection getConnection() throws SQLException;

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void save() {

    }
}