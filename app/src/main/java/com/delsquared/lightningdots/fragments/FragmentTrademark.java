package com.delsquared.lightningdots.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;

import java.util.Calendar;

public class FragmentTrademark extends android.support.v4.app.Fragment {

    private int textColor = -1;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		
        // Inflate the layout for this fragment
		View theLayout = (View) inflater.inflate(R.layout.fragment_trademark, container, false);
		
		// Set the copyright message
		TextView textViewTrademark = (TextView) theLayout.findViewById(R.id.fragment_trademark_textview_trademark);
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

    /*
    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);

        // Get our custom styles
        TypedArray typedArray = activity.obtainStyledAttributes(attrs, R.styleable.FragmentTrademark);

        // Set the text color
        textColor = typedArray.getColor(R.styleable.FragmentTrademark_text_color, getResources().getColor(R.color.black));

        // Recycle the typed array
        typedArray.recycle();
    }
    */

    public static FragmentTrademark newInstance() {

        // Create the new instance
        FragmentTrademark f = new FragmentTrademark();

        return f;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;

        if (getView() != null) {
            TextView textViewTrademark = (TextView) getView().findViewById(R.id.fragment_trademark_textview_trademark);
            textViewTrademark.setTextColor(textColor);
        }
    }

}
