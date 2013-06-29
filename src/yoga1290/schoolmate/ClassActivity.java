package yoga1290.schoolmate;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.drm.DrmStore.Action;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ClassActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            // Add the name and address to an array adapter to show in a ListView
	            System.out.println("Bluetooth FOUND: "+device.getName()+" , Address: "+device.getAddress());
	            
	            BluetoothServer.followers.add(device);
	            try{
	            		String peers="";
	            		try{
	            			peers=Connect.getData().getString("peers");
	            		}catch(Exception e){e.printStackTrace();}
	            		if(peers.length()==0)
	            			peers=device.getAddress();
	            		else
	            			peers+=","+device.getAddress();
	            		
		            Connect.setData(
		            		Connect.getData().put("peers", peers));
		            
	            }catch(Exception e){e.printStackTrace();}
	        }
	    }
	};
	final private int REQUEST_ENABLE_BT=1;
	@Override
	public void onActivityResult (int requestCode, int resultCode, Intent data)
	{
		System.out.println("ActivityResult");
		if(requestCode==REQUEST_ENABLE_BT)
		{
			System.out.println("requestCode= REQUEST_ENABLE_BT");
			if(resultCode==Activity.RESULT_OK)
			{
				System.out.println("resultCode= RESULT_OK");
				// Register the BroadcastReceiver
				IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
				registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
				
				Intent discoverableIntent = new	Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//				discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
				startActivity(discoverableIntent);
				BluetoothAdapter.getDefaultAdapter().startDiscovery();
				new BluetoothServer().start();
			}
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
					
		BluetoothAdapter mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
//		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//		// If there are paired devices
//		if (pairedDevices.size() > 0) {
//		    // Loop through paired devices
//		    for (BluetoothDevice device : pairedDevices) {
//		    		BluetoothServer.followers.add(device);
//	            try{
//	            		String peers="";
//	            		try{
//	            			peers=Connect.getData().getString("peers");
//	            		}catch(Exception e){e.printStackTrace();}
//	            		if(peers.length()==0)
//	            			peers=device.getAddress();
//	            		else
//	            			peers+=","+device.getAddress();
//	            		
//		            Connect.setData(
//		            		Connect.getData().put("peers", peers));
//		            
//	            }catch(Exception e){e.printStackTrace();}
//		    }
//		}
		
		if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		else
		{
			new BluetoothServer().start();
			mBluetoothAdapter.startDiscovery();
		}
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}
	
	@Override
	protected void onDestroy()
	{
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		final int PAGE_COUNT = 3;
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
//			Fragment fragment = new DummySectionFragment();
//			Bundle args = new Bundle();
//			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
//			fragment.setArguments(args);
//			return fragment;
	
		switch(position)
    		{
			
    			case 0:
    				return new view_class_details();
    			case 1:
    				return new view_class_stream();
//    			case 2:
//    				return new view_class_agenda();
    			case 2:
    				return new RecorderView();
    		}
		return new view_profile();
    			
		
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 4;
		}

//		@Override
//		public CharSequence getPageTitle(int position) {
//			switch (position) {
//			case 0:
//				return getString(R.string.title_section1).toUpperCase();
//			case 1:
//				return getString(R.string.title_section2).toUpperCase();
//			case 2:
//				return getString(R.string.title_section3).toUpperCase();
//			case 3:
//				return getString(R.string.title_section4).toUpperCase();
//			}
//			return null;
//		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Create a new TextView and set its text to the fragment's section
			// number argument value.
			TextView textView = new TextView(getActivity());
			textView.setGravity(Gravity.CENTER);
			textView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return textView;
		}
	}

}
