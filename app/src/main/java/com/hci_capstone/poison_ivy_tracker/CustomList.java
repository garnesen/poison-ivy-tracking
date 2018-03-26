package com.hci_capstone.poison_ivy_tracker;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.EditText;

/**
 * Created by douglasbotello on 3/25/18.
 */

public class CustomList extends ArrayAdapter {

    private Activity context;
    private String[] settingsItems;
    private String[] settingsSubItems;
    private Context thisContext;
    private AlertDialog ad;
    private int currentPosition;

    public CustomList(Activity context, String[] settingsItems, String[] settingsSubItems) {
        super(context, R.layout.editfielddialogue, settingsItems);
        this.context = context;
        this.settingsItems = settingsItems;
        this.settingsSubItems = settingsSubItems;
        thisContext = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        currentPosition = position;
        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView= inflater.inflate(R.layout.editfielddialogue, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

        TextView subTextView = (TextView) rowView.findViewById(R.id.subtxt);
        txtTitle.setText(settingsItems[position]);

        subTextView.setText(settingsSubItems[position]);

        //set row click behavior for first row
        if (position == 0) {
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
                    LayoutInflater inflater = (LayoutInflater) thisContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


                    builder.setView(inflater.inflate(R.layout.edit_field_trailname, null))
                            .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    EditText edit = (EditText) ((AlertDialog) dialog).findViewById(R.id.editTextEmail);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    cancelDialog();
                                }
                            });
                    ad = builder.create();
                    ad.show();
                }
            });
        }
        else { //for second row
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
                    LayoutInflater inflater = (LayoutInflater) thisContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


                    builder.setView(inflater.inflate(R.layout.edit_field_email, null))
                            .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    EditText edit = (EditText) ((AlertDialog) dialog).findViewById(R.id.editTextTrailname);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    cancelDialog();
                                }
                            });
                    ad = builder.create();
                    ad.show();
                }
            });
        }

    return rowView;
    }




    private void cancelDialog() {
        ad.cancel();
    }

}


