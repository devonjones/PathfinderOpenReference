package org.evilsoft.pathfinder.reference;

import java.util.ArrayList;
import java.util.HashMap;

import org.acra.ErrorReporter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

public class ContentErrorReporter implements View.OnClickListener {
	private Activity act;
	private ArrayList<HashMap<String, String>> path;
	private String title;

	public ContentErrorReporter(Activity act,
			ArrayList<HashMap<String, String>> path, String title) {
		this.act = act;
		this.path = path;
		this.title = title;
	}

	@Override
	public void onClick(View v) {
		final EditText input = new EditText(act);
		AlertDialog.Builder alert = new AlertDialog.Builder(act);
		alert.setTitle("Report Content Issue");
		alert.setMessage("What can be done to improve this article?");
		alert.setView(input);
		alert.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						Editable value = input.getText();
						if (!value.toString().trim().equals("")) {
							ErrorReporter e = ErrorReporter.getInstance();
							e.putCustomData("Path", renderPath());
							e.putCustomData("IdPath", renderIdPath());
							e.putCustomData("Title", title);
							e.putCustomData("UserComment", value.toString());
							e.putCustomData("LastClick", "ContentErrorReporter.onClick: Ok");
							e.handleException(null);
						}
					}
				});
		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						ErrorReporter e = ErrorReporter.getInstance();
						e.putCustomData("LastClick", "ContentErrorReporter.onClick: Cancel");
					}
				});
		alert.show();
	}

	public String renderPath() {
		StringBuffer sb = new StringBuffer();
		for (int i = this.path.size() - 1; i >= 0; i--) {
			sb.append("/");
			HashMap<String, String> value = this.path.get(i);
			sb.append(value.get("name"));
		}
		return sb.toString();
	}

	public String renderIdPath() {
		StringBuffer sb = new StringBuffer();
		for (int i = this.path.size() - 1; i >= 0; i--) {
			sb.append("/");
			HashMap<String, String> value = this.path.get(i);
			sb.append(value.get("id"));
		}
		return sb.toString();
	}
}
