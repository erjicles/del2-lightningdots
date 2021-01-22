package com.delsquared.lightningdots.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;

public class AcceptTermsDialog extends DialogFragment {

	private static final String SHOW_REASON_KEY = "showAcceptTermsDialogReason";
	
	// Use this instance of the interface to deliver action events
	AcceptTermsDialogListener mListener;
	
	public interface AcceptTermsDialogListener {
        void onDialogPositiveClick(@SuppressWarnings("unused") DialogFragment dialog);
        void OnFragmentAttached();
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
	    	LightningDotsApplication.logDebugErrorMessage("Exception encountered: " + e.getMessage());
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
		WebView webViewMessage = view.findViewById(R.id.dialog_acceptterms_webview_message);
		
		// Set the mime type and encoding
		String mime = "text/html";
	    String encoding = "utf-8";
	    
	    // Show the html message
	    webViewMessage.loadData(messageHtmlString, mime, encoding);
        
        // Get the accept terms checkbox
        CheckBox acceptTermsCheckbox = view.findViewById(R.id.dialog_acceptterms_checkbox_accept);
        
        // Get the accept terms button
        Button acceptTermsButton = view.findViewById(R.id.dialog_acceptterms_button_accept);
        
        // Set the checkbox check changed listener
        acceptTermsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
			// Toggle the accept terms button
			Dialog dialog = getDialog();
			if (dialog == null) {
				LightningDotsApplication.logDebugErrorMessage("dialog is null");
				return;
			}
			Button acceptButton = dialog.findViewById(R.id.dialog_acceptterms_button_accept);
			acceptButton.setEnabled(isChecked);
		});
        
        // Set the accept terms button click listener
        acceptTermsButton.setOnClickListener(clickedView -> {
			mListener.onDialogPositiveClick(AcceptTermsDialog.this);
			dismiss();
		});
        
        return view;
        
    }
    
    @Override
    public void onAttach(@NonNull Context context) {
    	super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AcceptTermsDialogListener) context;
            
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }
	
	public enum ShowAcceptTermsDialogReason {
		NONE_ACCEPTED(0),
		NO_INTERNET(1),
		NEW_VERSION_TERMS(2),
		NEW_VERSION_PRIVACYPOLICY(3),
		NEW_VERSION_BOTH(4);
		
		private final int value;
	    ShowAcceptTermsDialogReason(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	}


}
