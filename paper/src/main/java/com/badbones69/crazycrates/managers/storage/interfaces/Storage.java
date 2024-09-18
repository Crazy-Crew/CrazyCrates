package com.badbones69.crazycrates.managers.storage.interfaces;

import com.badbones69.crazycrates.common.enums.Environment;
import com.badbones69.crazycrates.common.interfaces.Manifest;
import java.io.File;

@Manifest(environment = Environment.DEVELOPMENT)
public interface Storage<T> {

    T getAccess();

    default void start() {}

    default void save() {}

    default void reload() {}

    File getFile();

}