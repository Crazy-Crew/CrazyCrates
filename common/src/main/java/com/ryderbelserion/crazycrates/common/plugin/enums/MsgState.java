package com.ryderbelserion.crazycrates.common.plugin.enums;

public enum MsgState {

    send_message("send_message"),
    send_actionbar("send_actionbar");

    private final String name;

    MsgState(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}