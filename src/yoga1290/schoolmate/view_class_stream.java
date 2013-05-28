package yoga1290.schoolmate;

import java.net.InetAddress;
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
		while(new Date().getTime()-t<2000);
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
						{
//							totalLength+=o;
//							data.add(ByteBuffer.wrap(buff, 0, o));
							ServerData.send2Followers("",buff,o);
						}			
			}
			
//			System.out.println("SENDING AUDIO DATA...");
//			if(totalLength>0)
//			{
//				byte all[]=new byte[totalLength];
//				int i,j,p=0;
//				for(i=0;i<data.size();i++)
//				{
//					buff=data.get(i).array();
//					for(j=0;j<buff.length;j++)
//						all[p++]=buff[j];
//				}
//				ServerData.send2Followers("",all,all.length);
//			}
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
			}catch(Exception e){e.printStackTrace();}
		}
	}
}
public class view_class_stream extends Fragment implements OnClickListener, OnTouchListener , URLThread_CallBack
{
	private View v;
	private long lastPress=0,lastRelease=0;
	private RecordingThread record=null;
	private Button button_post;
	Activity X=this.getActivity();
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
		final view_class_stream X=this;
		( (Button)v.findViewById(R.id.button_post_row_fb)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				try{
					new URLThread("https://graph.facebook.com/"+Connect.getData().getString("fbid")+"/feed?access_token="+Connect.getData().getString("fb_access_token"), X, "message="+txt).start();
				}catch(Exception e){e.printStackTrace();}
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
				}catch(Exception e){e.printStackTrace();}
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
//        View v2=inflater.inflate(R.layout.view_newpost, container, false);
//        ((TextView)v2.findViewById(R.id.textView_newpost_text)).setText("Post#1");
        
        ll.addView(v2);
        
        View v3=li.inflate(R.layout.view_post_row, null);
//        View v3=inflater.inflate(R.layout.view_newpost, container, false);
//        ((TextView)v3.findViewById(R.id.textView_newpost_text)).setText("Post#2");
        
        ll.addView(v3);
        
        X=this.getActivity();
        loadData();
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
						JSONObject json=new JSONObject();
						json.put("text", txt);
						//TODO IP fix
						ServerData.send2Followers(json.toString());
						
						ll.addView(postRowView(txt));
						
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