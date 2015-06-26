package com.opax.sebastian.millionaire.game;

import java.io.Serializable;

/**
 * Created by Sebastian on 2015-04-06.
 */
final class LevelGame implements Serializable {
    public final int cash;
    public final int level;
    public final boolean iSguaranteedPrizePool;

    LevelGame(int cash, int level, boolean iSguaranteedPrizePool) {
        this.cash = cash;
        this.level = level;
        this.iSguaranteedPrizePool = iSguaranteedPrizePool;
    }
}
