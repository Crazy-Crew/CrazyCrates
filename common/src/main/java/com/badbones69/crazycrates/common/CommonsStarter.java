package com.badbones69.crazycrates.common;

public class CommonsStarter {

    private static CommonsBuilder commonsBuilder;

    public void run() {
        commonsBuilder = new CommonsBuilder();

        commonsBuilder.run();
    }

    public static CommonsBuilder getCommonsBuilder() {
        return commonsBuilder;
    }
}