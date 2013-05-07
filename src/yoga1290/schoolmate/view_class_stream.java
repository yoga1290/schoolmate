package yoga1290.schoolmate;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


public class view_class_stream extends Fragment implements OnClickListener, URLThread_CallBack
{
	private View v;
	private Button button_post;
	Activity X=this.getActivity();
	LayoutInflater li;
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
		new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				try{
					//TODO check post format
				JSONArray posts=Connect.getData().getJSONArray("posts");
				System.out.println(posts.length()+" posts found");
					try{
						for(int i=0;i<posts.length();i++)
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
        button_post.setOnClickListener(this);
        
//        button_post.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				
//				return false;
//			}
//		});
//	    ll = (LinearLayout) inflater.inflate(R.id.linearLayout_class_stream,null);
		
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

						ll.addView(postRowView(txt));
						JSONObject json=new JSONObject();
						json.put("text", txt);
//						json.put("createdAt", new Date().getTime());
//						json.put("recieved", new JSONArray().put(InetAddress.getLocalHost().toString()));
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
}