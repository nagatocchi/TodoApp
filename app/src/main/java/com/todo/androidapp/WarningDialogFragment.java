package com.todo.androidapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


/**
 * Created by Anika on 10.07.15
 */
public class WarningDialogFragment extends DialogFragment {
        /* The activity that creates an instance of this dialog fragment must
         * implement this interface in order to receive event callbacks.
         * Each method passes the DialogFragment in case the host needs to query it. */
        public interface NoticeDialogListener {
            void onDialogWarningPositiveClick(DialogFragment dialog);
        }

        // Use this instance of the interface to deliver action events
        NoticeDialogListener mListener;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            // Verify that the host activity implements the callback interface
            try {
                // Instantiate the NoticeDialogListener so we can send events to the host
                mListener = (NoticeDialogListener) activity;
            } catch (ClassCastException e) {
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException(activity.toString()
                        + " must implement NoticeDialogListener");
            }
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.dialog_warning_title);
            builder.setMessage(R.string.dialog_warning)
                    .setNeutralButton(R.string.dialog_warning_neutral, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onDialogWarningPositiveClick(WarningDialogFragment.this);
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }

}

