package com.opax.sebastian.millionaire.game;

import java.io.Serializable;

/**
 * Created by Sebastian on 2015-05-28.
 */
public class Statistics implements Serializable {
    private final PlayerStatistics playerStatistics;
    private final QuestionStatistics questionStatistics;

    public Statistics(PlayerStatistics playerStatistics, QuestionStatistics questionStatistics){
        this.playerStatistics = playerStatistics;
        this.questionStatistics = questionStatistics;
    }

    public PlayerStatistics getPlayerStatistics(){
        return playerStatistics;
    }

    public QuestionStatistics getQuestionStatistics(){
        return questionStatistics;
    }
}
