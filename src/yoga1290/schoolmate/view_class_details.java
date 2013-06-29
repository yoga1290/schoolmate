package yoga1290.schoolmate;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class view_class_details extends Fragment
{
	View v;
	final Activity currentActivity=this.getActivity();
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) 
    {
        v=inflater.inflate(R.layout.view_class_details, container, false);
        
        ImageView mv=(ImageView) v.findViewById(R.id.imageView_class_details);

        try{
	        new URLThread("http://yoga1290.appspot.com/schoolmate/class?id="+Connect.getData().getString("current_class"), new URLThread_CallBack() {
				
				@Override
				public void URLCallBack(String class_response) {
					try{
						String tmp[]=new JSONObject(class_response).getString("schedule").split(",");
						int schedule[]=new int[tmp.length];
						for(int i=0;i<schedule.length;i++)
							schedule[i]=Integer.parseInt(tmp[i]);
						
						final Bitmap timetable= Charts.getClassTimetable(500, 500, 
								schedule);
						
//						currentActivity.runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								// TODO Auto-generated method stub
								((ImageView) v.findViewById(R.id.imageView_class_details))
									.setImageBitmap(timetable);
//							}
//						});
						
					}catch(Exception e){e.printStackTrace();}
				}
			}, "").start();
        }catch(Exception e){e.printStackTrace();}
        
        return v;
    }
	
	
}
