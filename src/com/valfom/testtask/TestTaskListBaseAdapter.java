package com.valfom.testtask;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class TestTaskListBaseAdapter extends BaseAdapter {
 
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;
    private ImageLoader imageLoader;
 
    public TestTaskListBaseAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
    	
        this.data = data;
        
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());
    }
 
    public int getCount() {
    	
        return data.size();
    }
 
    public Object getItem(int position) {
    	
        return null;
    }
 
    public long getItemId(int position) {
    	
        return position;
    }
 
	public View getView(int position, View convertView, ViewGroup parent) {
    	
    	HashMap<String, String> item;
    	String type;
    	
    	item = new HashMap<String, String>();
        item = data.get(position);
        type = item.get(TestTaskListActivity.KEY_TYPE);
        
        if (type.equals(TestTaskListActivity.ITEM_TYPE_ANNOUNCEMENT)) {
        
        	convertView = inflater.inflate(R.layout.list_item, null);
	 
	        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
	        TextView tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
	        TextView tvCounter = (TextView) convertView.findViewById(R.id.tvCounter);
	        ImageView ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);
	        
	    	tvName.setText(item.get(TestTaskListActivity.TAG_NAME));
	    	tvPrice.setText(item.get(TestTaskListActivity.TAG_PRICE));
	    	tvCounter.setText(item.get(TestTaskListActivity.TAG_IMAGES));
	    	imageLoader.DisplayImage(item.get(TestTaskListActivity.TAG_IMAGE), ivThumbnail);
	    	
        } else convertView = inflater.inflate(R.layout.list_loader_item, null);
        
        
        return convertView;
    }
}
