package com.todo.androidapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;
import com.todo.androidapp.bl.TodoService;
import com.todo.androidapp.bl.TodoServiceSingletonFactory;
import com.todo.androidapp.model.Todo;
import com.todo.androidapp.util.ConvertCalendar;

import java.util.Calendar;
import java.util.GregorianCalendar;
/**
 * Created by Anika on 03.07.15
 */

/**
 * Detail view of a selected Todo item.
 */
public class DetailActivity extends Activity implements DeleteDialogFragment.NoticeDialogListener, DiscardDialogFragment.NoticeDialogListener{

    //UI references
    private Button saveButton;
    private Button deleteButton;
    private ToggleButton favButton;
    private CheckBox doneCheckBox;
    private EditText mNameView;
    private EditText mDescView;
    private TextView mTime;
    private TextView mDate;

    private boolean mFavorite = false;
    private boolean mDone = false;
    private boolean create = true;
    private GregorianCalendar mExpiry;
    private Todo mTodo;
    private TodoService mTodoService;

    /**
     * On create method for starting the view.
     *
     * @param savedInstanceState Instance state of the view.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mTodoService = TodoServiceSingletonFactory.create(this);
        deleteButton = (Button) findViewById(R.id.todo_delete_button);
        saveButton = (Button) findViewById(R.id.todo_edit_button);
        favButton = (ToggleButton) findViewById(R.id.todo_favorite);
        doneCheckBox = (CheckBox) findViewById(R.id.doneCheck);
        mNameView = (EditText) findViewById(R.id.todo_edit_summary);
        mDescView = (EditText) findViewById(R.id.todo_edit_description);
        mTime = (TextView) findViewById(R.id.editTime);
        mDate = (TextView) findViewById(R.id.editDate);

        Intent i = getIntent();
        create = i.getBooleanExtra("create", true);
        mTodo = (Todo)i.getSerializableExtra("todo");

        if(!create) {
            saveButton.setText(getString(R.string.todo_edit_confirm));
            deleteButton.setText(getString(R.string.todo_delete));

            mFavorite = mTodo.getFavorite();
            favButton.setChecked(mFavorite);

            mDone = mTodo.getDone();
            doneCheckBox.setChecked(mDone);

            mNameView.setText(mTodo.getName());
            mDescView.setText(mTodo.getDescription());
            setTitle("");

        } else {
            saveButton.setText(getString(R.string.todo_create_confirm));
            deleteButton.setText(getString(R.string.dialog_cancel));
            setTitle(R.string.new_todo);
        }

        mExpiry = mTodo.getExpiry();

        mTime.setText(String.format("%02d:%02d", mExpiry.get(Calendar.HOUR_OF_DAY), mExpiry.get(Calendar.MINUTE)));
        mDate.setText(String.format("%02d.%02d.%02d", mExpiry.get(Calendar.DAY_OF_MONTH), mExpiry.get(Calendar.MONTH)+1, mExpiry.get(Calendar.YEAR)));
        //Months start with 0 in util.Calendar

        saveButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Method for saving (creating) a (new) Todo from the detailed view.
             *
             * @param v The view the button is in.
             */
            public void onClick(View v) {
                mTodo.setName(mNameView.getText().toString());
                mTodo.setDescription(mDescView.getText().toString());
                if(create) {
                    mTodoService.createTodo(mTodo);
                } else {
                    mTodoService.editTodo(mTodo);
                }
                finish();
            }
        });

        /**
         * Method for deleting (discarding) a (new) Todo from the detailed view.
         * Shows the respective dialogs.
         *
         * @param v The view the button is in.
         */
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(create) {
                    DialogFragment alert = new DiscardDialogFragment();
                    alert.show(getFragmentManager(), "discardDialog");
                } else {
                    DialogFragment alert = new DeleteDialogFragment();
                    alert.show(getFragmentManager(), "deleteDialog");
                }
            }
        });

    }

    /**
     * Method for confirming the deletion of a Todo from the detailed view.
     *
     * @param dialog The dialog the button is in.
     */
    @Override
     public void onDialogDeletePositiveClick(DialogFragment dialog) {
        mTodoService.deleteTodo(mTodo);
        finish();
    }

    /**
     * Method for cancelling the deletion of a Todo from the detailed view.
     *
     * @param dialog The dialog the button is in.
     */
    @Override
    public void onDialogDeleteNegativeClick(DialogFragment dialog) {
    }

    /**
     * Method for confirming the discard of a new Todo from the detailed view.
     *
     * @param dialog The dialog the button is in.
     */
    @Override
    public void onDialogDiscardPositiveClick(DialogFragment dialog) {
        finish();
    }

    /**
     * Method for cancelling the discard of a new Todo from the detailed view.
     *
     * @param dialog The dialog the button is in.
     */
    @Override
    public void onDialogDiscardNegativeClick(DialogFragment dialog) {
    }

    /**
     * Method for setting the urgency status of a Todo object from the detailed view.
     *
     * @param view The view the button is in.
     */
    public void onToggleClicked(View view) {
        mFavorite = ((ToggleButton) view).isChecked();
        mTodo.setFavorite(mFavorite);
    }

    /**
     * Method for setting the done status of a Todo object from the detailed view.
     *
     * @param view The view the checkbox is in.
     */
    public void onChecked(View view) {
        mDone = ((CheckBox) view).isChecked();
        mTodo.setDone(mDone);
    }

    /**
     * Method for creating a new time picker dialog.
     *
     * @param v The view the dialog will be created in.
     */
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment() {

            /**
             * Method for setting a new expiry time in the time picker dialog.
             *
             * @param view The time picker dialog.
             * @param hourOfDay The hours of the new expiry time.
             * @param minute The minutes of the new expiry time.
             */
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                ((TextView) findViewById(R.id.editTime)).setText(String.format("%02d:%02d", hourOfDay, minute));
                //set newTime in Todo
                GregorianCalendar expiry = mTodo.getExpiry();
                expiry.set(GregorianCalendar.HOUR_OF_DAY,hourOfDay);
                expiry.set(GregorianCalendar.MINUTE, minute);
                mTodo.setExpiry(expiry);
            }
        };
        newFragment.show(getFragmentManager(), "timePicker");
    }

    /**
     * Method for creating a new date picker dialog.
     *
     * @param v The view the dialog will be created in.
     */
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment() {

            /**
             * Method for setting a new expiry date in the time picker dialog.
             *
             * @param view The date picker dialog.
             * @param year The years of the new expiry time.
             * @param month The months of the new expiry time.
             * @param day The days of the new expiry time.
             */
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                ((TextView) findViewById(R.id.editDate)).setText(String.format("%02d.%02d.%02d", day, month+1, year));
                //set newDate in Todo
                GregorianCalendar expiry = mTodo.getExpiry();
                expiry.set(GregorianCalendar.DAY_OF_MONTH, day);
                expiry.set(GregorianCalendar.MONTH, month);
                expiry.set(GregorianCalendar.YEAR, year);
                mTodo.setExpiry(expiry);
            }
        };
        newFragment.show(getFragmentManager(), "datePicker");
    }
}
