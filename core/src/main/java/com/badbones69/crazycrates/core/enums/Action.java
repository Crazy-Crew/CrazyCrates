package com.badbones69.crazycrates.core.enums;

public enum Action {

    send_message("send_message"),
    send_actionbar("send_actionbar");

    private final String name;

    Action(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}