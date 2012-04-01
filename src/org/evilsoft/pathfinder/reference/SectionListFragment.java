package org.evilsoft.pathfinder.reference;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;
import org.evilsoft.pathfinder.reference.db.user.PsrdUserDbAdapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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
		openDb();
		expListAdapter = new SectionExpandableListAdapter(getActivity().getApplicationContext());
		expListAdapter.refresh(dbAdapter, userDbAdapter);
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
		super.onCreate(savedInstanceState);
		openDb();
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getExpandableListView().setCacheColorHint(Color.WHITE);
		getExpandableListView().setGroupIndicator(new ColorDrawable(Color.TRANSPARENT));
		getExpandableListView().setOnChildClickListener(this);
		getExpandableListView().setOnGroupClickListener(this);
	}

	public void refresh(PsrdDbAdapter dbAdapter, PsrdUserDbAdapter userDbAdapter) {
		expListAdapter.refresh(dbAdapter, userDbAdapter);
	}

	@Override
	public boolean onChildClick(ExpandableListView elv, View v, int groupPosition, int childPosition, long id) {
		String uri = getUri(groupPosition, childPosition);
		SectionViewFragment viewer = (SectionViewFragment) this.getActivity().getSupportFragmentManager().findFragmentById(
				R.id.section_view_fragment);

		if (viewer == null || !viewer.isInLayout()) {
			Intent showContent = new Intent(this.getActivity().getApplicationContext(), SectionViewActivity.class);

			showContent.setData(Uri.parse(uri));
			startActivity(showContent);
		} else {
			viewer.updateUrl(uri);
		}
		return false;
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		String sectionName = (String)expListAdapter.getGroup(groupPosition);
		String sectionId = expListAdapter.getPfGroupId(groupPosition);
		String uri = "pfsrd://" + sectionName + "/" + sectionId;
		SectionViewFragment viewer = (SectionViewFragment) this.getActivity().getSupportFragmentManager().findFragmentById(
				R.id.section_view_fragment);

		if (viewer == null || !viewer.isInLayout()) {
			Intent showContent = new Intent(this.getActivity().getApplicationContext(), SectionViewActivity.class);

			showContent.setData(Uri.parse(uri));
			startActivity(showContent);
		} else {
			viewer.updateUrl(uri);
		}
		return false;
	}

	private String getUri(int groupPosition, int childPosition) {
		String sectionName = (String)expListAdapter.getGroup(groupPosition);
		String sectionId = expListAdapter.getPfGroupId(groupPosition);
		String specificName = (String)expListAdapter.getChild(groupPosition, childPosition);
		String specificId = expListAdapter.getPfChildId(groupPosition, childPosition);
		StringBuffer sb = new StringBuffer();
		sb.append("pfsrd://");
		sb.append(addSectionName(sectionName));
		sb.append("/");
		sb.append(sectionId);
		sb.append("/");
		sb.append(specificName);
		if (addId(sectionName)) {
			sb.append("/");
			sb.append(specificId);
		}
		return sb.toString();
	}

	private String addSectionName(String sectionName) {
		return sectionName;
	}

	private boolean addId(String sectionName) {
		if (sectionName.startsWith("Rules")) {
			return true;
		} else if (sectionName.endsWith("Classes")) {
			return true;
		}
		return false;
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
