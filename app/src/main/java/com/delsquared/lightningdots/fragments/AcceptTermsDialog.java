package com.delsquared.lightningdots.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.delsquared.lightningdots.R;

public class AcceptTermsDialog extends DialogFragment {
	
	static final int NUMBER_OF_TABS = 2;
	private static final String SHOW_REASON_KEY = "showAcceptTermsDialogReason";
	
	// Use this instance of the interface to deliver action events
	AcceptTermsDialogListener mListener;
	
	public interface AcceptTermsDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void OnFragmentAttached();
    }
	
	public AcceptTermsDialog() {
        // Empty constructor required for DialogFragment
    }
	
	public static AcceptTermsDialog newInstance(ShowAcceptTermsDialogReason showReason) {

		// Create the new instance
		AcceptTermsDialog f = new AcceptTermsDialog();
		
		// Add the flag whether to programmatically set the margins
		Bundle args = new Bundle();
		args.putInt(SHOW_REASON_KEY, showReason.getValue());
		f.setArguments(args);

        return f;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListener != null) {
            mListener.OnFragmentAttached();
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	// Remove the title bar and footer bar
    	setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	// Inflate the view for the dialog
    	View view = inflater.inflate(R.layout.dialog_acceptterms, container);
    	
    	// Initialize the show dialog reason value
    	int showReasonValue = ShowAcceptTermsDialogReason.NONE_ACCEPTED.getValue();
    	
    	// Get the args
	    Bundle args = getArguments();
	    
	    // Check if we have args
	    if (args != null)
	    {
	    	
	    	// Initialize the reason
	    	showReasonValue = args.getInt(SHOW_REASON_KEY);
	    	
	    }
	    
	    // Convert the show reason value to a ShowAcceptTermsDialogReason
	    ShowAcceptTermsDialogReason showReason = ShowAcceptTermsDialogReason.NONE_ACCEPTED;
	    try {
	    	showReason = ShowAcceptTermsDialogReason.values()[showReasonValue];
	    } catch (Exception e) {
	    	showReason = ShowAcceptTermsDialogReason.NONE_ACCEPTED;
	    }
    	
    	// Initialize the message
		String messageHtmlString = getString(R.string.dialog_accepttermsnointernet_welcome);
				
		
		// Add extra message based on the reason to show the dialog
		switch (showReason) {
		case NO_INTERNET:
			messageHtmlString += getString(R.string.dialog_accepttermsnointernet_nointernet);
			break;
			
		case NEW_VERSION_TERMS:
			messageHtmlString += getString(R.string.dialog_accepttermsnointernet_newversionterms);
			break;
			
		case NEW_VERSION_PRIVACYPOLICY:
			messageHtmlString += getString(R.string.dialog_accepttermsnointernet_newversionprivacypolicy);
			break;
			
		case NEW_VERSION_BOTH:
			messageHtmlString += getString(R.string.dialog_accepttermsnointernet_newversionboth);
			break;
			
		default:
			messageHtmlString += getString(R.string.dialog_accepttermsnointernet_noneaccepted);
				
		}
		
		// Construct the html string
		messageHtmlString += getString(R.string.dialog_accepttermsnointernet_text);
		
		messageHtmlString = messageHtmlString
				.replace(
						getString(R.string.dialog_accepttermsnointernet_termsurlplaceholder)
						, getString(R.string.terms_of_service_url))
				.replace(
						getString(R.string.dialog_accepttermsnointernet_privacypolicyurlplaceholder)
						, getString(R.string.privacy_policy_url))
				.replace(
						getString(R.string.dialog_accepttermsnointernet_termsplaceholder)
						, getString(R.string.terms_of_service_activity_title))
				.replace(
						getString(R.string.dialog_accepttermsnointernet_privacypolicyplaceholder)
						, getString(R.string.privacy_policy_activity_title))
				.replace(
						getString(R.string.dialog_accepttermsnointernet_appnameplaceholder)
						, getString(R.string.app_name));
		
		// Get the webview
		WebView webViewMessage = (WebView) view.findViewById(R.id.dialog_acceptterms_webview_message);
		
		// Set the mime type and encoding
		String mime = "text/html";
	    String encoding = "utf-8";
	    
	    // Show the html message
	    webViewMessage.loadData(messageHtmlString, mime, encoding);
        
        // Get the accept terms checkbox
        CheckBox acceptTermsCheckbox = (CheckBox) view.findViewById(R.id.dialog_acceptterms_checkbox_accept);
        
        // Get the accept terms button
        Button acceptTermsButton = (Button) view.findViewById(R.id.dialog_acceptterms_button_accept);
        
        // Set the checkbox check changed listener
        acceptTermsCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        	@Override
        	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        		// Toggle the accept terms button
        		Button acceptButton = (Button) getDialog().findViewById(R.id.dialog_acceptterms_button_accept);
            	acceptButton.setEnabled(isChecked);
        	}
        });
        
        // Set the accept terms button click listener
        acceptTermsButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		mListener.onDialogPositiveClick(AcceptTermsDialog.this);
            	dismiss();
        	}
        });
        
        return view;
        
    }
    
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AcceptTermsDialogListener) activity;
            
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    /*
	public class AcceptTermsDialogFragmentPagerAdapter extends PagerAdapter {
        
		public AcceptTermsDialogFragmentPagerAdapter(
				FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUMBER_OF_TABS;
        }

        @Override
        public Fragment getItem(int position) {
        	
        	if (position == 0) {
        		return FragmentTermsOfService.newInstance(
        				true);
        	}
        		
        	return FragmentPrivacyPolicy.newInstance(
        			true);
        }
        
        @Override
        public CharSequence getPageTitle(int position) {
        	
        	if (position == 0) {
        		return getResources().getString(R.string.terms_of_service_activity_title);
        	}
        	
            return getResources().getString(R.string.privacy_policy_activity_title);
        }
    }
    */
	
	public static enum ShowAcceptTermsDialogReason {
		NONE_ACCEPTED(0),
		NO_INTERNET(1),
		NEW_VERSION_TERMS(2),
		NEW_VERSION_PRIVACYPOLICY(3),
		NEW_VERSION_BOTH(4);
		
		private final int value;
	    private ShowAcceptTermsDialogReason(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	}


}
