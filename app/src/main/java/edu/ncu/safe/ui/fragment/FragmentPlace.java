package edu.ncu.safe.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import edu.ncu.safe.R;

public class FragmentPlace extends Fragment {
	private EditText et_place;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_dialog_place, null);
		et_place = (EditText) view.findViewById(R.id.et_place);
		return new TextView(getActivity());
	}
}