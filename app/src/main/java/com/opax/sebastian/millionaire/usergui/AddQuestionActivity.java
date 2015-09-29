package com.opax.sebastian.millionaire.usergui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.opax.sebastian.millionaire.R;
import com.opax.sebastian.millionaire.game.QuestionDataE;
import com.opax.sebastian.millionaire.game.QuestionDataParceable;
import com.opax.sebastian.millionaire.game.SQLite.QuestionDbAdapter;

import java.util.*;

public class AddQuestionActivity extends Activity {

    private Button buttonAdd;
    private EditText editContentQuestion;
    private EditText editLevel;
    private EditText editA;
    private EditText editB;
    private EditText editC;
    private EditText editD;
    private RadioGroup radioGroup;

    private QuestionDataParceable questionDataParceable;
    private QuestionDbAdapter questionDbAdapter = new QuestionDbAdapter(this);
    private int idOptionA;
    private int idOptionB;
    private int idOptionC;
    private int idOptionD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        Intent i = getIntent();
        questionDataParceable = i.getParcelableExtra("QuestionData");
        if(questionDataParceable != null)
            initControls(questionDataParceable);
        else
            initControls(null);
    }

    private void initControls(QuestionDataParceable q){

        buttonAdd = (Button) findViewById(R.id.buttonOk);
        editContentQuestion = (EditText)findViewById(R.id.editContentQuestion);
        editLevel = (EditText)findViewById(R.id.editLevel);
        editA = (EditText) findViewById(R.id.editA);
        editB = (EditText) findViewById(R.id.editB);
        editC = (EditText) findViewById(R.id.editC);
        editD = (EditText) findViewById(R.id.editD);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        idOptionA =  ((findViewById(R.id.radioButtonA)).getId());
        idOptionB =  ((findViewById(R.id.radioButtonB)).getId());
        idOptionC =  ((findViewById(R.id.radioButtonC)).getId());
        idOptionD =  ((findViewById(R.id.radioButtonD)).getId());

        if(q != null) {
            editContentQuestion.setText(q.name);
            editA.setText(q.answers[0]);
            editB.setText(q.answers[1]);
            editC.setText(q.answers[2]);
            editD.setText(q.answers[3]);
            editLevel.setText(Integer.toString(q.level));
            buttonAdd.setText(R.string.value_add_question_activity_change_question);
            buttonAdd.setOnClickListener(new listenerButtonOk("upgrade"));

            if (q.answers[0].equals(q.goodAnswer))
                radioGroup.check(idOptionA);
            if (q.answers[1].equals(q.goodAnswer))
                radioGroup.check(idOptionB);
            if (q.answers[2].equals(q.goodAnswer))
                radioGroup.check(idOptionC);
            if (q.answers[3].equals(q.goodAnswer))
                radioGroup.check(idOptionD);
        }
            else
                buttonAdd.setOnClickListener(new listenerButtonOk("add"));

    }

    private QuestionDataE getQuestionFromControls(){
        String name, goodAnswer = "";
        List<String> ans = new ArrayList<>();
        int level;

        name = editContentQuestion.getText().toString();
        ans.add(editA.getText().toString().trim());
        ans.add(editB.getText().toString().trim());
        ans.add(editC.getText().toString().trim());
        ans.add(editD.getText().toString().trim());

        if(editLevel.getText().toString().isEmpty())
            return null;

        level = Integer.parseInt(editLevel.getText().toString());

        if (radioGroup.getCheckedRadioButtonId() == idOptionA)
            goodAnswer = editA.getText().toString();
        if (radioGroup.getCheckedRadioButtonId() == idOptionB)
            goodAnswer = editB.getText().toString();
        if (radioGroup.getCheckedRadioButtonId() == idOptionC)
            goodAnswer = editC.getText().toString();
        if (radioGroup.getCheckedRadioButtonId() == idOptionD)
            goodAnswer = editD.getText().toString();

        return new QuestionDataE(name, ans, goodAnswer, level);
    }

    private class listenerButtonOk implements View.OnClickListener{
        private String opt;

        public listenerButtonOk(String opt){
            this.opt = opt;
        }

        @Override
        public void onClick(View v){
            QuestionDataE q = getQuestionFromControls();
            if(q == null) {
                MyToast.getToast(getApplicationContext(), getApplicationContext().getString(R.string.error_add_question_activity_wrong_values));
                return;
            }

            List<String> answers = q.getAnswers();

            Set<String> set = new HashSet<>(answers);//sprawdzamy, czy w liscie nie ma duplikatow
            if(set.size() < answers.size()){
                MyToast.getToast(getApplicationContext(), getApplicationContext().getString(R.string.error_add_question_activity_duplicate_answers));
                return;
            }

            questionDbAdapter.open();

            if(opt.equals("upgrade")){
                if(questionDbAdapter.upgradeQuestion(q, questionDataParceable.name) > 0)
                    MyToast.getToast(getApplicationContext(), getApplicationContext().getString(R.string.value_add_question_activity_good_change));
                else
                    MyToast.getToast(getApplicationContext(), getApplicationContext().getString(R.string.error_add_question_activity_inner_error));
            }
            else if(opt.equals("add")) {
                if (questionDbAdapter.isQuestion(q.getName()))
                    MyToast.getToast(getApplicationContext(), getApplicationContext().getString(R.string.error_add_question_activity_double_question));
                else {
                    if(questionDbAdapter.addQuestion(q) > 0)
                        MyToast.getToast(getApplicationContext(), getApplicationContext().getString(R.string.value_add_question_activity_good_values));
                    else
                        MyToast.getToast(getApplicationContext(), getApplicationContext().getString(R.string.error_add_question_activity_inner_error));
                }
            }
            questionDbAdapter.close();
        }
    }
}
