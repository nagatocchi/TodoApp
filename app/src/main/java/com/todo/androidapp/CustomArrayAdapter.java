package com.todo.androidapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.todo.androidapp.bl.TodoService;
import com.todo.androidapp.model.Todo;
import com.todo.androidapp.util.ConvertCalendar;

import java.util.GregorianCalendar;
import java.util.List;
/**
 * Created by Anika on 07.07.15
 */

/**
 * Array adapter for a custom List.
 */
public class CustomArrayAdapter extends BaseAdapter {

        Context context;
        List<Todo> values;
        private TodoService todoService;
        private static LayoutInflater inflater = null;

        public CustomArrayAdapter(Context context, List<Todo> values, TodoService todoService) {
            this.context = context;
            this.values = values;
            this.todoService = todoService;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        /**
         * Method for getting the amount of different view items used in the list.
         *
         * @return The amount of items used.
         */
        @Override
        public int getViewTypeCount() {
            return 1;
        }

        /**
         * Method getting the type of view item used in the list.
         *
         * @param position The position of the item in the list.
         * @return The type of item used.
         */
        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        /**
         * Method for getting the number of items the list is showing.
         *
         * @return The size of the list.
         */
        @Override
        public int getCount() {
            return values.size();
        }

        /**
         * Method for getting a Todo object used at a given position in the list.
         *
         * @param position The position of the item in the list.
         * @return The Todo at the given position in the list.
         */
        @Override
        public Object getItem(int position) {
            return values.get(position);
        }

        /**
         * Method for getting the ID of an item at a given position in the list.
         *
         * @param position The position of the item in the list.
         * @return The ID of the item in the list.
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * Method for building the list items.
         *
         * @param position The position of the item in the list.
         * @param convertView The view of the item.
         * @param parent The parent view of the item.
         * @return The built view of the item.
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            final ViewHolder viewHolder;
            if (vi == null) {
                vi = inflater.inflate(R.layout.todo_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.star = (ToggleButton) vi.findViewById(R.id.todo_fav);
                viewHolder.name = (TextView) vi.findViewById(R.id.todoListName);
                viewHolder.date = (TextView) vi.findViewById(R.id.todoListDate);
                viewHolder.background = (LinearLayout) vi.findViewById(R.id.list_item_background);
                vi.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) vi.getTag();
            }

            String dateString = ConvertCalendar.convertCalendarToSimpleString((values.get(position).getExpiry()));

            viewHolder.star.setOnClickListener(new View.OnClickListener() {

                /**
                 * Method for setting the urgency status of a Todo object from the list.
                 *
                 * @param v The view the button is in.
                 */
                @Override
                public void onClick(View v) {
                    final boolean isChecked = viewHolder.star.isChecked();
                    Todo temp = values.get(position);
                    temp.setFavorite(isChecked);
                    todoService.editTodo(temp);
                }
            });

            if(values.get(position).getDone()) {
                viewHolder.background.setAlpha((float) 0.4);
            } else if(values.get(position).getExpiry().getTimeInMillis() < new GregorianCalendar().getTimeInMillis()){
                viewHolder.background.setBackgroundResource(R.color.primary_light);
            } else {
                viewHolder.background.setAlpha((float) 1);
                viewHolder.background.setBackgroundResource(R.color.transparent);
            }

            viewHolder.name.setText(values.get(position).getName());
            viewHolder.star.setChecked(values.get(position).getFavorite());
            viewHolder.date.setText(dateString);

            return vi;
        }

    /**
     * Method for getting the a list of all Todo objects used in the list.
     *
     * @return The list of Todos.
     */
    public List<Todo> getData() {
        return this.values;
    }


    /**
     * Viewholder class for storing the elements of an item from the list. Increases performance.
     */
    static class ViewHolder {
        public ToggleButton star;
        public TextView name;
        public TextView date;
        public LinearLayout background;
    }

}


