/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opax.sebastian.millionaire.game;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author Sebastian
 */
class AllQuestions implements Serializable{
    /**
     * Baza wszystkich pytań.
     * są odpowiednio podzielone według trudności
     * jako kontener wykorzystuje ArrayList
     */
    private final List<QuestionDataE> questions;

    AllQuestions() {
        questions = new ArrayList<>();
    }

    /**
     * Dodaje pytanie do bazy pytań, automatycznie pytanie jest sortowane według trudności
     * @param question przyjmuję pojedyńcze pytanie
     */
    public void addQuestion(QuestionDataE question) {
        questions.add(question);
    }
    /**
     * zwraca jedno losowe pytanie z odpowiednim poziomem trudnosci
     * @param ql poziom pytania, ktore ma zostac zwrocone z bazy pytan
     * @return losowe pytanie, wylosowane z bazy pytań o odpowiednim poziomie trudnosci
     */
    public QuestionDataE getQuestion(int levelQuestion){
        QuestionDataE singleQuestion;

        List<QuestionDataE> onlyThisLevel = new ArrayList<>();
        for(int i = 0; i < questions.size();++i){
            if(questions.get(i).getLevelQuestion() == levelQuestion){
                onlyThisLevel.add(questions.get(i));
            }
        }

        singleQuestion = getRandomQuestion(onlyThisLevel);
        questions.remove(singleQuestion);
        return singleQuestion;
    }
    /**
     * losuje pytanie
     * @param q lista pytan, z ktorych ma zostac wylosowane pytanie
     * @return zwraca wylosowane pytanie
     */
    private QuestionDataE getRandomQuestion(List<QuestionDataE> q){
        int rnd = new Random().nextInt(q.size());

        QuestionDataE tmp = q.get(rnd);
        q.remove(rnd);
        return tmp;
    }
}
