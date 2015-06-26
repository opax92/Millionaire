package com.opax.sebastian.millionaire.usergui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.opax.sebastian.millionaire.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    private Spinner spinnerDataBases;
    private Spinner spinnerGamePlanes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initControls();
    }

    protected void onResume(){
        super.onResume();
        initControls();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        Intent i;
        switch(id){
            case R.id.action_new_game:
                i = new Intent(getApplicationContext(), MillionaireActivity.class);
                //i.putExtra("readGame", "false");
                startActivity(i);
                return true;
            case R.id.action_question_editor:
                EditorQuestionsActivity.nameDateBase = (String)spinnerDataBases.getSelectedItem();
                i = new Intent(getApplicationContext(), EditorQuestionsActivity.class);
                startActivity(i);
                return true;
            case R.id.action_game_plan_editor:
                i = new Intent(getApplicationContext(), GamePlanActivity.class);
                startActivity(i);
                return true;
            /*case R.id.action_load_game:
                i = new Intent(getApplicationContext(), MillionaireActivity.class);
                i.putExtra("readGame", "true");
                startActivity(i);
                return true;*/
        }

        return super.onOptionsItemSelected(item);
    }

    private void initControls() {
        spinnerGamePlanes = (Spinner) findViewById(R.id.spinnergameplan);
        spinnerDataBases = (Spinner) findViewById(R.id.spinnerdatabases);

        String[] files = getApplicationContext().fileList();
        List<String> dataBases = new ArrayList<>(Arrays.asList(getApplicationContext().databaseList()));
        Iterator<String> iterator = dataBases.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().contains("journal"))
                iterator.remove();
        }

        ArrayAdapter<String> spinnerGPAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner_item, files);
        ArrayAdapter<String> spinnerDBAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner_item, dataBases);

        spinnerGamePlanes.setAdapter(spinnerGPAdapter);
        spinnerDataBases.setAdapter(spinnerDBAdapter);

        spinnerGamePlanes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MillionaireActivity.gamePlanName = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
}
