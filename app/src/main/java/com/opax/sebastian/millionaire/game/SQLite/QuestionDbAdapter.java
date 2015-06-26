package com.opax.sebastian.millionaire.game.SQLite;

/**
 * Created by Sebastian on 2015-05-06.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.opax.sebastian.millionaire.game.QuestionDataE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionDbAdapter {
    private static final String DEBUG_TAG = "SqLiteQuestionsManager";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "questions.db";
    private static final String DB_QUESTIONS_TABLE = "questions";

    public static final String KEY_NAME = "NAME";
    public static final String KEY_ANSWER_A = "ANSWER_A";
    public static final String KEY_ANSWER_B = "ANSWER_B";
    public static final String KEY_ANSWER_C = "ANSWER_C";
    public static final String KEY_ANSWER_D = "ANSWER_D";
    public static final String KEY_GOOD_ANSWER = "GOOD_ANSWER";
    public static final String KEY_LEVEL = "LEVEL";

    public static final int NAME_COLUMN = 0;
    public static final String NAME_OPTIONS = "TEXT NOT NULL";
    public static final int ANSWER_A_COLUMN = 1;
    public static final String ANSWER_A_OPTIONS = "TEXT NOT NULL";
    public static final int ANSWER_B_COLUMN = 2;
    public static final String ANSWER_B_OPTIONS = "TEXT NOT NULL";
    public static final int ANSWER_C_COLUMN = 3;
    public static final String ANSWER_C_OPTIONS = "TEXT NOT NULL";
    public static final int ANSWER_D_COLUMN = 4;
    public static final String ANSWER_D_OPTIONS = "TEXT NOT NULL";
    public static final int GOOD_ANSWER_COLUMN = 5;
    public static final String GOOD_ANSWER_OPTIONS = "TEXT NOT NULL";
    public static final int LEVEL_COLUMN = 6;
    public static final String LEVEL_OPTIONS = "TEXT NOT NULL";

    public static final String[] allColumns = {KEY_NAME, KEY_ANSWER_A, KEY_ANSWER_B,
            KEY_ANSWER_C, KEY_ANSWER_D, KEY_GOOD_ANSWER, KEY_LEVEL};

    private static final String DB_CREATE_QUESTIONS_TABLE =
            "CREATE TABLE " + DB_QUESTIONS_TABLE + "( " +
                    KEY_NAME + " " + NAME_OPTIONS + ", " +
                    KEY_ANSWER_A + " " + ANSWER_A_OPTIONS + ", " +
                    KEY_ANSWER_B + " " + ANSWER_B_OPTIONS + ", " +
                    KEY_ANSWER_C + " " + ANSWER_C_OPTIONS + ", " +
                    KEY_ANSWER_D + " " + ANSWER_D_OPTIONS + ", " +
                    KEY_GOOD_ANSWER + " " + GOOD_ANSWER_OPTIONS + ", " +
                    KEY_LEVEL + " " + LEVEL_OPTIONS +
                    ");";

    private static final String DROP_QUESTIONS_TABLE =
            "DROP TABLE IF EXISTS " + DB_QUESTIONS_TABLE;

    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper dbHelper;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context, String name,
                       SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_QUESTIONS_TABLE);

            Log.d(DEBUG_TAG, "Database creating...");
            Log.d(DEBUG_TAG, "Table " + DB_QUESTIONS_TABLE + " ver." + DB_VERSION + " created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_QUESTIONS_TABLE);

            Log.d(DEBUG_TAG, "Database updating...");
            Log.d(DEBUG_TAG, "Table " + DB_QUESTIONS_TABLE + " updated from ver." + oldVersion + " to ver." + newVersion);
            Log.d(DEBUG_TAG, "All data is lost.");

            onCreate(db);
        }
    }

    public QuestionDbAdapter(Context context) {
        this.context = context;
    }

    public QuestionDbAdapter open() {
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            Log.e("SQLException", e.toString());
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long addQuestion(QuestionDataE q) {
        ContentValues newQuestionValues = new ContentValues();
        List<String> ans = new ArrayList<>(q.getAnswers());

        newQuestionValues.put(KEY_NAME, q.getName());
        newQuestionValues.put(KEY_ANSWER_A, ans.get(0));
        newQuestionValues.put(KEY_ANSWER_B, ans.get(1));
        newQuestionValues.put(KEY_ANSWER_C, ans.get(2));
        newQuestionValues.put(KEY_ANSWER_D, ans.get(3));
        newQuestionValues.put(KEY_GOOD_ANSWER, q.getGoodAnswer());
        newQuestionValues.put(KEY_LEVEL, q.getLevelQuestion());

        return db.insert(DB_QUESTIONS_TABLE, null, newQuestionValues);
    }

    public boolean isQuestion(String name){
        Cursor cursor = db.rawQuery("select NAME,ANSWER_A,ANSWER_B,ANSWER_C,ANSWER_D,GOOD_ANSWER,LEVEL from questions where NAME='" + name + "'", null);
        if(cursor.moveToFirst())
            return true;
        return false;
    }

    public boolean removeQuestion(String name){
        String where = KEY_NAME + "=" + "'" + name + "'";
        return db.delete(DB_QUESTIONS_TABLE, where, null) > 0;
    }

    public QuestionDataE getQuestionDataFromName(String name){
        Cursor cursor = db.rawQuery("select NAME,ANSWER_A,ANSWER_B,ANSWER_C,ANSWER_D,GOOD_ANSWER,LEVEL from questions where NAME='" + name + "'", null);
        if (cursor != null && cursor.moveToFirst())
            return getQuestionDataE(cursor);
        return null;
    }

    public List<QuestionDataE> getAllQuestions() {
        Cursor cursor = db.query(DB_QUESTIONS_TABLE, allColumns, null, null, null, null, null);
        List<QuestionDataE> allQuestions = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do{
                QuestionDataE tmp = getQuestionDataE(cursor);
                allQuestions.add(tmp);
            }while(cursor.moveToNext());
        }

        return allQuestions;
    }

    public int upgradeQuestion(QuestionDataE q, String name){
        ContentValues updateQuestionValues = new ContentValues();
        List<String> ans = new ArrayList<>(q.getAnswers());
        String where = KEY_NAME + "=" + "'" + name + "'";

        updateQuestionValues.put(KEY_NAME, q.getName());
        updateQuestionValues.put(KEY_ANSWER_A, ans.get(0));
        updateQuestionValues.put(KEY_ANSWER_B, ans.get(1));
        updateQuestionValues.put(KEY_ANSWER_C, ans.get(2));
        updateQuestionValues.put(KEY_ANSWER_D, ans.get(3));
        updateQuestionValues.put(KEY_GOOD_ANSWER, q.getGoodAnswer());
        updateQuestionValues.put(KEY_LEVEL, q.getLevelQuestion());

        return db.update(DB_QUESTIONS_TABLE, updateQuestionValues, where, null);
    }

    private QuestionDataE getQuestionDataE(Cursor cursor){
        String name;
        String goodA;
        int level;
        String[] ans = new String[4];

        name = cursor.getString(NAME_COLUMN);

        ans[0] = cursor.getString(ANSWER_A_COLUMN);
        ans[1] = cursor.getString(ANSWER_B_COLUMN);
        ans[2] = cursor.getString(ANSWER_C_COLUMN);
        ans[3] = cursor.getString(ANSWER_D_COLUMN);

        goodA = cursor.getString(GOOD_ANSWER_COLUMN);
        level = cursor.getInt(LEVEL_COLUMN);

        return new QuestionDataE(name, Arrays.asList(ans), goodA, level);
    }
}
