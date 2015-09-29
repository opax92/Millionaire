package com.opax.sebastian.millionaire.usergui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.opax.sebastian.millionaire.R;
import com.opax.sebastian.millionaire.game.GamePlanData;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GamePlanActivity extends ActionBarActivity {

    private GamePlanData g;
    private String[] dataG;
    private int currentPosition;

    private Button btnSave;
    private Button btnOk;
    private Button btnDelete;

    private EditText editNameFile;
    private EditText editQuantityQuestions;
    private EditText editQuestionCash;
    private EditText editQuestionLevel;
    private Spinner spinner;

    private CheckBox checkQuaranteed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_plan);

        initControls();
        currentPosition = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_plan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }


    private void setEnabledControls(boolean e){
        editQuantityQuestions.setEnabled(e);
        editQuestionCash.setEnabled(e);
        editQuestionLevel.setEnabled(e);
        checkQuaranteed.setEnabled(e);
        btnSave.setEnabled(e);
        spinner.setEnabled(e);
    }

    private void cleanControls(){
        editQuantityQuestions.setText("");
        editQuestionLevel.setText("");
        editNameFile.setText("");
        editQuestionCash.setText("");
        spinner.setAdapter(null);
    }

    private void setTextToControls(int id){
        if(dataG == null){
            editQuestionCash.setText("");
            editQuestionLevel.setText("");
            checkQuaranteed.setChecked(false);
            return;
        }
        if(dataG[id] == null){
            editQuestionCash.setText("");
            editQuestionLevel.setText("");
            checkQuaranteed.setChecked(false);
            return;
        }

        String[] s = dataG[id].split(";");

        editQuestionCash.setText(s[0]);
        editQuestionLevel.setText(s[1]);
        checkQuaranteed.setChecked(Boolean.parseBoolean(s[2]));
    }

    private void initControls() {
        editNameFile = (EditText) findViewById(R.id.editNameFile);
        editQuestionLevel = (EditText) findViewById(R.id.editQuestionLevel);
        editQuestionCash = (EditText) findViewById(R.id.editCash);
        editQuantityQuestions = (EditText) findViewById(R.id.editQuantityQuestions);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnOk = (Button) findViewById(R.id.btnOk);
        btnDelete = (Button)findViewById(R.id.btnDelete);
        spinner = (Spinner) findViewById(R.id.spinner);

        checkQuaranteed = (CheckBox) findViewById(R.id.checkQuaranteed);

        setEnabledControls(false);

        spinner.setOnTouchListener(new OnTouchListenerSpinner());
        spinner.setOnItemSelectedListener(new OnItemSelectedListenerSpinner());

        editQuantityQuestions.addTextChangedListener(new TextWatcherEditQuantityQuestions());

        btnOk.setOnClickListener(new OnClickListenerBtnOk());
        btnSave.setOnClickListener(new OnClickListenerBtnSave());
        btnDelete.setOnClickListener(new OnClickListenerBtnDelete());
    }

    //Listenery do Spinnera
    private class  OnTouchListenerSpinner implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                String cash = editQuestionCash.getText().toString();
                String level = editQuestionLevel.getText().toString();
                String quaranteed = Boolean.toString(checkQuaranteed.isChecked());

                dataG[currentPosition] = cash + ";" + level + ";" + quaranteed;
            }
            return false;
        }
    }

    private class OnItemSelectedListenerSpinner implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            currentPosition = position;
            setTextToControls(currentPosition);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    //Listenery do Editow
    private class TextWatcherEditQuantityQuestions implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().isEmpty())
                return;

            List<Integer> questionsNumber = new ArrayList<>();
            int sz = Integer.parseInt(s.toString());
            if(sz <= 0)
                return;
            if (dataG == null)
                dataG = new String[sz];
            if (dataG.length == 0)
                dataG = new String[sz];
            else
                dataG = Arrays.copyOf(dataG, sz);

            for (int i = 1; i <= sz; ++i)
                questionsNumber.add(i);

            ArrayAdapter<Integer> spinnerAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner_item, questionsNumber);
            spinner.setAdapter(spinnerAdapter);
        }
    }

    //Listenery do Buttonow
    private class OnClickListenerBtnOk implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String nameFile = editNameFile.getText().toString();

            if (nameFile.isEmpty()) {
                MyToast.getToast(getApplicationContext(), getApplicationContext().getString(R.string.error_game_plan_activity_empty_field));
                return;
            }
            g = new GamePlanData(getApplicationContext(), nameFile);
            g.readDataFromFile();
            List<Integer> questionsNumbers = new ArrayList<>();

            if (g.size() > 0) {
                btnDelete.setEnabled(true);

                dataG = new String[g.size()];

                for (int i = 1; i <= g.size(); ++i) {
                    questionsNumbers.add(i);
                    dataG[i - 1] = g.getData(i - 1);
                }
                editQuantityQuestions.setText(Integer.toString(g.size()));
                setTextToControls(0);
                ArrayAdapter<Integer> spinnerAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner_item, questionsNumbers);
                spinner.setAdapter(spinnerAdapter);

                setEnabledControls(true);
            } else {
                MyToast.getToast(getApplicationContext(),  getApplicationContext().getString(R.string.error_game_plan_activity_nf));
                btnDelete.setEnabled(false);
                g = new GamePlanData(getApplicationContext(), nameFile);
                questionsNumbers.add(1);
                editQuantityQuestions.setText("1");
                dataG = new String[1];
                spinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner_item, questionsNumbers));
                setEnabledControls(true);
            }
        }
    }

    private class OnClickListenerBtnSave implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String cash = editQuestionCash.getText().toString();
            String level = editQuestionLevel.getText().toString();
            String quaranteed = Boolean.toString(checkQuaranteed.isChecked());

            if(cash.isEmpty() || level.isEmpty() || quaranteed.isEmpty()) {
                MyToast.getToast(getApplicationContext(), getApplicationContext().getString(R.string.error_game_plan_activity_gpe));
                return;
            }

            dataG[currentPosition] = cash + ";" + level + ";" + quaranteed;
            g.clean();

            for (int i = 0; i < dataG.length; ++i)
                g.addData(dataG[i]);

            if(g.writeDataToFile()) {
                MyToast.getToast(getApplicationContext(), getApplicationContext().getString(R.string.info_game_plan_activity_gpe));
            }
            else
                MyToast.getToast(getApplicationContext(), getApplicationContext().getString(R.string.error_game_plan_activity_gpe));
        }
    }

    private class OnClickListenerBtnDelete implements View.OnClickListener{
        @Override
        public void onClick(View v){
            if(g != null)
                if(getApplicationContext().deleteFile(g.getName())) {
                    MyToast.getToast(getApplicationContext(), getApplicationContext().getString(R.string.value_game_plan_activity_delete_true));
                    cleanControls();
                    setEnabledControls(false);
                }
            else
                MyToast.getToast(getApplicationContext(), getApplicationContext().getString(R.string.value_game_plan_activity_delete_False));
        }
    }
}
