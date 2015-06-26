/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opax.sebastian.millionaire.game;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author Sebastian
 */

public class QuestionData implements Serializable {
    /**
     * Klasa dla użytkownika gry.
     */

    private String questionName;
    private List<String> questionAnswers;

    public QuestionData(String questionName, List<String> questionAnswers){
        this.questionName = questionName;
        this.questionAnswers = new ArrayList<>(questionAnswers);
    }

    public String getName(){
        return questionName;
    }

    public List<String> getAnswers(){
        return new ArrayList<>(questionAnswers);
    }

    protected void removeAnswer(String str){
        questionAnswers.remove(str);
    }
}
