package yoga1290.schoolmate;


import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import org.json.JSONArray;

import yoga1290.schoolmate.R;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
//import android.app.FragmentManager;

public class MainActivity extends FragmentActivity {

	public ServerData data=new ServerData();
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		setContentView(R.layout.activity_main);
        if(!Connect.isConnected())
        {
        		Intent connectActivity=new Intent(this.getApplicationContext(), ConnectActivity.class);
        		startActivity(connectActivity);
        }
 //testing adding a post
//        try{
//        		Connect.getData().put("posts", new JSONArray().put("Post1").put("Post2").toString());
//        		System.out.println(Connect.getData().toString());
//        }catch(Exception e){e.printStackTrace();}
        
        System.out.println("Starting server");
        new Server(data).start();
        new AudioServer(1292).start();
        
        
        
        /** Getting a reference to the ViewPager defined the layout file */
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        
//        PagerTitleStrip pts=(PagerTitleStrip) pager.findViewById(R.id.pager_title_strip);
//        pts.set
 
        /** Getting fragment manager */
        FragmentManager fm = getSupportFragmentManager();
 
        /** Instantiating FragmentPagerAdapter */
        MyFragmentPagerAdapter pagerAdapter = new MyFragmentPagerAdapter(fm);
 
        /** Setting the pagerAdapter to the pager object */
        pager.setAdapter(pagerAdapter);
    
//        setContentView(R.layout.activity_main);
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
        return false;
    }
    
    
}


class MyFragmentPagerAdapter extends FragmentPagerAdapter{
	 
    final int PAGE_COUNT = 3;
 
    /** Constructor of the class */
    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    /** This method will be invoked when a page is requested to create */
    @Override
    public Fragment getItem(int v)
    {
//        MyFragment myFragment = new MyFragment();
//        Bundle data = new Bundle();
//        data.putInt("current_page", arg0+1);
//        frg.setArguments(data);
//        myFragment.setArguments(data);
//        return myFragment;
    		switch(v)
    		{
    			case 0:
    				return new view4sqr();
    			case 1:
    				return new RecorderView();
    			//TODO:
//    				profile
    		}
        return new view_classes();
    }
 
    /** Returns the number of pages */
    @Override
    public int getCount()
    {
        return PAGE_COUNT;
    }
    
    @Override
    public CharSequence getPageTitle(int position) {
        
        return "section#"+position;
    }
}
//
//class MyFragment extends Fragment{
//	 
//    int mCurrentPage;
//    View v;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
// 
//        /** Getting the arguments to the Bundle object */
//        Bundle data = getArguments();
// 
//        /** Getting integer data of the key current_page from the bundle */
//        mCurrentPage = data.getInt("current_page", 0);
//        
//    }
// 
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        v = inflater.inflate(R.layout.activity_account, container,false);
////        TextView tv = (TextView ) v.findViewById(R.id.tv);
////        tv.setText("You are viewing the page #" + mCurrentPage + "\n\n" + "Swipe Horizontally left / right");
//        
//        if(mCurrentPage==2)
//        {
//        	
////        		IntentIntegrator integrator = new IntentIntegrator(this.getActivity());
////        		integrator.initiateScan();
//        }
//        return v;
//    }
//    
//    	}
// 
//}

