package com.amap.location.findchildren;

import android.content.Context;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.amap.location.findchildren.RequestConnect.*;


/**
 * Created by kevin.zhang on 2018/4/17.
 */

public class ChildrenAdapter extends BaseAdapter {
    private List<Children> mData;
    private Context mContext;

    public ChildrenAdapter(List<Children>  mData,Context mContext){

        this.mData = mData;
        this.mContext = mContext;
    }
    @Override
    public int getCount() {
        return  mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_child,parent,false);

        TextView txt_aName = (TextView) convertView.findViewById(R.id.name);
        TextView txt_aSpeak = (TextView) convertView.findViewById(R.id.detail);
        Button btn = (Button) convertView.findViewById(R.id.btn);
        final  String children_email = mData.get(position).getEmail();


        txt_aName.setText(children_email);
        txt_aSpeak.setText(mData.get(position).getDetail());


        return convertView;


    }
}
