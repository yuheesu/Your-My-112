package com.androidtutorialpoint.googlemapsnearbyplaces;

/**
 * Created by wsjin on 2016-11-01.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ListViewItem> arrData;
    private LayoutInflater inflater;

    public ListViewAdapter(Context c, ArrayList<ListViewItem> arr) {
        this.context = c;
        this.arrData = arr;
        inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return arrData.size();
    }

    public Object getItem(int position) {
        return arrData.get(position).getName();
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        ImageView image = (ImageView)convertView.findViewById(R.id.image);
        image.setImageResource(arrData.get(position).getImage());

        TextView name = (TextView)convertView.findViewById(R.id.name);
        name.setText(arrData.get(position).getName());

        TextView tel = (TextView)convertView.findViewById(R.id.tel);
        tel.setText(arrData.get(position).getTel());

        TextView email = (TextView)convertView.findViewById(R.id.email);
        email.setText(arrData.get(position).getEmail());


        /*
        Button modifyBtn = (Button)convertView.findViewById(R.id.modifybtn);
        modifyBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(context, "수정합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        Button delBtn = (Button)convertView.findViewById(R.id.delbtn);
        delBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(context, "삭제합니다.", Toast.LENGTH_SHORT).show();
            }
        });
        */

        return convertView;
    }


}



