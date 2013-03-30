package com.valfom.testtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

public class TestTaskListActivity extends ListActivity {

	public final static String TAG_ANNOUNCEMENT = "announcement";
	public final static String TAG_NAME = "name";
	public final static String TAG_PRICE = "price";
	public final static String ATTRIBUTE_FREE = "free";
	public final static String TAG_VALUE = "value";
	public final static String TAG_CURRENCY = "currency";
	public final static String TAG_IMAGE = "image";
	public final static String TAG_IMAGES = "images";
	public final static String ATTRIBUTE_URL = "url";
	public final static String KEY_TYPE = "type";
	
	public final static String ITEM_TYPE_ANNOUNCEMENT = "1";
	public final static String ITEM_TYPE_INDICATOR = "2";
	
	private ListView listView;
	private TestTaskXMLParser parser;
	private Document document;
	private String xml;
	private ArrayList<HashMap<String, String>> data = null;
	private TestTaskListBaseAdapter adapter;
	
	private int currentPage = 0;
	private boolean loadMoreOnScroll = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.list);
		
		listView = getListView();

		listView.setOnScrollListener(new TestTaskScrollListener());

		data = new ArrayList<HashMap<String, String>>();
		parser = new TestTaskXMLParser();
		
		adapter = new TestTaskListBaseAdapter(TestTaskListActivity.this, data);
		listView.setAdapter(adapter);
		
		// Подгружаем объявления с сервера
		new TestTaskLoadAnnouncementsAsyncTask().execute();
		
		handleIntent(getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
	    
		setIntent(intent);
	    handleIntent(intent);
	}
	
	private void handleIntent(Intent intent) {
		
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	    	
	    	String query;
	    	
	    	loadMoreOnScroll = false;

	    	query = intent.getStringExtra(SearchManager.QUERY);
	    	
	    	// Сохраняем строку запроса для search suggestions
	    	SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
	    			TestTaskSuggestionContentProvider.AUTHORITY, TestTaskSuggestionContentProvider.MODE);
	        suggestions.saveRecentQuery(query, null);
	      
	        // Отсеиваем всё, кроме буквенных символов
	    	Pattern pattern = Pattern.compile("[\\p{L}]+");
	    	Matcher matcher = pattern.matcher(query);
	    	
	    	ArrayList<String> words = new ArrayList<String>();
	    	
	    	// Находим все слова в строке
	    	while(matcher.find()) words.add(matcher.group());
	    	
	    	if (words.size() > 0) {
	    	
		    	ArrayList<HashMap<String, String>> searchResultData = new ArrayList<HashMap<String, String>>();
		    	
		    	for (HashMap<String, String> item : data) {
		    		
		    		if (item.get(KEY_TYPE).equals(ITEM_TYPE_ANNOUNCEMENT)) {
			    		
		    			String announcementName = item.get(TAG_NAME);
			    		
		    			// Если в заголовке объявления присутствует хоть одно слово
		    			// из поискового запроса - добавляем его в результат поисковой выдачи
		    			for (String word : words) {
		    				
				    		if (announcementName.contains(word)) {
				    			
				    			searchResultData.add(item);
				    			break;
				    		}
		    			}
		    		}
		    	}
		    	
		    	adapter = new TestTaskListBaseAdapter(TestTaskListActivity.this, searchResultData);
		    	listView.setAdapter(adapter);
	    	}
	    }
	}
	
	private class TestTaskLoadAnnouncementsAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			String url = "http://commonservice.dmir.ru/webservices/Announcements.asmx/SearchAnnouncementsByDaysAgo?rubricId=dmir_737&locationId=1&DaysAgo=&pageNum="
					+ currentPage + "&pageSize=25";

			xml = parser.getXmlFromUrl(url); // Получаем объявления с сервера в формате xml
			
			if (xml != null) {
			
				document = parser.getDomElement(xml);
	
				NodeList nodeList = document.getElementsByTagName(TAG_ANNOUNCEMENT);
	
				// Удаляем индикатор загрузки элементов списка, если он существует
				if (data.size() > 0) {
					
					int lastItemIndex;
					HashMap<String, String> item;
					String type;
	
					item = new HashMap<String, String>();
					lastItemIndex = data.size() - 1;
					item = data.get(lastItemIndex);
					type = item.get(KEY_TYPE);
					
					if (type.equals(ITEM_TYPE_INDICATOR)) data.remove(lastItemIndex);
				}
				
				// Собираем данные по каждому объявлению в массив
				for (int i = 0; i < nodeList.getLength(); i++) {
	
					HashMap<String, String> item = new HashMap<String, String>();
					Element element = (Element) nodeList.item(i);
	
					item.put(KEY_TYPE, ITEM_TYPE_ANNOUNCEMENT);
					item.put(TAG_NAME, parser.getValue(element, TAG_NAME));
					item.put(TAG_PRICE, parser.getPriceValue(element));
					item.put(TAG_IMAGE, parser.getAttribute(element, TAG_IMAGE, ATTRIBUTE_URL));
					item.put(TAG_IMAGES, parser.getImagesCount(element));
					
					data.add(item);
				}
				
				// Добавляем индикатор загрузки элементов списка
				HashMap<String, String> item = new HashMap<String, String>();
				item.put(KEY_TYPE, ITEM_TYPE_INDICATOR);
				data.add(item);
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			super.onPostExecute(result);

			// Прячем индикатор загрузки списка
			RelativeLayout rlLoadingList = (RelativeLayout) findViewById(R.id.rlLoadingList);
			rlLoadingList.setVisibility(View.GONE);
			
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onDestroy() {

		super.onDestroy();
		
		// Удаляем кэшированные изображения
		ImageLoader imageLoader = new ImageLoader(this);
		imageLoader.clearCache();
	}

	public class TestTaskScrollListener implements OnScrollListener {

		// Количество элементов от последнего видимого до конца списка,
		// начиная с которого начинают подгружаться следующие объявления
		private static final int VISIBLE_THRESHOLD = 0;
		
		private int previousTotal = 0;
		private boolean loading = true;

		public TestTaskScrollListener() {}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

			if (loadMoreOnScroll) {
			
				if (loading) {
	
					if (totalItemCount > previousTotal) {
	
						loading = false;
						previousTotal = totalItemCount;
						currentPage++;
					}
				}
	
				// Если не происходит загрузка и список прокручен до нужного элемента - подгружаем новые объявления
				if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + VISIBLE_THRESHOLD)) {
	
					new TestTaskLoadAnnouncementsAsyncTask().execute();
					
					loading = true;
				}
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		
		Intent gallery = new Intent(TestTaskListActivity.this, TestTaskGalleryActivity.class);
		gallery.putExtra("announcementId", id);
		startActivity(gallery);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.menu_list, menu);
		
		// Инициализируем Search Widget
	    SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    searchView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
	    
//	    searchManager.setOnCancelListener(new SearchManager.OnCancelListener() {
//	    	
//	    	@Override
//	    	public void onCancel() {
//	    		
//	    		Log.d(TAG, "SearchManager.OnCancelListener");
//	    	}
//	    });
//	    searchManager.setOnDismissListener(new SearchManager.OnDismissListener() {
//	    	
//	    	@Override
//	    	public void onDismiss() {
//
//	    		Log.d(TAG, "SearchManager.OnDismissListener");
//	    	}
//	    });
//	    searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//	    	
//	    	@Override
//	    	public boolean onClose() {
//	    		
//	    		Log.d(TAG, "SearchView.OnCloseListener");
//	    		
//	    		return false;
//	    	}
//	    });
	    
	    // Из-за проблемы в Android 4.0+, ни одно из этих событий не происходит,
	    // так что отслеживаем onMenuItemActionCollapse
	    // https://code.google.com/p/android/issues/detail?id=25758
	    
	    MenuItem menuItem = menu.findItem(R.id.action_search);
	    
	    menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
            	
            	adapter = new TestTaskListBaseAdapter(TestTaskListActivity.this, data);
            	listView.setAdapter(adapter);
            	
            	// Подгружать объявления при прокрутке
            	loadMoreOnScroll = true;
            	
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
            	
                return true;
            }
        });

		return true;
	}
}
