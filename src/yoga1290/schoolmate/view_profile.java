package yoga1290.schoolmate;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import yoga1290.schoolmate.R.*;

public class view_profile extends Fragment implements URLThread_CallBack
{
	View v;
	URLThread URLThread_studentData=null,URLThread_facebookData=null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) 
    {
        v=inflater.inflate(R.layout.view_profile, container, false);
        
        ImageView iv_gpa=(ImageView) v.findViewById(R.id.imageview_profile_gpa);
        iv_gpa.setImageBitmap(Charts.getGPAGraph(400, 100, new int[]{340,280,320,320,300,300,280,290,290,300,300,300,300}));
        
        
        //Testing
		String fbid="870205250";
//		if(URLThread_facebookData==null)
//		{
			URLThread_facebookData=new URLThread("https://graph.facebook.com/"+fbid+"?fields=picture&access_token="+Connect.OAuthFacebook_AppAccessToken,this, "");
			URLThread_facebookData.start();
//		}
        return v;
    }

	public void getDataFrom(String uri)
	{
		URLThread_studentData=new URLThread(uri, this, "");
		URLThread_studentData.start();
	}
	
	@Override
	public void URLCallBack(String response)
	{
		try
		{
			if(URLThread_studentData!=null)
			{
				JSONObject json=new JSONObject(response);
				Iterator<String> K=json.keys();
				String curKey="";
				LinearLayout ll=(LinearLayout) v.findViewById(R.id.linearLayout_profile_info);
				while(K.hasNext())
				{
					curKey=K.next();
					TextView tv=new TextView(this.getActivity());
					tv.setText((curKey)+" : "+json.getString(curKey));
					ll.addView(tv);
				}
				//TODO
				String fbid="870205250";
				URLThread_facebookData=new URLThread("https://graph.facebook.com/"+fbid+"?fields=picture&access_token="+Connect.OAuthFacebook_AppAccessToken,this, "");
				URLThread_facebookData.start();
			}
			if(URLThread_facebookData!=null)
			{
				JSONObject json=new JSONObject(response);
				final String ppURL=json.getJSONObject("picture").getJSONObject("data").getString("url");
				final view_profile X=this;
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						final Bitmap pp=loadBitmap(ppURL);
						X.getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								(	(ImageView) (X.v.findViewById(R.id.imageview_profile_picture)) ).setImageBitmap(pp);
							}
						});
					}
				}).start();
				
				
			}
		}catch(Exception e){e.printStackTrace();}
		
	}
	
	
	public static Bitmap loadBitmap(String url) {
	    Bitmap bitmap = null;
	    InputStream in = null;
	    BufferedOutputStream out = null;
	    int i;
	    try {
	        in = new BufferedInputStream(new URL(url).openStream(), 200);

	        final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
	        out = new BufferedOutputStream(dataStream, 200);
//	        copy(in, out);
	        byte buff[]=new byte[100];
	        while((i=in.read(buff))>0)
	        		out.write(buff, 0, i);
	        out.flush();
	        out.close();
	        in.close();

	        final byte[] data = dataStream.toByteArray();
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        //options.inSampleSize = 1;

	        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);
	        
	    } catch (Exception e) {
//	        Log.e(TAG, "Could not load Bitmap from: " + url);
	    } finally {
//	        closeStream(in);
//	        closeStream(out);
	    }

	    return bitmap;
	}
	
}
