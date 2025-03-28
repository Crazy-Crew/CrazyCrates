package com.badbones69.crazycrates.core.config.beans;

import ch.jalu.configme.Comment;
import ch.jalu.configme.beanmapper.ExportName;
import org.jetbrains.annotations.NotNull;

public class ModelData {

    @Comment("The namespace i.e. nexo")
    @ExportName("namespace")
    private String namespace = "";

    @Comment("The id i.e. emerald_helmet")
    @ExportName("id")
    private String id = "";

    public void setNamespace(@NotNull final String namespace) {
        this.namespace = namespace;
    }

    public void setId(@NotNull final String id) {
        this.id = id;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getId() {
        return this.id;
    }

    public ModelData init() {
        this.namespace = "";
        this.id = "";

        return this;
    }
}