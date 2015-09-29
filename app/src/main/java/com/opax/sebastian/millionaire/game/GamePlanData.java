package com.opax.sebastian.millionaire.game;

/**
 * Created by Sebastian on 2015-05-20.
 */

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.io.OutputStream;

/**
 *
 * @author Sebastian
 */
public class GamePlanData implements Serializable {
    private String fileName;
    private List<String> data;
    private Context context;
    private File file;

    public GamePlanData(Context context, String fileName){
        this.fileName = fileName;
        this.context = context;
        data = new ArrayList<>();
        file = new File(context.getFilesDir(), fileName);

    }

    public int size(){
        return data.size();
    }

    public String getName(){return fileName;}

    public String getData(int id){
        return data.get(id);
    }

    public void addData(String d){
        data.add(d);
    }

    private boolean checkFormat(List<String> data){
        if(data.size() == 0)
            return false;

        for(String s : data) {
            if (s == null)
                return false;
            String t[] = s.split(";");
            if (t.length != 3)
                return false;
            if (!isStringInteger(t[0]) || !isStringInteger(t[1]))
                return false;
            if (!t[2].equalsIgnoreCase("false") && !t[2].equalsIgnoreCase("true"))
                return false;
        }
        return true;
    }

    private boolean isStringInteger(String str){
        try
        {
            int i = Integer.parseInt(str);
        }
        catch(NumberFormatException e)
        {
            return false;
        }
        return true;
    }

    public void clean(){
        data.clear();
    }

    public boolean readDataFromFile(){
        FileInputStream inputStream;

        try{
            inputStream = context.openFileInput(fileName);
            InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader buffreader = new BufferedReader(inputreader);

            String line;
            while((line = buffreader.readLine()) != null){
                data.add(line);
            }

            buffreader.close();
            inputreader.close();
            inputStream.close();

        }catch(IOException e){
            return false;
        }

        if(!checkFormat(data)) {
            clean();
            return false;
        }
        return true;
    }

    public boolean writeDataToFile(){
        FileOutputStream outputStream;

        if(!checkFormat(data)){
            clean();
            return false;
        }

        try{
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter outwriter = new OutputStreamWriter(outputStream);
            BufferedWriter buffwriter = new BufferedWriter(outwriter);

            for(String s : data){
                buffwriter.write(s.toCharArray());
                buffwriter.newLine();
            }

            buffwriter.flush();
            buffwriter.close();

            outwriter.close();
            outputStream.close();

        }catch(IOException e){
            Log.e("GamePlanData.java", e.toString());
        }
        return true;
    }
}
