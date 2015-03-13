package com.rmathur.uiuccheckin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ItemArrayAdapter extends ArrayAdapter {
    private List studentList = new ArrayList();

    public ItemArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public void add(String[] object) {
        studentList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.studentList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ItemViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_layout, parent, false);
            viewHolder = new ItemViewHolder();
            viewHolder.name = (TextView) row.findViewById(R.id.name);
            viewHolder.uin = (TextView) row.findViewById(R.id.uin);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder) row.getTag();
        }
//        String[] stat = getItem(position);
//        viewHolder.name.setText(stat[0]);
//        viewHolder.uin.setText(stat[1]);
        return row;
    }
//
//    @Override
//    public String[] getItem(int index) {
//        return this.studentList.get(index);
//    }

    static class ItemViewHolder {
        TextView name;
        TextView uin;
    }
}