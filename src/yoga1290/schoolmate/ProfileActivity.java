package yoga1290.schoolmate;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProfileActivity extends Activity {

	final ProfileActivity thisClass=this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		
		try{
	        try
	        {
	        		((TextView) findViewById(R.id.textview_profile_name)).setText(Connect.getData().getString("name"));
	        }catch(Exception e){e.printStackTrace();}
			
			
			new URLThread("http://yoga1290.appspot.com/schoolmate/student?id="+Connect.getData().getString("cur_profileid"), new URLThread_CallBack() {
				@Override
				public void URLCallBack(String response_profile) {
					
					try{
						final JSONObject userdata=new JSONObject(response_profile);
						try{
							new URLThread("https://graph.facebook.com/"+userdata.getString("fbid")+"?fields=picture&access_token="+Connect.OAuthFacebook_AppAccessToken,new URLThread_CallBack() {
								@Override
								public void URLCallBack(String fbpp)
								{
									
									try{
										final Bitmap pp=loadBitmap(
												new JSONObject(fbpp).getJSONObject("picture").getJSONObject("data").getString("url")
												);
										thisClass.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												(	(ImageView) (thisClass.findViewById(R.id.imageview_profile_picture)) ).setImageBitmap(pp);
											}
										});
									}catch(Exception e){e.printStackTrace();}//TODO when fbpp doesnt load
									
								}
							}, "").start();
						}catch(Exception e)// fbid not included in userdata?...try "pp"
						{
							e.printStackTrace();
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try{
										(	(ImageView) (thisClass.findViewById(R.id.imageview_profile_picture)) ).setImageBitmap(loadBitmap(userdata.getString("pp")));
									}catch(Exception e){e.printStackTrace();}
								}
							});
							
						}
						
						final String gpa[]=userdata.getString("gpa").split(",");
						final String classes[]=userdata.getString("classes").split(",");
						new Thread(new Runnable() {
							@Override
							public void run() {

								int i;
								
								runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										int i,GPA[]=new int[gpa.length];
										for (i = 0; i < gpa.length; i++)
											GPA[i]=Integer.parseInt(gpa[i]);
										
										((ImageView) thisClass.findViewById(R.id.imageview_profile_gpa)).setImageBitmap(Charts.getGPAGraph(400, 100, GPA));
									}
								});
								
								final LinearLayout ll=(LinearLayout) thisClass.findViewById(R.id.linearLayout_profile_classes);
								for(i=0;i<classes.length;i++)
								{
									new URLThread("http://yoga1290.appspot.com/schoolmate/class?id="+classes[i],
											new classCallbackResponse(thisClass, classes[i]), "").start();
									
								}
							}
						}).start();
								
								
								
						
					}catch(Exception e){e.printStackTrace();}//TODO when can't get the facebook id
					
				}
			}, "").start();
		
		}catch(Exception e){e.printStackTrace();}//TODO when smth wrong w URLConnection or in reading Connect
		
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
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


class classCallbackResponse implements URLThread_CallBack
{

	final private Activity currentActivity;
	final private String classId;
	public classCallbackResponse(Activity currentActivity,String classId)
	{
		this.currentActivity=currentActivity;
		this.classId=classId;
	}
	@Override
	public void URLCallBack(String class_response) {
		try{
			String tmp[]=new JSONObject(class_response).getString("schedule").split(",");
			int schedule[]=new int[tmp.length];
			for(int i=0;i<schedule.length;i++)
				schedule[i]=Integer.parseInt(tmp[i]);
			
			final Bitmap img=Charts.getClassTimetable(100, 100, 
					schedule);
			
			
			currentActivity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					LayoutInflater li = (LayoutInflater) currentActivity.getSystemService(currentActivity.LAYOUT_INFLATER_SERVICE);
					View v=li.inflate(R.layout.activity_profile_row_class, null);
					ImageView iv=((ImageView)v.findViewById(R.id.imageView_activity_profile_row_class));
					iv.setImageBitmap(img);
					iv.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							try{
								Connect.setData(Connect.getData().put("current_class", classId));
								currentActivity.startActivity(new Intent(currentActivity, ClassActivity.class)	);
							}catch(Exception e){e.printStackTrace();}
						}
					});
					((TextView) v.findViewById(R.id.textView_activity_profile_row_class)).setText(classId);
					((LinearLayout) currentActivity.findViewById(R.id.linearLayout_profile_classes)).addView(v);
					
				}
			});
			
		}catch(Exception e){e.printStackTrace();}
	}
	
}