package org.evilsoft.pathfinder.reference;

import android.content.Context;
import android.database.Cursor;
import android.widget.BaseAdapter;

public abstract class DisplayListAdapter extends BaseAdapter {
	protected Cursor c;
	protected Context context;

	public DisplayListAdapter(Context context, Cursor c) {
		this.c = c;
		this.context = context;
	}

	@Override
	public int getCount() {
		return c.getCount();
	}

	@Override
	public Object getItem(int index) {
		moveCursor(index);
		return buildItem(c);
	}

	@Override
	public long getItemId(int index) {
		c.moveToFirst();
		c.move(index);
		return c.getInt(0);
	}

	public void moveCursor(int index) {
		c.moveToPosition(index);
	}

	public void swapCursor(Cursor curs) {
		c = curs;
	}

	public abstract Object buildItem(Cursor c);
}
