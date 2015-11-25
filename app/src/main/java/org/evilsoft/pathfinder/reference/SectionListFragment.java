package org.evilsoft.pathfinder.reference;

import org.acra.ErrorReporter;
import org.evilsoft.pathfinder.reference.db.DbWrangler;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ExpandableListView;

public class SectionListFragment extends ExpandableListFragment implements
		ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener {
	private DbWrangler dbWrangler;
	private SectionExpandableListAdapter expListAdapter;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		expListAdapter = new SectionExpandableListAdapter(getActivity().getApplicationContext());
		setListAdapter(expListAdapter);
	}

	private void openDb() {
		if (dbWrangler == null) {
			dbWrangler = new DbWrangler(this.getActivity().getApplicationContext());
		}
		if (dbWrangler.isClosed()) {
			dbWrangler.open();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		openDb();
		refresh(dbWrangler);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getExpandableListView().setCacheColorHint(Color.WHITE);
		getExpandableListView().setGroupIndicator(new ColorDrawable(Color.TRANSPARENT));
		getExpandableListView().setOnChildClickListener(this);
		getExpandableListView().setOnGroupClickListener(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		refresh(dbWrangler);
	}

	public void refresh(DbWrangler dbWrangler) {
		expListAdapter.refresh(dbWrangler);
	}

	private void updateFragment(String url) {
		Bundle args = new Bundle();
		args.putString("url", url);
		FragmentTransaction ft = this.getActivity().getSupportFragmentManager().beginTransaction();
		SectionViewFragment viewer = new SectionViewFragment();
		viewer.setArguments(args);
		ft.replace(R.id.section_view_layout, viewer, "viewer");
		ft.commit();
	}

	@Override
	public boolean onChildClick(ExpandableListView elv, View v, int groupPosition, int childPosition, long id) {
		StringBuffer sb = new StringBuffer();
		sb.append("SectionListFragment.onChildClick: groupPosition:");
		sb.append(groupPosition);
		sb.append(", childPosition:");
		sb.append(childPosition);
		sb.append(", id:");
		sb.append(id);
		ErrorReporter e = ErrorReporter.getInstance();
		e.putCustomData("LastClick", sb.toString());
		String uri = getUri(groupPosition, childPosition);
		if(PathfinderOpenReferenceActivity.isTabletLayout(getActivity())) {
			updateFragment(uri);
		} else {
			Intent showContent = new Intent(this.getActivity().getApplicationContext(), SectionViewActivity.class);
			showContent.setData(Uri.parse(uri));
			startActivity(showContent);
		}
		return false;
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		StringBuffer sb = new StringBuffer();
		sb.append("SectionListFragment.onGroupClick: groupPosition:");
		sb.append(groupPosition);
		sb.append(", id:");
		sb.append(id);
		ErrorReporter e = ErrorReporter.getInstance();
		e.putCustomData("LastClick", sb.toString());
		if(expListAdapter.getChildrenCount(groupPosition) != 0) {
			return false;
		}
		String uri = expListAdapter.getPfGroupUrl(groupPosition);
		if(PathfinderOpenReferenceActivity.isTabletLayout(getActivity())) {
			updateFragment(uri);
		} else {
			Intent showContent = new Intent(this.getActivity().getApplicationContext(), SectionViewActivity.class);

			showContent.setData(Uri.parse(uri));
			startActivity(showContent);
		}
		return false;
	}

	private String getUri(int groupPosition, int childPosition) {
		String group = (String)expListAdapter.getGroup(groupPosition);
		if(group.equals("Bookmarks")) {
			String sectionUrl = expListAdapter.getPfGroupUrl(groupPosition);
			String specificName = (String) expListAdapter.getChild(groupPosition, childPosition);
			StringBuffer sb = new StringBuffer();
			sb.append(sectionUrl);
			sb.append("/");
			sb.append(specificName);
			return sb.toString();
		} else {
			return expListAdapter.getPfChildUrl(groupPosition, childPosition);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (dbWrangler != null) {
			dbWrangler.close();
		}
	}
}
