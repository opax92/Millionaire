package com.opax.sebastian.millionaire.game;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sebastian on 2015-05-07.
 */
public class QuestionDataParceable implements Parcelable {
    public final String name;
    public final String[] answers;
    public final String goodAnswer;
    public final int level;

    public QuestionDataParceable(QuestionDataE q){
        this.name = q.getName();
        this.goodAnswer = q.getGoodAnswer();
        this.level = q.getLevelQuestion();
        List<String> ans = new ArrayList<String>(q.getAnswers());
        answers = new String[4];
        answers[0] = ans.get(0);

        answers[1] = ans.get(1);
        answers[2] = ans.get(2);
        answers[3] = ans.get(3);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeStringArray(this.answers);
        dest.writeString(this.goodAnswer);
        dest.writeInt(this.level);
    }

    private QuestionDataParceable(Parcel in) {
        this.name = in.readString();
        this.answers = in.createStringArray();
        this.goodAnswer = in.readString();
        this.level = in.readInt();
    }

    public static final Parcelable.Creator<QuestionDataParceable> CREATOR = new Parcelable.Creator<QuestionDataParceable>() {
        public QuestionDataParceable createFromParcel(Parcel source) {
            return new QuestionDataParceable(source);
        }

        public QuestionDataParceable[] newArray(int size) {
            return new QuestionDataParceable[size];
        }
    };
}
