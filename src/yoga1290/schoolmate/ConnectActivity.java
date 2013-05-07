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

public class ConnectActivity extends Activity implements OnClickListener
{

    /**
     * Get these values after registering your oauth app at: https://foursquare.com/oauth/
     */

	Button button_connectfb,button_connect_google,button_connect4sqr;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        
        button_connectfb=(Button) findViewById(R.id.button_connectfb);
        button_connect4sqr=(Button) findViewById(R.id.button_connect4sqr);
        button_connect_google=(Button) findViewById(R.id.button_connect_google);
        
        button_connect4sqr.setOnClickListener(this);
        button_connect_google.setOnClickListener(this);
        button_connectfb.setOnClickListener(this);
    }
	@Override
	public void onClick(View v) {
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
