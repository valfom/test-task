package com.valfom.testtask;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class TestTaskViewPagerAdapter extends PagerAdapter {
	    
	private List<String> urls;
	private Context context;
	private ImageLoader imageLoader;
	
	TestTaskViewPagerAdapter(Context context, List<String> urls) {
			
		this.context = context;
		this.urls = urls;
		
		imageLoader = new ImageLoader(context.getApplicationContext());
	}
	
    @Override
    public int getCount() {
      
    	return urls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
      
    	return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      
    	ImageView imageView = new ImageView(context);
    	
      	imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      	imageView.setPadding(10, 10, 10, 10);
      	imageLoader.DisplayImage(urls.get(position), imageView);
      	
      	((ViewPager) container).addView(imageView, 0);
      	
      	return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      
    	((ViewPager) container).removeView((ImageView) object);
    }
}
