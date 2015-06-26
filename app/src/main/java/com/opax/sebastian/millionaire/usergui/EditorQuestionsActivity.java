package com.opax.sebastian.millionaire.usergui;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.opax.sebastian.millionaire.R;
import com.opax.sebastian.millionaire.game.QuestionDataE;
import com.opax.sebastian.millionaire.game.QuestionDataParceable;
import com.opax.sebastian.millionaire.game.SQLite.QuestionDbAdapter;

import java.util.ArrayList;
import java.util.List;

public class EditorQuestionsActivity extends ActionBarActivity {
    public static String nameDateBase;

    private ListView listView;
    private QuestionDbAdapter questionDbAdapter;
    private ArrayAdapter<Question> listAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_intent);

        listView = (ListView) findViewById(R.id.listView);
        questionDbAdapter = new QuestionDbAdapter(this);
        questionDbAdapter.open();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
                Question nameQ = (Question) parent.getItemAtPosition(position);

                Intent i = new Intent(getApplicationContext(), AddQuestionActivity.class);

                QuestionDataE questionDataE = questionDbAdapter.getQuestionDataFromName(nameQ.getName());
                QuestionDataParceable questionDataParceable = new QuestionDataParceable(questionDataE);
                i.putExtra("QuestionData", questionDataParceable);
                startActivity(i);
            }
        });
        initListView();
    }

    @Override
    protected void onResume(){
        super.onResume();
        questionDbAdapter.open();
        initListView();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(questionDbAdapter != null) questionDbAdapter.close();
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(questionDbAdapter != null) questionDbAdapter.close();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(questionDbAdapter != null) questionDbAdapter.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_questions_intent, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_add_question:
                startActivity(new Intent(getApplicationContext(), AddQuestionActivity.class));
                return true;
            case R.id.action_del_question:
                Question tmp;
                for(int i = 0; i < listAdapter.getCount();++i){
                    tmp = listAdapter.getItem(i);
                    if(tmp.isChecked()) {
                        questionDbAdapter.removeQuestion(tmp.getName());
                        listAdapter.remove(tmp);
                        --i;
                    }
                }
                listAdapter.notifyDataSetChanged();
                return true;
            /*case R.id.action_dell_bd:
                if(nameDateBase != null){
                    getApplicationContext().deleteDatabase(nameDateBase);
                }*/
        }
        return super.onOptionsItemSelected(item);
    }

    private static class Question {
        private String name = "";
        private boolean checked = false;

        public Question(String name ) {
            this.name = name ;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public String toString() {
            return name ;
        }

    }

    private void initListView(){
        List<QuestionDataE> q = questionDbAdapter.getAllQuestions();
        List<Question> questionList = new ArrayList<>();
        for(QuestionDataE e : q){
            questionList.add(new Question(e.getName()));
        }
        listAdapter = new QuestionsArrayAdapter(this, questionList);
        listView.setAdapter(listAdapter);
    }

    private static class QuestionsArrayAdapter extends ArrayAdapter<Question> {
        private LayoutInflater inflater;

        public QuestionsArrayAdapter( Context context, List<Question> questionList ) {
            super(context, R.layout.custom_list_item, R.id.rowTextView, questionList );

            inflater = LayoutInflater.from(context) ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Question question = this.getItem(position);

            CheckBox checkBox;
            TextView textView;

            if (convertView == null){
                convertView = inflater.inflate(R.layout.custom_list_item, null);

                textView = (TextView)convertView.findViewById(R.id.rowTextView);
                checkBox = (CheckBox)convertView.findViewById(R.id.CheckBox01);

                checkBox.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox)v;
                        Question question = (Question)cb.getTag();
                        question.setChecked(cb.isChecked());
                    }
                });
            }
            else {
                textView = (TextView) convertView.findViewById(R.id.rowTextView);
                checkBox = (CheckBox) convertView.findViewById(R.id.CheckBox01);
            }

            checkBox.setTag(question);
            checkBox.setChecked(question.isChecked());
            textView.setText(question.getName());

            return convertView;
        }

    }

}
