package com.fiiandroid.weatherapp;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

public class CustomSimpleAdapter extends CursorAdapter {

    private Context context;
    private Cursor movedCursor;
    private String alarmTimeText;

    private DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                NotificationDatabaseHelper.getInstance(context).deleteNotification(alarmTimeText);
                movedCursor.requery();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                break;
        }
    };

    public CustomSimpleAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.alarm_item_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String alarmTime = cursor.getString(cursor.getColumnIndexOrThrow(NotificationDatabaseHelper.ALARM_COL));
        view.setTag(alarmTime);
        movedCursor = cursor;
        NotificationWrapper notificationWrapper = new NotificationWrapper(view);
        notificationWrapper.getTextView().setText(alarmTime);
        notificationWrapper.getButton().setOnClickListener(clickListener -> {
            showDialog(view);
        });
    }

    private void showDialog(View view) {
        alarmTimeText = (String) view.getTag();
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage("Are you sure you want to remove this alarm?")
                .setTitle("Confirmation")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .create();
        alertDialog.show();

    }
}
