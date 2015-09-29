/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opax.sebastian.millionaire.game;

import java.io.Serializable;

/**
 *
 * @author Sebastian
 */
public enum StateGame implements Serializable {
        NOT_STARTED,
        STARTED,
        WAIT_FOR_ANSWER,
        LOSS,
        WIN,
        //:))
}
