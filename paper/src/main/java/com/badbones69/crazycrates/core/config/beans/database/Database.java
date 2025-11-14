package com.badbones69.crazycrates.core.config.beans.database;

import ch.jalu.configme.Comment;
import org.jetbrains.annotations.NotNull;

public class Database {

    @Comment("""
            The type of database which we store data in!
    
            Available Types:
              ⤷ SQLITE
              ⤷ YAML (DEFAULT)
            """)
    private String type = "YAML";

    public Database() {
        setType("YAML");
    }

    public void setType(@NotNull final String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}