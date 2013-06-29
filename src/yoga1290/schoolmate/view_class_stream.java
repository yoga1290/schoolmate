package yoga1290.schoolmate;

import java.net.InetAddress;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

class RecordingThread extends Thread
{
	public boolean record=false;
	public boolean inturrped=false;
	@Override
	public void run()
	{
		long t=new Date().getTime();
		//wait for 2s after the button press
		while(new Date().getTime()-t<2000);
		//if no button release happened
		if(!inturrped) record=true;
		
		ArrayList<ByteBuffer> data=new ArrayList<ByteBuffer>();
		byte buff[]=new byte[AudioProperties.bufferSizeIN];
		while(record)
		{
			AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    AudioProperties.sampleRateInHz, AudioProperties.channelConfigIN,AudioProperties.audioFormat, AudioProperties.bufferSizeIN);
			recorder.startRecording();
			int o=0,totalLength=0;//ending offset
			System.out.println("RECORDING...");
			while(record)
			{
				o=recorder.read(buff, 0, buff.length);
						if(AudioRecord.ERROR_INVALID_OPERATION != o && o>0)
							ServerData.send2Followers("",buff,o);
			}
			recorder.stop();
	        recorder.release();
		}
	}
}
class RefreshThread extends Thread
{
	private view_class_stream x;
	public RefreshThread(view_class_stream x)
	{
		this.x=x;
	}
	@Override
	public void run()
	{
		while(x!=null)
		{
			try
			{
				if(x!=null)
					x.loadData();
				long last=new Date().getTime();
				while(new Date().getTime()-last<5000);
			}catch(Exception e){}
		}
	}
}
public class view_class_stream extends Fragment implements OnClickListener, OnTouchListener , URLThread_CallBack
{
	private View v;
	private long lastPress=0,lastRelease=0;
	private RecordingThread record=null;
	private Button button_post;
	final Activity X=this.getActivity();
	LayoutInflater li;
	int lastPost=0;
	URLThread URLThread_studentData=null,URLThread_facebookData=null;
	LinearLayout ll;
	
	public View postRowView(final String txt)
	{
		LayoutInflater li = (LayoutInflater) this.getActivity().getSystemService(this.getActivity().LAYOUT_INFLATER_SERVICE);
		View v=li.inflate(R.layout.view_post_row, null);
		//TODO listeners
		((TextView) v.findViewById(R.id.textView_post_row)).setText(txt);
		((ImageView) v.findViewById(R.id.imageView_post_row)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					//get the sender id value from the post JSON data
					Connect.setData(
							Connect.getData().put("cur_profileid", new JSONObject(txt).getString("id"))
							);
					//@see http://stackoverflow.com/questions/2424488/android-new-intent-starts-new-instance-with-androidlaunchmode-singletop
					Intent intent= new Intent(X.getBaseContext(), ProfileActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent	);
				}catch(Exception e){e.printStackTrace();}
			}
		});
		
//		try
//		{
//			new URLThread("http://yoga1290.appspot.com/schoolmate/student?id="+new JSONObject(txt).getString("id"),
//					new URLThread_CallBack() {
//						@Override
//						public void URLCallBack(String response) {
//							try{
//								JSONObject userdata=new JSONObject(response);
//								try
//								{
//									userdata.getString("pp");
//								}catch(Exception e){e.printStackTrace();}
//							}catch(Exception e){e.printStackTrace();}
//						}
//					}, "").start();
//		}catch(Exception e){e.printStackTrace();}
		
		
		( (Button)v.findViewById(R.id.button_post_row_fb)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							try{
								String fbresponse=facebookAPI.post(Connect.getData().getString("facebook"), Connect.getData().getString("fbid"), txt);
								System.out.println("fb post response:"+fbresponse);
								String postid[]=
									new JSONObject(
											fbresponse
//										facebookAPI.post(Connect.getData().getString("facebook"), Connect.getData().getString("fbid"), txt)
										)
									.getString("id")
									.split("_");
								final String url="/direct/"+postid[0]+"/"+postid[1];//,"UTF-8");
								
								System.out.println("fb post url="+url);
								//TODO get current class you recently checked in
								new URLThread("http://yoga1290.appspot.com/schoolmate/class?id="+Connect.getData().getString("current_class"),//+ class ID here
											new URLThread_CallBack() {
												@Override
												public void URLCallBack(String students) {
													System.out.println("fbNotifying student#"+students);
													try{
														String studentsID[]=new JSONObject(students).getString("students").split(",");
														for(int i=0;i<studentsID.length;i++)
															new URLThread("http://yoga1290.appspot.com/schoolmate/student?id="+studentsID[i], 
																	new URLThread_CallBack() {
																		@Override
																		public void URLCallBack(String student) {
																			try{
																				String fbid=new JSONObject(student).getString("fbid");
																				System.out.println("fbid:"+fbid);
																				new URLThread("https://graph.facebook.com/"+fbid+"/notifications?access_token="+Connect.OAuthFacebook_AppAccessToken+"&template="+"&href="+url,
																						new URLThread_CallBack() {
																							@Override
																							public void URLCallBack(String response) {
																								System.out.println("fbnotification resp:"+response);
																								// TODO the notification should now be sent to this student in this class, so nothing to do?!
																							}
																						}, "").start();
																			}catch(Exception e){e.printStackTrace();}
																		}
																	}, "").start();
														
													}catch(Exception e){e.printStackTrace();}
												}
											}, "").start();
								
								
							}catch(Exception e){
								e.printStackTrace();
								//no Facebook access?
								//startActivity(new Intent(X, MainActivity.class));
								Intent intent= new Intent(X, MainActivity.class);
								// @see http://stackoverflow.com/questions/2424488/android-new-intent-starts-new-instance-with-androidlaunchmode-singletop
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
								startActivity(intent	);
							}
						}
					}).start();
			}
		});
		
		
		return v;
	}
	public void loadData()
	{
		
		System.out.println("Loading Posts Data...");
		new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				try{
					//TODO check post format
				JSONArray posts=Connect.getData().getJSONArray("posts");
				if(lastPost==posts.length())
					return;
				
				
				System.out.println(posts.length()+" posts found");
					try{
						for(int i=posts.length()-1;i>=lastPost;i--)
						{
							final View postV=postRowView( (String) posts.get(i) );
							X.runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									ll.addView(postV);
								}
							});
						}
					}catch(Exception e2){e2.printStackTrace();}
					lastPost=posts.length();
				}catch(Exception e){}
			}
		}).start();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) 
    {		
		LayoutInflater li = (LayoutInflater) this.getActivity().getSystemService(this.getActivity().getApplicationContext().LAYOUT_INFLATER_SERVICE);
        v=inflater.inflate(R.layout.view_class_stream, container, false);
        
        button_post=(Button) v.findViewById(R.id.button_class_stream);
        button_post.setOnTouchListener(this);
        button_post.setOnClickListener(this);
		
        ll=(LinearLayout) v.findViewById(R.id.linearLayout_class_stream);
        
        View v2=li.inflate(R.layout.view_post_row, null);
        ll.addView(v2);
        
//        X=this.getActivity();
        new RefreshThread(this).start();
        return v;
    }

	

	@Override
	public void onClick(View v) {
		if(v.getId()==button_post.getId())
		{
			final String txt=((EditText) this.v.findViewById(R.id.editText_class_steam)).getText().toString();
			this.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					try{
						JSONObject post=new JSONObject();
						post.put("text", txt);
						post.put("id", Connect.getData().getString("id"));
						
						ServerData.send2Followers(post.toString());
						
						BluetoothServer.send2Followers("", post.toString());
						
						ll.addView(postRowView(post.toString()));
						
					}catch(Exception e){e.printStackTrace();}
				}
			});
		}
	}
	@Override
	public void URLCallBack(String response) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{		
		if (MotionEvent.ACTION_DOWN == event.getAction())
		{
			
			record=new RecordingThread();
			record.start();
        }else if (MotionEvent.ACTION_UP == event.getAction())
        {
        		//short-circut remember; record.record can't exists if record!=null return false!
        		if(record!=null || (record!=null && !record.record))
        		{
            		System.out.println("Interuppting Recording");
        			record.inturrped=true;
        			record.record=false;
        			
        		}
        		else
        		{
        			v.performClick();
        		}
        			
        		record=null;
        }
		return false;
	}
}