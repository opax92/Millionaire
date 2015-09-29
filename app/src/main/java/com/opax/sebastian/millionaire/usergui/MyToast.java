package com.opax.sebastian.millionaire.usergui;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by opax on 16.08.2015.
 */
public class MyToast {
    private static Toast toast;

    public static void getToast(Context context, String str) {
        if (toast == null)
        {
            toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {
            toast.cancel();
            toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}