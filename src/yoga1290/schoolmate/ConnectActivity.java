package yoga1290.schoolmate;

import java.util.zip.Inflater;

import yoga1290.schoolmate.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

public class ConnectActivity extends Activity implements OnClickListener
{
	EditText		EditText_ID,EditText_PIN;
	Button		button_connect,button_connectfb,button_connect_google,button_connect4sqr;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_connect);
        
        EditText_ID=(EditText) findViewById(R.id.et_connect);
        EditText_PIN=(EditText) findViewById(R.id.editText_pin);
        
        button_connect=(Button) findViewById(R.id.button_connect);
        button_connectfb=(Button) findViewById(R.id.button_connectfb);
        button_connect4sqr=(Button) findViewById(R.id.button_connect4sqr);
        button_connect_google=(Button) findViewById(R.id.button_connect_google);
        
        button_connect.setOnClickListener(this);
        button_connect4sqr.setOnClickListener(this);
        button_connect_google.setOnClickListener(this);
        button_connectfb.setOnClickListener(this);
        
        try{
	        	EditText_ID.setText(Connect.getData().getString("id"));
	        	EditText_ID.setText(Connect.getData().getString("pin"));
        }catch(Exception e){};
    }
	@Override
	public void onClick(View v) {
		if(v.getId()==button_connect.getId())
		{
			final ConnectActivity thisActivity=this;
			
			try{
				//TODO check keys
				Connect.setData(
					Connect.getData()
						.put("id", EditText_ID.getText().toString())
							.put("pin", EditText_PIN.getText().toString())
					);
			}catch(Exception e){e.printStackTrace();}
			
			//TODO handle stuff
			new URLThread("http://yoga1290.appospot.com/schoolmate/student?id="+EditText_ID.getText().toString()+"&pin="+EditText_PIN.getText().toString(),
							new URLThread_CallBack(){
								@Override
								public void URLCallBack(String response) {
									//TODO check if the response is fine, close the activity
									System.out.println("Update Done :)");
									thisActivity.finish();
								}
							}, Connect.getData().toString());
		}
		else
		{
				String uri="";
				if(v.getId()==button_connect4sqr.getId())
					uri=Connect.OAuthFoursquareURI;
				if(v.getId()==button_connectfb.getId())
					uri=Connect.OAuthFacebookURI;
				
				try
				{
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(uri));
					this.startActivity(i);
				}catch(Exception e){e.printStackTrace();}
		}
	}

}
