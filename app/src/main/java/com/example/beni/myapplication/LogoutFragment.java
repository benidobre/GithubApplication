package com.example.beni.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by beni on 4/25/2017.
 */

public  class LogoutFragment extends DialogFragment {

    public static LogoutFragment newInstance(String title) {
        LogoutFragment frag = new LogoutFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.a)
                .setTitle(title)
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((ProfileActivity)getActivity()).doPositiveClick();
                            }
                        }
                )
                .setNegativeButton("NO",
                        null
                )
                .create();
    }
}
