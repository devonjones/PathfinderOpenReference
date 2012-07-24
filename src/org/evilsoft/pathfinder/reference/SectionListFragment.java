package org.evilsoft.pathfinder.reference;

import org.acra.ErrorReporter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;
import org.evilsoft.pathfinder.reference.db.user.PsrdUserDbAdapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ExpandableListView;

public class SectionListFragment extends ExpandableListFragment implements
		ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener {
	private PsrdDbAdapter dbAdapter;
	private PsrdUserDbAdapter userDbAdapter;
	private SectionExpandableListAdapter expListAdapter;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		expListAdapter = new SectionExpandableListAdapter(getActivity().getApplicationContext());
		setListAdapter(expListAdapter);
	}

	private void openDb() {
		if (userDbAdapter == null) {
			userDbAdapter = new PsrdUserDbAdapter(this.getActivity().getApplicationContext());
		}
		if (userDbAdapter.isClosed()) {
			userDbAdapter.open();
		}
		if (dbAdapter == null) {
			dbAdapter = new PsrdDbAdapter(this.getActivity().getApplicationContext());
		}
		if (dbAdapter.isClosed()) {
			dbAdapter.open();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		openDb();
		refresh(dbAdapter, userDbAdapter);
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
		refresh(dbAdapter, userDbAdapter);
	}

	public void refresh(PsrdDbAdapter dbAdapter, PsrdUserDbAdapter userDbAdapter) {
		expListAdapter.refresh(dbAdapter, userDbAdapter);
	}

	private void updateFragment(String uri) {
		Fragment frag = this.getActivity().getSupportFragmentManager().findFragmentByTag("viewer");
		if (SectionViewFragment.class.isInstance(frag)) {
			((SectionViewFragment)frag).updateUrl(uri);
		}
		else {
			FragmentTransaction ft = this.getActivity().getSupportFragmentManager().beginTransaction();
			SectionViewFragment viewer = new SectionViewFragment(uri);
			ft.replace(R.id.section_view_layout, viewer, "viewer");
			ft.commit();
		}
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
		} else if(group.startsWith("Rules")) {
			return expListAdapter.getPfChildUrl(groupPosition, childPosition);
		} else {
			String sectionUrl = expListAdapter.getPfGroupUrl(groupPosition);
			String specificId = expListAdapter.getPfChildId(groupPosition, childPosition);
			StringBuffer sb = new StringBuffer();
			sb.append(sectionUrl);
			if (specificId != null) {
				sb.append("?subtype=");
				sb.append(specificId);
			}
			return sb.toString();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (dbAdapter != null) {
			dbAdapter.close();
		}
		if (userDbAdapter != null) {
			userDbAdapter.close();
		}
	}
}
