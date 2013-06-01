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
//			URLThread_facebookData=new URLThread("https://graph.facebook.com/"+fbid+"?fields=picture&access_token="+Connect.OAuthFacebook_AppAccessToken,this, "");
//			URLThread_facebookData.start();
//		}
			
			URLThread_studentData=new URLThread("http://yoga1290.appspot.com/schoolmate/student?id=1", this, "");
			URLThread_studentData.start();
        return v;
    }
	private View getRowView(String txt)
	{
		LayoutInflater li = (LayoutInflater) this.getActivity().getSystemService(this.getActivity().LAYOUT_INFLATER_SERVICE);
		View v=li.inflate(R.layout.view_post_row, null);
		//TODO listeners
		((TextView) v.findViewById(R.id.textView_post_row)).setText(txt);
		return v;
	}
	
	@Override
	public void URLCallBack(String response)
	{
		final view_profile X=this;
		final String resp=response;
		try
		{
			if(URLThread_studentData!=null)
			{
				this.getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						((LinearLayout) v.findViewById(R.id.linearLayout_profile_info)).addView(getRowView("HELLO!"));
					}
				});
//				X.getActivity().runOnUiThread(new Runnable() {
//					@Override
//				public void run() {
//					try{
//						JSONObject json=new JSONObject(resp);
//						Iterator<String> K=json.keys();
//						while(K.hasNext())
//						{
//							String curKey=K.next();
//							System.out.println(curKey+":");
//							final View row=getRowView(curKey);
//							
//												((LinearLayout) X.v.findViewById(R.id.linearLayout_profile_info)).addView(row);
//		//										System.out.println(json.getString(curKey));
//		//									}catch(Exception e){e.printStackTrace();}	
//									}
//					}catch(Exception e){e.printStackTrace();}
//				}
//				});
					
//				}
				//TODO
				String fbid="870205250";// Connect.getData().getString("fbid");
				URLThread_facebookData=new URLThread("https://graph.facebook.com/"+fbid+"?fields=picture&access_token="+Connect.OAuthFacebook_AppAccessToken,this, "");
				URLThread_facebookData.start();
				URLThread_studentData=null;
			}
			if(URLThread_facebookData!=null)
			{
				JSONObject json=new JSONObject(response);
				final String ppURL=json.getJSONObject("picture").getJSONObject("data").getString("url");
				
				try{
					Connect.setData(
							Connect.getData().put("pp", ppURL)
							);
				}catch(Exception e){e.printStackTrace();}
				
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
				
				URLThread_facebookData=null;
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
