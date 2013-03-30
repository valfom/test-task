package com.valfom.testtask;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class TestTaskGalleryBaseAdapter extends BaseAdapter {

    private Activity context;
    private static ImageView imageView;
    private List<String> urls;
    private static ViewHolder holder;
    private ImageLoader imageLoader;

    public TestTaskGalleryBaseAdapter(Activity context, List<String> urls) {

        this.context = context;
        this.urls = urls;
        
        imageLoader = new ImageLoader(context.getApplicationContext());
    }

    @Override
    public int getCount() {
    	
    	return urls.size();
    }

    @Override
    public Object getItem(int position) {
    	
        return null;
    }

    @Override
    public long getItemId(int position) {
    	
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            holder = new ViewHolder();

            imageView = new ImageView(this.context);

            convertView = imageView;

            holder.imageView = imageView;

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        imageLoader.DisplayImage(urls.get(position), holder.imageView);

        holder.imageView.setScaleType(ImageView.ScaleType.CENTER);

        return imageView;
    }

    private static class ViewHolder {
    	
        ImageView imageView;
    }
}