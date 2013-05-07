package yoga1290.schoolmate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class view_class_details extends Fragment implements URLThread_CallBack
{
	View v;
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) 
    {
        v=inflater.inflate(R.layout.view_class_details, container, false);
        
        ImageView mv=(ImageView) v.findViewById(R.id.imageView_class_details);
//        try{
////        		Connect.getData().getString(Connect.KEY_STUDENTID);
//        		new URLThread("http://yoga1290.appspot.com/schoolmate/classes?student="+Connect.getData().getString(Connect.KEY_STUDENTID), this, "").start();
//        }catch(Exception e){
//        		startActivity(new Intent(this.getActivity(), ConnectActivity.class));
//        }
//        LinearLayout ll=(LinearLayout) v.findViewById(R.id.linearLayout_class_details);
//        for(int i=0;i<3;i++)
//        {
//            TextView tv=new TextView(this.getActivity());
//            tv.setText("detail#"+i);
//            ll.addView(tv);
//        }
        int schdule[]=new int[]{1,1,1,127,	100,40};
        try{
        		String data[]=Connect.getData().getString("current_class").split(",");
        		for(int i=0;i<6;i++)
        			schdule[i]=Integer.parseInt(data[i]);
//        		System.out.println(Connect.getData().getString("current_class"));
        }
        catch(Exception e){e.printStackTrace();}
        mv.setImageBitmap(Charts.getClassTimetable(500, 500, schdule));
        
        return v;
    }


	@Override
	public void URLCallBack(String response) {
		// TODO Auto-generated method stub
		
	}
	
	
}
