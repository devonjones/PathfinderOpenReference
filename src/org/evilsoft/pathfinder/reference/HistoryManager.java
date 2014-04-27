package org.evilsoft.pathfinder.reference;

import java.util.List;

import org.evilsoft.pathfinder.reference.db.DbWrangler;
import org.evilsoft.pathfinder.reference.db.user.HistoryAdapter;
import org.evilsoft.pathfinder.reference.list.HistoryListAdapter;
import org.evilsoft.pathfinder.reference.list.HistoryListItem;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class HistoryManager {
	private Activity activity;
	private DbWrangler dbWrangler;
	private List<Cursor> cursorList;
	public DrawerLayout drawerLayout;
	public View drawer;
	private ListView drawerList;
	private Integer drawerSelection;
	private View drawerSelectionView = null;

	public HistoryManager(Activity activity, DbWrangler dbWrangler,
			List<Cursor> cursorList) {
		this.activity = activity;
		this.dbWrangler = dbWrangler;
		this.cursorList = cursorList;
	}

	public void setupDrawer() {
		drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
		drawer = activity.findViewById(R.id.history_drawer);
		drawerList = (ListView) activity.findViewById(R.id.history_drawer_list);
		HistoryAdapter ha = new HistoryAdapter(dbWrangler.getUserDbAdapter());
		Cursor curs = ha.fetchHistory();
		cursorList.add(curs);
		drawerList.setAdapter(new HistoryListAdapter(activity
				.getApplicationContext(), curs));
		drawerList.setOnItemClickListener(new DrawerItemClickListener());
		drawerLayout.setDrawerListener(new DrawerListener());
	}

	public void refreshDrawer() {
		if (drawerList != null) {
			HistoryAdapter ha = new HistoryAdapter(
					dbWrangler.getUserDbAdapter());
			Cursor curs = ha.fetchHistory();
			cursorList.add(curs);
			HistoryListAdapter hla = (HistoryListAdapter) drawerList
					.getAdapter();
			hla.swapCursor(curs);
			hla.notifyDataSetChanged();
		}
	}

	public void openDrawer() {
		drawerLayout.openDrawer(drawer);
	}

	public void closeDrawer() {
		drawerLayout.closeDrawer(drawer);
	}

	private class DrawerListener implements DrawerLayout.DrawerListener {
		@Override
		public void onDrawerClosed(View arg0) {
			if (drawerSelection != null) {
				HistoryListAdapter hla = (HistoryListAdapter) drawerList
						.getAdapter();

				HistoryListItem item = (HistoryListItem) hla
						.getItem(drawerSelection);
				String url = item.getUrl();
				Intent showContent = new Intent(
						activity.getApplicationContext(), DetailsActivity.class);
				showContent.setData(Uri.parse(url));
				activity.startActivity(showContent);
			}
		}

		@Override
		public void onDrawerOpened(View arg0) {
			drawerSelection = null;
			clearSelection();
		}

		@Override
		public void onDrawerSlide(View arg0, float arg1) {
		}

		@Override
		public void onDrawerStateChanged(int arg0) {
		}

	}

	public void clearSelection() {
		if (drawerSelectionView != null) {
			drawerSelectionView
					.setBackgroundResource(android.R.color.transparent);
		}
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			clearSelection();
			drawerSelectionView = view;
			view.setBackgroundResource(R.color.light_grey);
			drawerSelection = position;
			drawerLayout.closeDrawer(drawer);
		}
	}
}
