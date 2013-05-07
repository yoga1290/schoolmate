package yoga1290.schoolmate;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
/*
 * http://yoga1290.appspot.com/schoolmate/student?id=
 * {
 * 		"classes":"1,2,3,4,É",
 * 		"studentId":"1290"
 * 		 É
 * }
 * 
 * http://yoga1290.appspot.com/schoolmate/class?id=
 * {
 * 		É,
 * 		"schedule":"1,2,3,4,5,6É", //6 integers repersenting periods,each in form of 0bSMTWTF (Sunday,Monday,É)
 * }
 */
public class view_classes extends Fragment implements URLThread_CallBack
{
	View v;
	int curClass=0;
	boolean doneLoadingClasses=false;
	String classes[];
	public View classRowView(String name,final int ar[])
	{
		LayoutInflater li = (LayoutInflater) this.getActivity().getSystemService(this.getActivity().LAYOUT_INFLATER_SERVICE);
		View v=li.inflate(R.layout.view_classes_row, null);
		//TODO listeners
		((TextView) v.findViewById(R.id.textView_classes_row)).setText(name);
		ImageView img=((ImageView) v.findViewById(R.id.imageView_classes_row));
		final Context c=this.getActivity();
        img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try
				{
					String data=""+ar[0];
					for(int i=1;i<6;i++)
						data+=","+ar[i];
					Connect.addService("current_class",data);
				}catch(Exception e){e.printStackTrace();}
				
				startActivity(new Intent(c, ClassActivity.class)	);
			}
		});
        img.setImageBitmap(Charts.getClassTimetable(100, 100, ar));
//		img.setImageBitmap(Charts.getTimepiece(100, 100, ar));
		return v;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) 
    {
        v=inflater.inflate(R.layout.view_classes, container, false);        
        LinearLayout ll=(LinearLayout) v.findViewById(R.id.linearLayout_classes);
        for(int i=0;i<30;i++)
        {
            ll.addView(classRowView("Class#"+i, new int[]{(int)(120*Math.random())+1,(int)(120*Math.random())+1,(int)(120*Math.random())+1,(int)(120*Math.random())+1,(int)(120*Math.random())+1,(int)(120*Math.random())+1}));
            
        }
        
        try{
        		doneLoadingClasses=false;
    			//GET yoga1290.appspot.com/schoolmate/student?id=ID
    			new URLThread("http://yoga1290.appspot.com/schoolmate/student?id="+Connect.getData().getString(Connect.KEY_STUDENTID), this, "").start();
	    }catch(Exception e){
	    		startActivity(new Intent(this.getActivity(), ConnectActivity.class));
	    	}
        
        return v;
    }
	@Override
	public void URLCallBack(final String response)
	{
		try{
			if(!doneLoadingClasses)
			{
				doneLoadingClasses=true;
				JSONObject json=new JSONObject(response);
				System.out.println("REG Classes="+json.getString("classes"));
				classes=json.getString("classes").split(",");
				
				new URLThread("http://yoga1290.appspot.com/schoolmate/class?id="+classes[curClass], this,"").start();
			}
			else
			{
				JSONObject json=new JSONObject(response);
				if(curClass<classes.length)
				{
						final String tmp[]=json.getString("schedule").split(",");
						
						this.getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								System.out.println("Adding Class "+curClass);
								int ar[]=new int[6];
								for(int i=0;i<6;i++)// 6 periods
									ar[i]=Integer.parseInt(tmp[i]);
								((LinearLayout) v.findViewById(R.id.linearLayout_classes)).addView(classRowView("Class "+classes[curClass], ar ));
							}
						});
						if(curClass+1<classes.length)
							new URLThread("http://yoga1290.appspot.com/schoolmate/class?id="+classes[++curClass], this,"").start();
				}
			}
		}catch(Exception e){e.printStackTrace();}
	}
	
	
}
