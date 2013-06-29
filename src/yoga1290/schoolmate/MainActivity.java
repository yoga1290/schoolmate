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

public class MainActivity extends Activity implements OnClickListener
{
	public ServerData data=new ServerData();
	EditText		EditText_ID,EditText_PIN;
	final Activity currentActivity=this;
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
        
        try{
    			String id=null;
	    		try{
	    			id=Connect.getData().getString("id");
	        		Connect.setData(
	        				Connect.getData().put("cur_profileid", id) );
	    		}catch(Exception e){e.printStackTrace();}
    		
    		
	    		if(id!=null)
	    		{
	    			Intent connectActivity=new Intent(this.getApplicationContext(), ProfileActivity.class);
	    			startActivity(connectActivity);
	    		}
        }catch(Exception e){e.printStackTrace();}
    
    System.out.println("Starting server");
    new Server(data).start();
        
    }
	@Override
	public void onClick(View v) {
		if(v.getId()==button_connect.getId())
		{
			final MainActivity thisActivity=this;
			
			try{
				Connect.setData(
					Connect.getData()
						.put("id", EditText_ID.getText().toString())
							.put("pin", EditText_PIN.getText().toString())
								.put("cur_profileid", EditText_ID.getText().toString())
					);				
				startActivity(new Intent(currentActivity, ProfileActivity.class));
				
			}catch(Exception e){e.printStackTrace();}
			
			//TODO handle stuff
			new URLThread("http://yoga1290.appospot.com/schoolmate/student?id="+EditText_ID.getText().toString()+"&pin="+EditText_PIN.getText().toString(),
							new URLThread_CallBack(){
								@Override
								public void URLCallBack(String response) {
									System.out.println("Update Done :)");
								}
							}, Connect.getData().toString()).start();
		
			
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
