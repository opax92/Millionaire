/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opax.sebastian.millionaire.game;
import com.opax.sebastian.millionaire.game.random.DistributedRandomNumberGenerator;

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
public final class Entertainment implements Serializable{
    private int nextId; //aktualna pozycja pytania na tle wszystkich pytan
    private int guaranteedWinCash;//gwarantowana wygrana
    private int currentWinCash;//aktualna wygrana

    private Statistics statistics;//statystyki
    private StateGame gameState; //stan gry
    private QuestionDataE question;//aktualne pytanie podczas rozgrywki
    private AllQuestions allQuestions;//baza wszystkich pytan
    private GamePlan gamePlan;//plan rozgrywki(ilosc pytan, za jaka stawke itd)
    private final LifeLines lifeLines;

    public Entertainment() {
        gamePlan = new GamePlan();
        allQuestions = new AllQuestions();
        lifeLines = new LifeLines();
        statistics = new Statistics(new PlayerStatistics("anonim"), new QuestionStatistics());
        gameState = StateGame.NOT_STARTED;
        nextId = 0;
    }


    public Entertainment(String playerName){
        this();
        statistics = new Statistics(new PlayerStatistics(playerName), new QuestionStatistics());
    }


    /**
     * dodaje nastepny poziom pytania, tzn range pytania, kwote za ktore pytanie
     * jest zadane oraz czy kwota jest kwota gwarantowana
     *
     * @param amount kwota wygrana po dobrej odpowiedzi na pytanie
     * @param questionLevel poziom pytania(latwy, sredni, trudny)
     * @param iSguaranteedPrizePool czy kwota po dobrej odpowiedzi na pytanie
     * jest gwarantowana
     */
    public void addNextLevel(int amount, int questionLevel, boolean iSguaranteedPrizePool) {
        gamePlan.addNextLevel(amount, questionLevel, iSguaranteedPrizePool);
    }

    /**
     * Dodaje pytanie do rozgrywki
     *
     * @param question pytanie, obiekt klasy QuestionDataE
     */
    public void addQuestion(QuestionDataE question) {
        allQuestions.addQuestion(question);
    }

    /**
     * zmiana statusy gry, inicjacja odpowiednich skladowych by przygotowac sie
     * do rozgrywki
     *
     * @return jesli wszystko wporzadku, zwraca true
     */
    public boolean startGame() {
        if (gameState == StateGame.NOT_STARTED) {
            nextId = 0;
            gameState = StateGame.STARTED;
            return true;
        }
        return false;//gdy gra się juz zakonczyla, nie ma mozliwosci jej zresetowania
        //trzeba wykorzystac nowa instancje gry
    }

    public void stopGame() {//zatrzymujemy pieniadze i nie gramy dalej
        if(gameState == StateGame.WAIT_FOR_ANSWER)
            statistics.getQuestionStatistics().addSelectedAnswer(false);
        gameState = StateGame.WIN;
        nextId = 0;
        gamePlan = null;
        allQuestions = null;
    }

    /**
     * pobiera nastepne pytanie wedlug planu rozgrywki. Pobrac pytanie mozna
     * tylko gdy gra jest rozpoczeta
     *
     * @return obiekt klasy QuestionData, dla uzytku zewnetrznego, zawiera nazwe
     * pytania oraz odpowiedzi w tablicy String'ow
     */
    public QuestionData getNextQuestion() {
        //rozgrywka musi byc rozpoczeta
        if (gameState == StateGame.STARTED) {
            gameState = StateGame.WAIT_FOR_ANSWER;
            //ustawiamy aktualne pytanie na losowe pytanie pobrane z bazy pytan
            question = allQuestions.getQuestion(gamePlan.getCurrentLevel().level);
            QuestionData d = new QuestionData(question.getName(), question.getAnswers());
            statistics.getQuestionStatistics().addQuestion(d.getName());
            return d;
        }
        return null;//wyjatek!
    }

    public String getGoodAnswer(){//mozna pobrac poprawna odpowiedz, tylko gdy przegralismy
        if(gameState == StateGame.LOSS)
            return question.getGoodAnswer();
        return null;
    }

    public int getQuestionNumber(){
        return gamePlan.getId() + 1;
    }

    public int getQuestionCount(){
        return gamePlan.getSize();
    }

    public Statistics getStatistics(){//statystyki gracza mozna pobrac po zakonczeniu rozgrywki.
        if(gameState == StateGame.LOSS || gameState == StateGame.WIN)
            return statistics;
        return null;
    }

    public QuestionData getCurrentQuestion(){
        return new QuestionData(question.getName(), question.getAnswers());
    }

    /**
     * zwraca, czy odpowiedz jest poprawna,
     *
     * @param answer
     * @return
     */
    public StateGame checkAnswer(String answer) {
        if (gameState == StateGame.WAIT_FOR_ANSWER) {
            String goodAnswer = question.getGoodAnswer();
            statistics.getQuestionStatistics().addSelectedAnswer(answer.equals(goodAnswer));
            if (answer.equals(goodAnswer)) {//prawidłow odpowiedz, etap wyzej.
                LevelGame l = gamePlan.getNextLevel();
                gameState = StateGame.STARTED;
                nextId++;
                statistics.getPlayerStatistics().incrementGoodAnswers();

                if (nextId == gamePlan.getSize()) {//wygrana
                    guaranteedWinCash = l.cash;
                    currentWinCash = l.cash;
                    statistics.getPlayerStatistics().setWinCash(currentWinCash);
                    stopGame();
                    return gameState;
                } else {//jesli dobra odpowiedz, lecz nie koniec gry
                    if (l.iSguaranteedPrizePool) {
                        guaranteedWinCash = l.cash;
                    }
                    currentWinCash = l.cash;
                    gameState = StateGame.STARTED;
                }
            } else if (!answer.equals(goodAnswer)) {//jesli nie prawidlowa odpowiedz, przegralismy
                gameState = StateGame.LOSS;
                currentWinCash = guaranteedWinCash;
                statistics.getPlayerStatistics().incrementBadAnswers();
                statistics.getPlayerStatistics().setWinCash(currentWinCash);
                return gameState;
            }
        }
        return gameState;
    }

    public StateGame getStateGame() {
        return gameState;
    }

    public int getQuaranteedWin() {
        return guaranteedWinCash;
    }

    public int getCurrentWin() {
        return currentWinCash;
    }

    public List<String> getTwoWrongAnswers() {
        return lifeLines.getTwoWrongAnswers();
    }

    public String phoneHelp() {
        return lifeLines.phoneFriend();
    }

    public Map<String, Integer> getAudienceAnswers(){
        return lifeLines.getAudienceAnswers();
    }

    private class LifeLines implements Serializable{
        private boolean fiftyFifty = false;
        private boolean phoneFriend = false;
        private boolean askAudience = false;

        List<String> getTwoWrongAnswers() {
            if (fiftyFifty)
                return null;
            fiftyFifty = true;

            List<String> badAnswers = new ArrayList<>();
            List<String> allAnswers = new ArrayList<>(question.getAnswers());

            int rnd;
            int i = 0;
            while (i != 2) {//dwa pytania
                rnd = new Random().nextInt(allAnswers.size());
                if (!allAnswers.get(rnd).equals(question.getGoodAnswer())) {
                    badAnswers.add(allAnswers.get(rnd));
                    allAnswers.remove(rnd);
                    i++;
                }
            }

            for(String s : badAnswers){
                question.removeAnswer(s);
            }

            return badAnswers;
        }

        String phoneFriend() {
            if (phoneFriend)
                return null;
            phoneFriend = true;

            int rnd = new Random().nextInt(100) + 1;
            String res = "";
            if (rnd <= 75) {
                res = question.getGoodAnswer();
            } else {
                List<String> allAnswers = new ArrayList<>(question.getAnswers());
                for (String s : allAnswers) {
                    if (!s.equals(question.getGoodAnswer())) {
                        res = s;
                        break;
                    }
                }
            }
            return res;
        }

        Map<String, Integer> getAudienceAnswers() {
            if(askAudience)
                return null;
            askAudience = true;

            Map<String, Integer> audienceMap = new HashMap<>();

            DistributedRandomNumberGenerator drng = new DistributedRandomNumberGenerator();

            List<String> allAnswers = new ArrayList<>(question.getAnswers());

            drng.addNumber(question.getGoodAnswer(), 0.75d);//odp prawidlowa

            for(int i = 0; i < allAnswers.size();++i) {
                if (!allAnswers.get(i).equals(question.getGoodAnswer()))
                    drng.addNumber(allAnswers.get(i), 0.50d);//odp bledna
            }

            for(String s : allAnswers)
                audienceMap.put(s, 0);

            String random;

            for (int i = 0; i < 100; i++) {
                random = drng.getDistributedRandomNumber();
                audienceMap.put(random, audienceMap.get(random) + 1);
            }
            return audienceMap;
        }
    }
}
