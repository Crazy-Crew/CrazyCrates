package com.badbones69.crazycrates.common.impl.crates;

import us.crazycrew.crazycrates.api.impl.crates.IReward;
import java.util.List;

public abstract class Reward implements IReward {

    private final List<String> commands;
    private final List<String> messages;

    private double weight;

    public Reward(final List<String> commands, final List<String> messages, final double weight) {
        this.commands = commands;
        this.messages = messages;

        this.weight = weight;
    }

    @Override
    public final List<String> getCommands() {
        return this.commands;
    }

    @Override
    public final List<String> getMessages() {
        return this.messages;
    }

    @Override
    public void setWeight(final double weight) {
        this.weight = weight;
    }

    @Override
    public final double getWeight() {
        return this.weight;
    }
}