package com.valfom.testtask;

import android.content.SearchRecentSuggestionsProvider;

public class TestTaskSuggestionContentProvider extends SearchRecentSuggestionsProvider {

	public final static String AUTHORITY = "com.valfom.testtask.TestTaskSuggestionContentProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;

	public TestTaskSuggestionContentProvider() {

		setupSuggestions(AUTHORITY, MODE);
	}
}
