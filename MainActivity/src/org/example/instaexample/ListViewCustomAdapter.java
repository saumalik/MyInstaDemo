package org.example.instaexample;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewCustomAdapter extends BaseAdapter{
 
    ArrayList<ImageUsed> itemList;
 
    public Activity context;
    public LayoutInflater inflater;
 
    public ListViewCustomAdapter(Activity context, ArrayList<ImageUsed> itemList) {
        super();
 
        this.context = context;
        this.itemList = itemList;
 
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return itemList.size();
    }
 
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return itemList.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
 
    public static class ViewHolder
    {
        ImageView imgViewLogo1;
        ImageView imgViewLogo2;
        TextView txtViewTitle;
        TextView txtViewDescription;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
 
        ViewHolder holder;
        if(convertView==null)
        {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.mylistviewrow, null);
 
            holder.imgViewLogo1 = (ImageView) convertView.findViewById(R.id.imgViewStand);
            holder.imgViewLogo2 = (ImageView) convertView.findViewById(R.id.imgViewLow);
 
            convertView.setTag(holder);
        }
        else
            holder=(ViewHolder)convertView.getTag();
 
        ImageUsed bean = (ImageUsed) itemList.get(position);
 
        holder.imgViewLogo1.setImageBitmap(bean.getImgStand());
        holder.imgViewLogo2.setImageBitmap(bean.getImgLow());
 
        return convertView;
    }
 
}