package us.crazycrew.crazycrates.platform;

import us.crazycrew.crazycrates.api.users.UserManager;

import java.io.File;
import java.util.logging.Logger;

public interface Server {

    File getFolder();

    Logger getLogger();

    UserManager getUserManager();

}