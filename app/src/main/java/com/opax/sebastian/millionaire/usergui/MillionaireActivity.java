package com.opax.sebastian.millionaire.usergui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.Intent;
import com.opax.sebastian.millionaire.R;
import com.opax.sebastian.millionaire.game.*;
import com.opax.sebastian.millionaire.game.SQLite.QuestionDbAdapter;

public class MillionaireActivity extends ActionBarActivity {
    public static String gamePlanName;
    public static String gameDataBaseName;

    private static final int QUESTIONS_COUNT = 4;

    private Button buttonA;
    private Button buttonB;
    private Button buttonC;
    private Button buttonD;
    private ImageButton buttonAudience;
    private ImageButton buttonFifty;
    private ImageButton buttonFriend;
    private TextView textQuestion;
    private TextView txtCurrentWin;
    private TextView txtQuestionId;

    private Entertainment entertainment = new Entertainment();//klasa z logika gry
    private QuestionDbAdapter questionDbAdapter;//adapter do bazy danych SQLite

    private Handler handler = new Handler();//do watkow


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_millionaire);

        initControls();
        setEnabledAnswersButtons(false);

        initGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_millionaire, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        switch(id){
           /* case R.id.action_game_save_game:
                if(entertainment.getStateGame() == StateGame.WAIT_FOR_ANSWER){//zapisac gre mozna tylko podczac rozgrywki
                    saveEntertainment();
                }*/
        }
        return super.onOptionsItemSelected(item);
    }


    private void initControls(){

        buttonA = (Button)findViewById(R.id.button);
        buttonB = (Button)findViewById(R.id.button2);
        buttonC = (Button)findViewById(R.id.button3);
        buttonD = (Button)findViewById(R.id.button4);

        buttonFifty = (ImageButton)findViewById(R.id.imageButton2);
        buttonFriend = (ImageButton)findViewById(R.id.imageButton3);
        buttonAudience = (ImageButton)findViewById(R.id.imageButton);

        textQuestion = (TextView)findViewById(R.id.textView);
        txtCurrentWin = (TextView)findViewById(R.id.txtcurrentwin);
        txtQuestionId = (TextView)findViewById(R.id.txtquestionid);

        buttonA.setOnClickListener(new ButtonsAnswer());
        buttonB.setOnClickListener(new ButtonsAnswer());
        buttonC.setOnClickListener(new ButtonsAnswer());
        buttonD.setOnClickListener(new ButtonsAnswer());
        buttonFifty.setOnClickListener(new ButtonsLifeLines("fiftyfifty"));
        buttonFriend.setOnClickListener(new ButtonsLifeLines("phonefriend"));
        buttonAudience.setOnClickListener(new ButtonsLifeLines("audience"));
    }

    private boolean checkGamePlanWithQuestionBase(List<QuestionDataE> l, GamePlanData gd){
        //sprawdza, czy baza pytan jest odpowiednia do danego poziomu rozgrywek
        //przykladowo, nie ma w poziomie rozgrywek losowania pytania z poziomu
        //ktory nie istnieje w bazie pytan itd

        Map<Integer, Integer> mapQuestionBase = new HashMap<>();
        //korzystamy z mapy, klucz, wartosc, kluczem jest poziom pytania, wartoscia ilosc poziomow w bazie danych
        List<Integer> allLevels = new ArrayList<>();//wszystkie poziomy, w bazie i w planie rozgrywki

        for(QuestionDataE q : l) {
            Integer old = mapQuestionBase.get(q.getLevelQuestion());
            if (old == null) {
                mapQuestionBase.put(q.getLevelQuestion(), 1);
                allLevels.add(q.getLevelQuestion());
            }
            else
                mapQuestionBase.put(q.getLevelQuestion(), ++old);
        }

        Map<Integer, Integer> mapGamePlan = new HashMap<>();//to samo, tylko dla planu rozgrywki

        String[] split;
        int levelValue;

        for(int i = 0; i < gd.size();++i) {
            split = gd.getData(i).split(";");
            levelValue = Integer.parseInt(split[1]);
            Integer old = mapGamePlan.get(levelValue);
            if (old == null) {
                mapGamePlan.put(levelValue, 1);
                allLevels.add(levelValue);
            }
            else
                mapGamePlan.put(levelValue, ++old);
        }

        //teraz musimy porownac wartosci
        for(Integer i : allLevels){
            //pierwszy warunek, jesli dany poziom wystepuje w planie rozgrywki
            //a nie wystepuje w bazie pytan zwracamy false, logiczne
            if(mapGamePlan.get(i) != null && mapQuestionBase.get(i) == null)
                return false;
            Integer questionCountInBase = mapQuestionBase.get(i);
            Integer questionCountInGP = mapGamePlan.get(i);

            //drugi warunek, jesli dany poziom jest w planie rozgrywki i w bazie danych
            //(poziom moze byc w bazie danych, a nie musi byc w planie rozgrywki)
            //nastepnie sprawdzamy czy ilosc poziomow, ktore sa w bazie pytan nie jest mniejsza
            //niz ilosc poziomow w planie rozgrywki, jesli tak jest false
            if(questionCountInBase != null && questionCountInGP != null)
                if(questionCountInBase - questionCountInGP < 0)
                    return false;
        }

        return true;//gratki, zkonfigurowales dobry plan rozgrywki xd
    }

    private void initGame(){
        questionDbAdapter = new QuestionDbAdapter(this);
        questionDbAdapter.open();

        List<QuestionDataE> answers = new ArrayList<>(questionDbAdapter.getAllQuestions());//pobieramy wszystkie pytania z bazy danych

        if(answers.isEmpty()) {
            MyToast.getToast(getApplicationContext(), getApplicationContext().getString(R.string.error_millionaire_activity2));
            finish();
            return;
        }
        questionDbAdapter.close();

        GamePlanData g = new GamePlanData(getApplicationContext(), gamePlanName);//pobieramy z pliku plan rozgrywki

        if(!g.readDataFromFile()) {
            MyToast.getToast(getApplicationContext(), getApplicationContext().getString(R.string.error_millionaire_activity1));
            getApplicationContext().deleteFile(gamePlanName);
            finish();
            return;
        }

        if(!checkGamePlanWithQuestionBase(answers, g)) {
            MyToast.getToast(getApplicationContext(), getApplicationContext().getString(R.string.error_millionaire_activity2));
            finish();
            return;
        }

        for(int i = 0; i < g.size();++i){
            String[] d = g.getData(i).split(";");
            entertainment.addNextLevel(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Boolean.parseBoolean(d[2]));
        }


        for (QuestionDataE q : answers)
            entertainment.addQuestion(q);

        entertainment.startGame();

        QuestionData d = entertainment.getNextQuestion();//pobieramy pierwsze pytaie

        handler.postDelayed(new ShowAnswers(d, 1500), 1500);//obsluge wyswietlenia pytan przekazujemy innemu watkowi
                                                            //poniewaz chcemy uzyskac efekt powolnego pokazywania
                                                            //pytania i odpowiedzi, w przypadku glownego watku aplikacja
                                                            //mogla by sie zaciac
    }

    private void cleanAllViews(){
        buttonA.setText("");
        buttonB.setText("");
        buttonC.setText("");
        buttonD.setText("");
        textQuestion.setText("");
    }

    private void setEnabledAnswersButtons(boolean enabled){
        buttonA.setEnabled(enabled);
        buttonB.setEnabled(enabled);
        buttonC.setEnabled(enabled);
        buttonD.setEnabled(enabled);
        buttonAudience.setEnabled(enabled);
        buttonFriend.setEnabled(enabled);
        buttonFifty.setEnabled(enabled);
    }

    private void setTxtQuestionId(){
        txtQuestionId.setText(getApplicationContext().getString(R.string.value_millionaire_activity_question) + " " + Integer.toString(entertainment.getQuestionNumber()) +
                "/" + Integer.toString(entertainment.getQuestionCount()) + ", " +
                getApplicationContext().getString(R.string.value_millionaire_activity_guaranteed) + " " + Integer.toString(entertainment.getQuaranteedWin()));

    }

    private class ButtonsLifeLines implements View.OnClickListener{//do obslugi kol ratunkowych
        private final String name;

        public ButtonsLifeLines(String name){
            this.name = name;
        }

        @Override
        public void onClick(View v){
            switch (name){
                case "fiftyfifty"://pol na pol
                    buttonFifty.setImageResource(R.drawable.fiftyfiftyno);//zmieniamy ikonke przycisku
                    List<String> wrongAnswers = entertainment.getTwoWrongAnswers();//pobieramy zle odpowiedzi
                    if(wrongAnswers == null) {//jesli zwraca null, to znaczy ze pytanie juz zostalo wykorzystane
                        MyToast.getToast(getApplicationContext(), getApplicationContext().getString(R.string.error_millionaire_activity_life_line_use));
                        return;
                    }
                    //przechodzimy przez kazda odpowiedz, ktora jest na przyciskach
                    //jesli sie zgadza z getTwoWrongsAnswers(), usuwamy ja
                    for(String s : wrongAnswers) {
                        if (buttonA.getText().toString().equals(s)) {
                            buttonA.setText("");
                            buttonA.setEnabled(false);
                        }
                        if (buttonB.getText().toString().equals(s)) {
                            buttonB.setText("");
                            buttonB.setEnabled(false);
                        }
                        if (buttonC.getText().toString().equals(s)) {
                            buttonC.setText("");
                            buttonC.setEnabled(false);
                        }
                        if (buttonD.getText().toString().equals(s)) {
                            buttonD.setText("");
                            buttonD.setEnabled(false);
                        }
                    }
                    break;

                case "phonefriend"://telefon do przyjaciela
                    buttonFriend.setImageResource(R.drawable.phonefriendno);
                    String friendAnswer = entertainment.phoneHelp();
                    if(friendAnswer == null){
                        MyToast.getToast(getApplicationContext(), getApplicationContext().getString(R.string.error_millionaire_activity_life_line_use));
                        return;
                    }

                    showSimpleAlertDialog(getApplicationContext().getString(R.string.value_millionaire_activity_ll_phone_title),
                            getApplicationContext().getString(R.string.value_millionaire_activity_ll_phone_msg1)
                                    + " " + friendAnswer + " " + getApplicationContext().getString(R.string.value_millionaire_activity_ll_phone_msg2));

                    break;
                case "audience"://pytanie do publicznosci
                    buttonAudience.setImageResource(R.drawable.atano);
                    Map<String, Integer> answersKeys = entertainment.getAudienceAnswers();

                    if(answersKeys == null){
                        MyToast.getToast(getApplicationContext(), getApplicationContext().getString(R.string.error_millionaire_activity_life_line_use));
                        return;
                    }

                    String[] keys = new String[QUESTIONS_COUNT];
                    keys[0] = buttonA.getText().toString();
                    keys[1] = buttonB.getText().toString();
                    keys[2] = buttonC.getText().toString();
                    keys[3] = buttonD.getText().toString();

                    String res = "";

                    for(int i = 0; i < QUESTIONS_COUNT; ++i)
                        if(answersKeys.get(keys[i]) != null)
                            res += getApplicationContext().getString(R.string.value_question) + " " +
                            keys[i] + ": " + answersKeys.get(keys[i]) + "%" + "\n";

                    showSimpleAlertDialog(getApplicationContext().getString(R.string.value_millionaire_activity_ll_audience_title), res);
                    break;
            }
        }
    }

    private void showSimpleAlertDialog(String title, String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MillionaireActivity.this);

        alertDialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private class CheckAnswer implements Runnable{//klasa do zaznaczania odpowiedzi, wywoluje efekt kolorowego zaznaczania
        private View view;//referencja do obiektu view, w tym wypadku przycisku
        private int marker;//by klasa wiedziala, ktory raz jest wywolywana

        public CheckAnswer(View view){
            this.view = view;
            marker = 0;
        }

        private void toggleColor(int color){
            ((Button)view).setTextColor(color);
            ++marker;
            handler.postDelayed(this, 250);
        }

        private void checkColorGoodAnswer(){
            String goodAnswer = entertainment.getGoodAnswer();
            if(goodAnswer == null)
                return;
            goodAnswer = goodAnswer.toLowerCase();
            if(buttonA.getText().toString().toLowerCase().equals(goodAnswer))
                buttonA.setTextColor(Color.GREEN);
            if(buttonB.getText().toString().toLowerCase().equals(goodAnswer))
                buttonB.setTextColor(Color.GREEN);
            if(buttonC.getText().toString().toLowerCase().equals(goodAnswer))
                buttonC.setTextColor(Color.GREEN);
            if(buttonD.getText().toString().toLowerCase().equals(goodAnswer))
                buttonD.setTextColor(Color.GREEN);
        }

        @Override
        public void run(){
            switch (entertainment.getStateGame()){
                case STARTED://poprawna odpowiedz, jedziemy dalej
                case WIN:
                    switch(marker){
                        case 0:
                            toggleColor(Color.GREEN);
                            break;
                        case 1:
                            toggleColor(Color.YELLOW);
                            break;
                        case 2:
                            toggleColor(Color.GREEN);
                            break;
                        case 3:
                            toggleColor(Color.YELLOW);
                            break;
                        case 4:
                            toggleColor(Color.GREEN);
                            break;
                        default:
                            handler.removeCallbacks(this);
                            txtCurrentWin.setText(Integer.toString(entertainment.getCurrentWin()));
                            if(entertainment.getStateGame() == StateGame.WIN)
                            {
                                showSimpleAlertDialog(getApplicationContext().getString(R.string.value_millionaire_activity_win_title), getApplicationContext().getString(R.string.value_millionaire_activity_win));
                                return;
                            }
                            QuestionData d = entertainment.getNextQuestion();
                            toggleColor(Color.WHITE);
                            cleanAllViews();
                            handler.postDelayed(new ShowAnswers(d, 1000), 1000);
                            break;
                    }
                    break;
                case LOSS:
                    checkColorGoodAnswer();
                    switch(marker){
                        case 0:
                            toggleColor(Color.RED);
                            break;
                        case 1:
                            toggleColor(Color.YELLOW);
                            break;
                        case 2:
                            toggleColor(Color.RED);
                            break;
                        case 3:
                            toggleColor(Color.YELLOW);
                            break;
                        case 4:
                            toggleColor(Color.RED);
                            break;
                        default:
                            txtCurrentWin.setText(Integer.toString(entertainment.getCurrentWin()));
                            handler.removeCallbacks(this);
                            break;
                    }
            }
        }
    }

    private class ShowAnswers implements Runnable{
        private final String name;
        private final List<String> answers;
        private final int wait;
        private int marker;

        public ShowAnswers(QuestionData q, int wait){
            name = q.getName();
            answers = q.getAnswers();
            this.wait = wait;
            marker = 0;
        }

        @Override
        public void run(){
            setEnabledAnswersButtons(false);
            setTxtQuestionId();
            switch(marker){
                case 0:
                    textQuestion.setText(name);
                    ++marker;
                    handler.postDelayed(this, wait);
                    break;
                case 1:
                    buttonA.setText(answers.get(0));
                    ++marker;
                    handler.postDelayed(this, wait);
                    break;
                case 2:
                    buttonB.setText(answers.get(1));
                    ++marker;
                    handler.postDelayed(this, wait);
                    break;
                case 3:
                    buttonC.setText(answers.get(2));
                    ++marker;
                    handler.postDelayed(this, wait);
                    break;
                case 4:
                    buttonD.setText(answers.get(3));
                    ++marker;
                    handler.postDelayed(this, 0);
                    break;
                default:
                    marker = 0;
                    setEnabledAnswersButtons(true);
                    handler.removeCallbacks(this);
            }
        }
    }

    //Listener
    private class ButtonsAnswer implements View.OnClickListener {
        private class ResDialogForPositive implements DialogInterface.OnClickListener {
            private final View view;

            public ResDialogForPositive(View view){
                this.view = view;
            }

            public void onClick(DialogInterface dialog, int id) {
                CheckAnswer checkAnswer = new CheckAnswer(view);
                entertainment.checkAnswer(((Button)view).getText().toString());
                setEnabledAnswersButtons(false);
                ((Button) view).setTextColor(Color.YELLOW);
                handler.postDelayed(checkAnswer, 3000);
            }
        }

        @Override
        public void onClick(View v) {
            ResDialogForPositive resDialogForPositive = new ResDialogForPositive(v);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MillionaireActivity.this);

            alertDialogBuilder
                    .setTitle(getApplicationContext().getString(R.string.ad_millionaire_activity_your_answer))
                    .setMessage(getApplicationContext().getString(R.string.ad_millionaire_activity_message1))
                    .setCancelable(false)
                    .setPositiveButton(getApplicationContext().getString(R.string.ad_yes), resDialogForPositive)
                    .setNegativeButton(getApplicationContext().getString(R.string.ad_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }
    }

}
