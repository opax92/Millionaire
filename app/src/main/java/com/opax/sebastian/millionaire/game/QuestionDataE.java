/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opax.sebastian.millionaire.game;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author Sebastian
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Sebastian
 */


public class QuestionDataE extends QuestionData implements Comparable<QuestionDataE>, Serializable {
    /**
     *Ma za zadanie przechowywać jedno pytanie, klasa jest finalna, stan obiektu jest stały po utworzeniu pytania.
     */
    private final String goodAnswer;
    private final int levelQuestion;
    //private final int id;
    @Override
    public int compareTo(QuestionDataE other)
    {
        return Integer.compare(levelQuestion, other.levelQuestion);
    }

    public QuestionDataE(String nameQuestion, List<String> allAnswers, String goodAnswer, int levelQuestion){
        super(nameQuestion, allAnswers);
        this.goodAnswer = goodAnswer;
        this.levelQuestion = levelQuestion;
        //this.id = id;
    }

    public int getLevelQuestion(){
        return levelQuestion;
    }

    public String getGoodAnswer(){
        return goodAnswer;
    }

    public void removeAnswer(String str){
        super.removeAnswer(str);
    }

}
