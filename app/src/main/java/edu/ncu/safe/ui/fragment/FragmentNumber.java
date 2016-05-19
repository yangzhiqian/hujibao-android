package edu.ncu.safe.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import edu.ncu.safe.R;

public  class FragmentNumber extends Fragment {
	private EditText et_number;
	private EditText et_note;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_dialog_number, null);
		et_number = (EditText) view.findViewById(R.id.et_number);
		et_note = (EditText) view.findViewById(R.id.et_note);
		return view;
	}

	public String getNumber() {
		return et_number.getText().toString().trim();
	}
	public String getNote() {
		return et_note.getText().toString().trim();
	}
};
