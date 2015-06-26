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
class GamePlan implements Serializable {
    private final List<LevelGame> gameLevel;
    private int nextId;

    GamePlan(){
        gameLevel = new ArrayList();
        nextId = 0;
    }

    public void addNextLevel(int amount, int questionLevel, boolean iSguaranteedPrizePool){
        gameLevel.add(new LevelGame(amount, questionLevel, iSguaranteedPrizePool));
    }

    public int getSize(){
        return gameLevel.size();
    }

    public int getId() { return nextId;}

    public LevelGame getNextLevel(){
        //if(nextId >= gameLevel.size())//zglosic wyjatek
        //return -1;
        return gameLevel.get(nextId++);
    }

    public LevelGame getCurrentLevel(){
        return gameLevel.get(nextId);
    }
}

