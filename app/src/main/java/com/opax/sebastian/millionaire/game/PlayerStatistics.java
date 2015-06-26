package com.opax.sebastian.millionaire.game;

import java.io.Serializable;

/**
 * Created by Sebastian on 2015-05-28.
 */
public class PlayerStatistics implements Serializable {
    private final String name;
    private int goodAnswersCount = 0;
    private int badAnswersCount = 0;
    private int winCash = 0;

    public PlayerStatistics(String name){
        this.name = name;
    }

    public void incrementGoodAnswers(){
        ++goodAnswersCount;
    }

    public void incrementBadAnswers(){
        ++badAnswersCount;
    }

    public void setWinCash(int winCash){
        this.winCash = winCash;
    }

    public String getName(){
        return name;
    }

    public int getGoodAnswersCount(){
        return goodAnswersCount;
    }

    public int getBadAnswersCount(){
        return badAnswersCount;
    }

    public int getWinCash(){
        return winCash;
    }
}
