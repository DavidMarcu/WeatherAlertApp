package com.fiiandroid.weatherapp;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class NotificationWrapper {
    private View base;
    private ImageButton button;
    private TextView textView;


    public NotificationWrapper(View base) {
        this.base = base;
    }

    public ImageButton getButton() {
        if (button == null) {
            button = base.findViewById(R.id.notification_remove_button);
        }
        return button;
    }

    public TextView getTextView() {
        if(textView == null){
            textView = base.findViewById(R.id.notification_time);
        }
        return textView;
    }
}
