package com.todo.androidapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.app.ListActivity;

import java.util.Collections;
import java.util.List;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.todo.androidapp.bl.TodoService;
import com.todo.androidapp.bl.TodoServiceSingletonFactory;
import com.todo.androidapp.bl.WebAppAction;
import com.todo.androidapp.bl.WebAppConnector;
import com.todo.androidapp.model.Todo;
import com.todo.androidapp.util.DateComparator;
import com.todo.androidapp.util.FavoriteComparator;
import com.todo.androidapp.util.DoneComparator;
import com.todo.androidapp.util.TodoChainedComparator;

/**
 * Created by Anika on 18.06.15
 */
public class MainActivity extends ListActivity implements WarningDialogFragment.NoticeDialogListener {
    private boolean webappReachable = false;
    private TodoService todoService;
    private Button mCreate;
    private ListView list;
    private RelativeLayout mMainView;
    private ProgressBar mProgressView;
    private boolean dateFirst = true;
    List<Todo> values;
    private CustomArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);

        Intent intent = getIntent();
        webappReachable = intent.getBooleanExtra(LoginActivity.SERVER_REACHABLE_STRING, false);

        TodoServiceSingletonFactory.setReachable(webappReachable);
        todoService = TodoServiceSingletonFactory.create(this);
        setContentView(R.layout.activity_main);
        mMainView = (RelativeLayout) findViewById(R.id.main_relative);
        mProgressView = (ProgressBar) findViewById(R.id.sync_progress);
        mCreate = (Button) findViewById(R.id.createNew);
        list = getListView();


        if (!webappReachable) {
            DialogFragment alert = new WarningDialogFragment();
            alert.show(getFragmentManager(), "warningDialog");
            fillThisView();
        } else {
            showProgress(true);
            try {
                WebAppSyncTask connectionTask = new WebAppSyncTask(todoService.getWebAppConnector());
                connectionTask.execute((Void) null);
            } catch (Exception e) {
                Log.e("TodoServiceImpl", e.getMessage(), e);
            }
        }
    }

    private void fillThisView() {
        values = todoService.getAllTodos();
        sortList();
        adapter = new CustomArrayAdapter(this, values, todoService);
        list.setAdapter(adapter);
        list.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long row_id) {
                Todo temp = values.get(position);
                final boolean isDone = temp.getDone();
                temp.setDone(!isDone);
                todoService.editTodo(temp);
                reloadAllData();
                return true;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mMainView.setVisibility(show ? View.GONE : View.VISIBLE);
            mMainView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mMainView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mMainView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onDialogWarningPositiveClick(DialogFragment dialog) {
        // Dismiss Warning Dialog "Server not Reachable".
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(list.getAdapter() != null) {
            list.setAdapter(list.getAdapter());
            reloadAllData();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        reloadAllData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.overflow, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_urgency) {
            dateFirst = false;
            reloadAllData();
            return true;
        }
        if (id == R.id.action_date) {
            dateFirst = true;
            reloadAllData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Todo sample = adapter.getData().get((int) id);
        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra("todo", sample);
        i.putExtra("create", false);
        startActivity(i);
    }


    public void createNewTodo(View view) {
        Intent i = new Intent(this, DetailActivity.class);
        Todo sample = new Todo();
        i.putExtra("todo", sample);
        i.putExtra("create", true);
        startActivity(i);
    }

    private void reloadAllData() {
        // get new data
        values = todoService.getAllTodos();
        if(!values.isEmpty()) {
            sortList();
            // update data in our adapter
            adapter.getData().clear();
            adapter.getData().addAll(values);
            // fire data changed
            adapter.notifyDataSetChanged();
        }
    }

    private void sortList() {

        if (dateFirst) {
            Collections.sort(values, new TodoChainedComparator(new DoneComparator(), new DateComparator(), new FavoriteComparator()));
        } else {
            Collections.sort(values, new TodoChainedComparator(new DoneComparator(), new FavoriteComparator(), new DateComparator()));
        }
    }

    public void synchronizeWithServer(List<Todo> serverTodos) {
        List<Todo> localTodos = todoService.getAllTodos();

        if (localTodos.isEmpty()) {
            for (Todo t : serverTodos) {
                todoService.createLocalTodo(t);
            }
        } else {
            for (Todo t : serverTodos) {
                todoService.deleteServerTodo(t);
            }
            for (Todo t : localTodos) {
                todoService.createServerTodo(t);
            }
        }
    }


    class WebAppSyncTask extends AsyncTask<Void, Void, Boolean> {
        private final WebAppConnector mConnector;
        private List<Todo> todoList;

        public WebAppSyncTask(WebAppConnector connector) {
            mConnector = connector;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                todoList = mConnector.readAllTodos();
                return true;
            } catch (Exception e) {
                Log.e("TodoServiceImpl", e.getMessage(), e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);
            if(success) {
                synchronizeWithServer(todoList);
                fillThisView();
            }
        }
    }
}