package com.opax.sebastian.millionaire.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sebastian on 2015-05-28.
 */
public class QuestionStatistics implements Serializable {
    private List<String> drawnQuestions;
    private List<Boolean> selectedAnswer;

    public QuestionStatistics(){
        drawnQuestions = new ArrayList<>();
        selectedAnswer = new ArrayList<>();
    }

    public void addQuestion(String name){
        drawnQuestions.add(name);
    }

    public void addSelectedAnswer(boolean selectedAnswer){
        this.selectedAnswer.add(selectedAnswer);
    }

    public List<String> getDrawnQuestions(){
        return new ArrayList<>(drawnQuestions);
    }

    public List<Boolean> getSelectedAnswer(){
        return new ArrayList<>(selectedAnswer);
    }
}
