package com.valfom.testtask;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;

@SuppressWarnings("deprecation")
public class TestTaskGalleryActivity extends Activity {
  
	private List<String> urls;
	private ViewPager viewPager;
	
	private int announcementId;
	
	private Gallery gallery;
	private List<String> urlsMini;
	private int selectedImagePosition;

    private TestTaskGalleryBaseAdapter galleryAdapter;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
    
		super.onCreate(savedInstanceState);
    
		setContentView(R.layout.gallery);
		
		// Прячем status bar
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		// Прячем action bar
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		
		// Получаем id объявления
		Intent intent = getIntent();
		if (!intent.hasExtra("announcementId")) finish();
		announcementId = (int) intent.getLongExtra("announcementId", 0);
		
		urls = new ArrayList<String>();
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		
		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            
        	@Override
            public void onPageSelected(int position) {
                
        		gallery.setSelection(viewPager.getCurrentItem());
            }
        });
		
		selectedImagePosition = viewPager.getCurrentItem();
		
		new TestTaskGetUrlsAsyncTask().execute("viewPager");
		
		gallery = (Gallery) findViewById(R.id.gallery);
		
		urlsMini =  new ArrayList<String>();
		
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                selectedImagePosition = pos;

                viewPager.setCurrentItem(pos, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });
		
		new TestTaskGetUrlsAsyncTask().execute("gallery");
	}

	// Загружает url изображений выбранного юбъявления
	// "viewPager" - url изображений для просмотра
	// "gallery" - url изображений для превью
    private class TestTaskGetUrlsAsyncTask extends AsyncTask<String, Void, Void> {

    	private String name;
    	
		@Override
		protected Void doInBackground(String... params) {

			String url = "http://commonservice.dmir.ru/webservices/Announcements.asmx/SearchAnnouncementsByDaysAgo?rubricId=dmir_737&locationId=1&DaysAgo=&pageNum="
					+ announcementId + "&pageSize=1";

			TestTaskXMLParser parser = new TestTaskXMLParser();
			String xml = parser.getXmlFromUrl(url); // Получаем объявления с сервера в формате xml
			
			name = params[0];
			
			if (xml != null) {
			
				Document document = parser.getDomElement(xml);
	
				NodeList nodeList = document.getElementsByTagName(TestTaskListActivity.TAG_ANNOUNCEMENT);
				
				Element element = (Element) nodeList.item(0);
	
				NodeList nl = element.getElementsByTagName(TestTaskListActivity.TAG_IMAGE);
				
				if (name.equals("viewPager")) {
				
					for (int i = 4; i < nl.getLength(); i += 5) {
					
						urls.add(parser.getElementAttribute(nl.item(i), TestTaskListActivity.ATTRIBUTE_URL));
					}
					
				} else {
					
					for (int i = 2; i < nl.getLength(); i += 5) {
						
						urlsMini.add(parser.getElementAttribute(nl.item(i), TestTaskListActivity.ATTRIBUTE_URL));
					}
				}
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			super.onPostExecute(result);
			
			
			if (name.equals("viewPager")) {
				
				if (urls.size() > 0) {
				
					TestTaskViewPagerAdapter adapter = new TestTaskViewPagerAdapter(TestTaskGalleryActivity.this, urls);
		
					viewPager.setAdapter(adapter);
				}
				
			} else {
				
				if (urlsMini.size() > 0) {
					
					galleryAdapter = new TestTaskGalleryBaseAdapter(TestTaskGalleryActivity.this, urlsMini);
		
			        gallery.setAdapter(galleryAdapter);
		
			        gallery.setSelection(selectedImagePosition, false);
				}
			}
		}
	}
    
    @Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
        
	        case android.R.id.home:
	            
	        	onBackPressed();
	            
	            return true;
	        default:
	        	return super.onMenuItemSelected(featureId, item);
		}
	}
}
