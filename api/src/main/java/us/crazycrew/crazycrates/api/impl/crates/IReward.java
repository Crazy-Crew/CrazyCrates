package us.crazycrew.crazycrates.api.impl.crates;

import java.util.List;

public interface IReward {

    List<String> getCommands();

    List<String> getMessages();

    double getWeight();

    void setWeight(double weight);

    double getChance();

}