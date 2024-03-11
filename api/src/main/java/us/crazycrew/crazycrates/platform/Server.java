package us.crazycrew.crazycrates.platform;

import us.crazycrew.crazycrates.api.users.UserManager;

import java.io.File;

public interface Server {

    File getFolder();

    UserManager getUserManager();

}