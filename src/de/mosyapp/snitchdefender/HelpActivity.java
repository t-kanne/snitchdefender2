package de.mosyapp.snitchdefender;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HelpActivity extends ActionBarActivity {

	TextView textView,textView_question1,textView_question2,textView_question3,textView_question4,
	textView_question5,textView_question6,textView_question7,textView_question8,textView_question9,
	textView_question10,textView_question11,textView_question12;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.fragment_help);

	 
	    textView = (TextView) findViewById (R.id.faq_view);
	    
	    textView_question1 = (TextView) findViewById (R.id.faq_question1);
	    textView_question1.setText (Html.fromHtml (getString (R.string.what_is_snitch_defender_question)));
	    
	    textView_question2 = (TextView) findViewById (R.id.faq_question2);
	    textView_question2.setText (Html.fromHtml (getString (R.string.how_snitch_defender_works_question)));
	    
	    textView_question3 = (TextView) findViewById (R.id.faq_question3);
	    textView_question3.setText (Html.fromHtml (getString (R.string.how_to_user_snitch_question)));
	    
	    textView_question4 = (TextView) findViewById (R.id.faq_question4);
	    textView_question4.setText (Html.fromHtml (getString (R.string.situations_to_use_question)));
	    
	    textView_question5 = (TextView) findViewById (R.id.faq_question5);
	    textView_question5.setText (Html.fromHtml (getString (R.string.settings_question)));
	    
	    textView_question6 = (TextView) findViewById (R.id.faq_question6);
	    textView_question6.setText (Html.fromHtml (getString (R.string.sensibility_question)));
	    
	    textView_question7 = (TextView) findViewById (R.id.faq_question7);
	    textView_question7.setText (Html.fromHtml (getString (R.string.prevent_standby_question)));
	    
	    textView_question8 = (TextView) findViewById (R.id.faq_question8);
	    textView_question8.setText (Html.fromHtml (getString (R.string.standby_exit_question)));
	    
	    textView_question9 = (TextView) findViewById (R.id.faq_question9);
	    textView_question9.setText (Html.fromHtml (getString (R.string.call_question)));
	    
	    textView_question10 = (TextView) findViewById (R.id.faq_question10);
	    textView_question10.setText (Html.fromHtml (getString (R.string.permission_question)));
	    
	    textView_question11 = (TextView) findViewById (R.id.faq_question11);
	    textView_question11.setText (Html.fromHtml (getString (R.string.tablet_question)));
	    
	    textView_question12 = (TextView) findViewById (R.id.faq_question12);
	    textView_question12.setText (Html.fromHtml (getString (R.string.other_questions_question)));
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_help, container,
					false);
			return rootView;
		}
	}

	
	

}
