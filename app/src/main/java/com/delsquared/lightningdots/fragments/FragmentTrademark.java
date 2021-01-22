package com.delsquared.lightningdots.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.delsquared.lightningdots.R;

import java.util.Calendar;

public class FragmentTrademark extends androidx.fragment.app.Fragment {

    private int textColor = -1;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		
        // Inflate the layout for this fragment
		View theLayout = inflater.inflate(R.layout.fragment_trademark, container, false);
		
		// Set the copyright message
		TextView textViewTrademark = theLayout.findViewById(R.id.fragment_trademark_textview_trademark);
		Calendar calendar = Calendar.getInstance();
		int currentYear = calendar.get(Calendar.YEAR);
		String yearText = "";
		if (currentYear > 2014)
			yearText = "2014-";
		yearText += Integer.toString(currentYear);
		String trademarkText =
				String.format(
                        getString(R.string.fragment_trademark_text)
                        , yearText
                        , getString(R.string.trademark_name));
		textViewTrademark.setText(trademarkText);

        // Set the text color
        if (textColor == -1) {
            textColor = getResources().getColor(R.color.black);
        }
        textViewTrademark.setTextColor(textColor);
	    
        return theLayout;
    }

    @SuppressWarnings("unused")
    public static FragmentTrademark newInstance() {

        // Create the new instance

        return new FragmentTrademark();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;

        if (getView() != null) {
            TextView textViewTrademark = getView().findViewById(R.id.fragment_trademark_textview_trademark);
            textViewTrademark.setTextColor(textColor);
        }
    }

}
