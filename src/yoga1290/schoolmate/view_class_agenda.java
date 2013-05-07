package yoga1290.schoolmate;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class view_class_agenda extends Fragment
{
	View v;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) 
    {
        v=inflater.inflate(R.layout.view_class_agenda, container, false);
        
        LinearLayout ll=(LinearLayout) v.findViewById(R.id.linearLayout_class_agenda);        
        for(int i=0;i<3;i++)
        {
            TextView tv=new TextView(this.getActivity());
            tv.setText("Event#"+i);
            ll.addView(tv);
        }
        return v;
    }
	
	
}
