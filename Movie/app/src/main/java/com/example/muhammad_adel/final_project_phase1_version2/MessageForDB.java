package com.example.muhammad_adel.final_project_phase1_version2;

import android.content.Context;
import android.widget.Toast;

/*to do message quickly*/
public class MessageForDB {

    public static void message(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
