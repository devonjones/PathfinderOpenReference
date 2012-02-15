package org.evilsoft.pathfinder.reference;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;

public class AutoCompleteHandler implements SimpleCursorAdapter.CursorToStringConverter, FilterQueryProvider, OnItemClickListener, OnClickListener, OnKeyListener {
	private static final String TAG = "AutoCompleteHandler";
	protected Context context;
	protected FragmentActivity activity;
	protected PsrdDbAdapter dbAdapter;
	protected AutoCompleteTextView searchAc;
	final static int[] to = new int[] { android.R.id.text1 };
	final static String[] from = new String[] { "name", "cnt" };
	
	public AutoCompleteHandler(Context context, FragmentActivity activity, PsrdDbAdapter dbAdapter, AutoCompleteTextView searchAc, ImageButton searchButton) {
		this.context = context;
		this.activity = activity;
		this.dbAdapter = dbAdapter;
		this.searchAc = searchAc;
		searchAc.setAdapter(getSearchCursorAdapter());
		searchAc.setOnItemClickListener(this);
		searchAc.setOnKeyListener(this);
		searchButton.setOnClickListener(this);
	}

	public CursorAdapter getSearchCursorAdapter() {
		SimpleCursorAdapter adapter =
				new SimpleCursorAdapter(this.context,
						android.R.layout.simple_dropdown_item_1line, null,
						from, to);
		adapter.setCursorToStringConverter(this);
		adapter.setFilterQueryProvider(this);
		return adapter;
	}

	@Override
	public CharSequence convertToString(Cursor cursor) {
		final int columnIndex = cursor.getColumnIndexOrThrow(from[0]);
		final String str = cursor.getString(columnIndex);
		return str;
	}

	@Override
	public Cursor runQuery(CharSequence constraint) {
		Cursor cursor = this.dbAdapter.autocomplete(
				(constraint != null ? constraint.toString() : null));
		return cursor;
	}

	@Override
	public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
		search();
	}

	@Override
	public void onClick(View button) {
		search();
	}

	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) {
		if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
			search();
			return true;
		}
		return false;
	}

	public void search() {
		hideKeyboard();
		int count = dbAdapter.countSearchArticles(searchAc.getText().toString());
		if(count == 1) {
			// Use rules screen
			launchSection(searchAc.getText().toString());
		}
		else if(count > 1) {
			// Use search screen
			launchSearch(searchAc.getText().toString());
		}
	}
	
	public void launchSearch(String searchText) {
		String uri = "pfsrd://Search/" + searchText;
		Log.e(TAG, uri);
		Intent showContent = new Intent(activity.getApplicationContext(), DetailsActivity.class);
		showContent.setData(Uri.parse(uri));
		activity.startActivity(showContent);
	}
	
	public void launchSection(String searchText) {
		String uri = buildUrl(searchText);
		Log.e(TAG, uri);
		Intent showContent = new Intent(activity.getApplicationContext(), DetailsActivity.class);
		showContent.setData(Uri.parse(uri));
		activity.startActivity(showContent);
	}

	public String buildUrl(String searchText) {
		Cursor c = dbAdapter.getSingleSearchArticle(searchText);
		c.moveToFirst();
		String sectionId = c.getString(0);
		String type = c.getString(1);
		String parentId = c.getString(5);
		StringBuffer sb = new StringBuffer();
		sb.append("pfsrd://");
		if(type.equals("feat")) {
			sb.append("Feats/0/All Feats/");
			sb.append(sectionId);
		}
		else if(type.equals("skill")) {
			sb.append("Skills/");
			sb.append(sectionId);
		}
		else if(type.equals("class")) {
			sb.append("Classes/");
			sb.append(sectionId);
		}
		else if(type.equals("creature")) {
			sb.append("Monsters/0/All Monsters/");
			sb.append(sectionId);
		}
		else if(type.equals("race")) {
			sb.append("Races/");
			sb.append(sectionId);
		}
		else if(type.equals("spell")) {
			sb.append("Spells/0/All/");
			sb.append(sectionId);
		}
		else {
			sb.append("Rules/");
			sb.append(parentId);
			sb.append("/");
			sb.append(sectionId);
		}
		return sb.toString();
	}

	public void hideKeyboard() {
		InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.toggleSoftInput(0, 0);
	}
}

