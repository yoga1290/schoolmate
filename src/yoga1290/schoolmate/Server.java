package yoga1290.schoolmate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

class ServerProperties
{
	public static final int port=1290;
}
class AudioProperties
{
	public static final int 	sampleRateInHz=44100,//44100,
							channelConfigIN=AudioFormat.CHANNEL_IN_STEREO,
							channelConfigOUT=AudioFormat.CHANNEL_OUT_STEREO,
							audioFormat=AudioFormat.ENCODING_PCM_16BIT;
	public static final int bufferSizeIN = AudioRecord.getMinBufferSize(AudioProperties.sampleRateInHz,AudioProperties.channelConfigIN,AudioProperties.audioFormat);
	public static final int bufferSizeOUT = AudioRecord.getMinBufferSize(AudioProperties.sampleRateInHz,AudioProperties.channelConfigOUT,AudioProperties.audioFormat);
}

//not yet used
class AudioServer extends Thread
{
	
	public int PORT;
	private ServerSocket ss;
	
	public AudioServer(int port)
	{
		this.PORT=port;
	}
	@Override
	public void run()
	{
		try{
			ss=new ServerSocket(PORT);//(InetAddress.getByAddress(new byte[]{(byte)255,(byte)255,(byte)255,(byte)255}) , PORT);
			while(true)
			{
				 	Socket s = ss.accept();
				 	if(!s.getLocalAddress().equals(InetAddress.getLocalHost()))
				 		new AudioTrackThread(ss.accept()).start();
//				 	byte buff[]=new byte[AudioProperties.bufferSizeOUT];
//				 	AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 
//							AudioProperties.sampleRateInHz,//44100,//11025, 
//							AudioProperties.channelConfigOUT,//AudioFormat.CHANNEL_IN_STEREO,//AudioFormat.CHANNEL_CONFIGURATION_MONO,
//							AudioProperties.audioFormat,//AudioFormat.ENCODING_PCM_16BIT,//AudioFormat.ENCODING_PCM_16BIT, 
//							AudioProperties.bufferSizeOUT,// 
//							AudioTrack.MODE_STREAM);
//				 	
//				 	
//		            InputStream in=s.getInputStream();
//					audioTrack.play();
//					int offset=0;
//					
//					while((offset=in.read(buff))>0)
//						audioTrack.write(buff, 0, offset);
//					audioTrack.stop();
			}
		}catch(Exception e){e.printStackTrace();}
	}
}
class AudioTrackThread extends Thread
{
	public Socket s;
	public AudioTrackThread(Socket s)
	{
		this.s=s;
	}
	public void run() {
		try{
			byte buff[]=new byte[AudioProperties.bufferSizeOUT];
		 	AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 
					AudioProperties.sampleRateInHz,//44100,//11025, 
					AudioProperties.channelConfigOUT,//AudioFormat.CHANNEL_IN_STEREO,//AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioProperties.audioFormat,//AudioFormat.ENCODING_PCM_16BIT,//AudioFormat.ENCODING_PCM_16BIT, 
					AudioProperties.bufferSizeOUT,// 
					AudioTrack.MODE_STREAM);
		 	
		 	
            InputStream in=s.getInputStream();
			audioTrack.play();
			int offset=0;
			
			while((offset=in.read(buff))>0)
				audioTrack.write(buff, 0, offset);
			audioTrack.stop();
		}catch(Exception e){e.printStackTrace();}
	}
}
class ServerData // implements Parcelable
{
	public static LinkedList<String> followers=new LinkedList<String>();
//	public static LinkedList<String> stream=new LinkedList<String>();
	public static TreeMap<String,Integer> preReservedPort=new TreeMap<String, Integer>();
	public static TreeMap<String,DataTransferThread> dataThread=new TreeMap<String, DataTransferThread>();
//	public static String following="";
	
	public boolean isFree2Listen=true;
	public byte buff[];
	public static int lastUsedPort=1291;
	public static long lastPckTime=0;
	
	public static void addClient(String IP)
	{
		followers.add(IP);
	}
	public static void broadcast(final byte data[],final int offset,final int port)
	{
		new Thread(new Runnable() {
			@Override
			public void run()
			{
				try
				{
					Socket s=new Socket(InetAddress.getByAddress(new byte[]{(byte)0,(byte)0,(byte)0,(byte)0}) , port);
					s.getOutputStream().write(data,0,offset);
					s.close();
				}catch(Exception e){e.printStackTrace();}
				
			}
		}).start();
	}
	public static void send2Followers(final byte data[],final int offset)
	{
		final Iterator<String> it=followers.iterator();
		while(it.hasNext())
		{
			final String follower=it.next();
			new Thread(new Runnable() {
				
				@Override
				public void run() 
				{
					try
					{
						int p=0,o;
						byte ip[]=new byte[4];
						String cur=follower;
						while(p<4 && (o=cur.indexOf("."))>-1)
						{
							ip[p++]=(byte) Integer.parseInt(cur.substring(0,o));
							cur=cur.substring(o+1);
						}
						ip[3]=(byte) Integer.parseInt(cur);
						System.out.println("Connecting to "+follower);
						
						
						Socket s=new Socket(InetAddress.getByAddress(ip) , ServerProperties.port);
					
						PrintWriter out=new PrintWriter(s.getOutputStream());
			            out.println("LISTEN "+new Date().getTime());
			            out.flush();
//			            out.close();
			            System.out.println("waiting for response b4 data...");
			            BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream()));
			            String resp=in.readLine();
			            if(resp.equals("NO"))
			            {
			            		s.close();
			            		return;
			            }
			            //TODO FIX; connection timeouts here but why?!!
			            
			            System.out.println(s.getInetAddress()+" response: "+resp);
			            out.close();
			            in.close();
			            s.close();
						
						Socket s2=new Socket(InetAddress.getByAddress(ip) , 
										Integer.parseInt(resp.split(" ")[1])	);
						s2.getOutputStream().write(data,0,offset);
						s2.close();
						
//						out.close();
//			            in.close();
//			            s.close();
					}catch(Exception e){e.printStackTrace();}
					
				}
			}).start();
		}
	}
	
	//senders: received clients other than me
	public static void send2Followers(final String senders,final byte data[],final int offset)
	{
		
		HashSet<String> visitedIP=new HashSet<String>();
		String ips[]=senders.split(",");
		for(int i=0;i<ips.length;i++)
			visitedIP.add(ips[i]);
		
		final Iterator<String> it=followers.iterator();
		while(it.hasNext())
		{
			final String follower=it.next();
			
			if(visitedIP.contains(follower))		continue;
			
			new Thread(new Runnable() {
				
				@Override
				public void run() 
				{
					try
					{
						int p=0,o;
						byte ip[]=new byte[4];
						String cur=follower;
						while(p<4 && (o=cur.indexOf("."))>-1)
						{
							ip[p++]=(byte) Integer.parseInt(cur.substring(0,o));
							cur=cur.substring(o+1);
						}
						ip[3]=(byte) Integer.parseInt(cur);
						System.out.println("Connecting to "+follower);
						
						
						Socket s=new Socket(InetAddress.getByAddress(ip) , ServerProperties.port);
					
						PrintWriter out=new PrintWriter(s.getOutputStream());
			            out.println("LISTEN");
			            out.println(senders+","+s.getLocalAddress().toString());
			            out.flush();
//			            out.close();
			            System.out.println("waiting for response b4 data...");
			            BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream()));
			            String resp=in.readLine();
			            if(resp.equals("NO"))
			            	{
			            		s.close();
			            		return;
			            	}
			            //TODO FIX; connection timeouts here but why?!!
			            
			            System.out.println(s.getInetAddress()+" response: "+resp);
			            out.close();
			            in.close();
			            s.close();
						
						Socket s2=new Socket(InetAddress.getByAddress(ip) , 
										Integer.parseInt(resp.split(" ")[1])	);
						s2.getOutputStream().write(data,0,offset);
						s2.close();
						
//						out.close();
//			            in.close();
//			            s.close();
					}catch(Exception e){e.printStackTrace();}
					
				}
			}).start();
		}
	}
	
	public static void send2Followers(final String JSON)
	{
		final Iterator<String> it=followers.iterator();
		while(it.hasNext())
		{
			final String follower=it.next();
			new Thread(new Runnable() {
				
				@Override
				public void run() 
				{
					try
					{
						int p=0,o;
						byte ip[]=new byte[4];
						String cur=follower;
						while(p<4 && (o=cur.indexOf("."))>-1)
						{
							ip[p++]=(byte) Integer.parseInt(cur.substring(0,o));
							cur=cur.substring(o+1);
						}
						ip[3]=(byte) Integer.parseInt(cur);
						System.out.println("Connecting to "+follower);
						
						
						Socket s=new Socket(InetAddress.getByAddress(ip) , ServerProperties.port);
					
						PrintWriter out=new PrintWriter(s.getOutputStream());
						out.println("POST\n"+s.getLocalAddress().toString()+"\n");//CMD
						
						out.println(JSON);
			            out.flush();
//			            out.close();
//			            System.out.println("waiting for response b4 data...");

			            out.close();
			            s.close();
					}catch(Exception e){e.printStackTrace();}
					
				}
			}).start();
		}
	}
	
	//SenderIPs, including yours
	public static void send2Followers(final String senderIPs,final String JSON)
	{
		final Iterator<String> it=followers.iterator();
		HashSet<String> visitedIP=new HashSet<String>();
		String ips[]=senderIPs.split(",");
		for(int i=0;i<ips.length;i++)
			visitedIP.add(ips[i]);
		
		while(it.hasNext())
		{
			final String follower=it.next();
			
			//don't send it back!
			if(visitedIP.contains(follower))
				continue;
			
			new Thread(new Runnable() {
				
				@Override
				public void run() 
				{
					try
					{
						int p=0,o;
						byte ip[]=new byte[4];
						
						
						String cur=follower;
						
						while(p<4 && (o=cur.indexOf("."))>-1)
						{
							ip[p++]=(byte) Integer.parseInt(cur.substring(0,o));
							cur=cur.substring(o+1);
						}
						ip[3]=(byte) Integer.parseInt(cur);
						System.out.println("Connecting to "+follower);
						
						
						Socket s=new Socket(InetAddress.getByAddress(ip) , ServerProperties.port);
					
						PrintWriter out=new PrintWriter(s.getOutputStream());
						out.println("POST\n"+senderIPs+"\n");//CMD
						
						out.println(JSON);
			            out.flush();
//			            out.close();
//			            System.out.println("waiting for response b4 data...");

			            out.close();
			            s.close();
					}catch(Exception e){e.printStackTrace();}
					
				}
			}).start();
		}
	}
//	@Override
//	public int describeContents() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		// TODO Auto-generated method stub
////		dest.writeMap(val)
//	}
}

class DataTransferThread extends Thread implements Runnable
{
	private int port=1291;
	private byte buff[];
	private AudioTrack audioTrack;
	private ServerSocket ss;
	
	private String senders="";
	
	@Deprecated
	public DataTransferThread(int port)//,ServerData data)
	{
		this.port=port;
		this.buff=new byte[AudioProperties.bufferSizeOUT];
		this.audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 
				AudioProperties.sampleRateInHz,//44100,//11025, 
				AudioProperties.channelConfigOUT,//AudioFormat.CHANNEL_IN_STEREO,//AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioProperties.audioFormat,//AudioFormat.ENCODING_PCM_16BIT,//AudioFormat.ENCODING_PCM_16BIT, 
				AudioProperties.bufferSizeOUT,// 
				AudioTrack.MODE_STREAM);
	}
	public DataTransferThread(int port,String senders)
	{
		this.port=port;
		this.senders=senders;
		this.buff=new byte[AudioProperties.bufferSizeOUT];
		this.audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 
				AudioProperties.sampleRateInHz,//44100,//11025, 
				AudioProperties.channelConfigOUT,//AudioFormat.CHANNEL_IN_STEREO,//AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioProperties.audioFormat,//AudioFormat.ENCODING_PCM_16BIT,//AudioFormat.ENCODING_PCM_16BIT, 
				AudioProperties.bufferSizeOUT,// 
				AudioTrack.MODE_STREAM);
	}
	@Override
	public void run()
	{
		try
		{
			ServerSocket ss = new ServerSocket(port);
            System.out.println("Waiting at port#"+port);
//            while(true)
//            {
	            Socket s = ss.accept();
	            System.out.println("LISTENing at port#"+port);
	            InputStream in=s.getInputStream();
				// Create a new AudioTrack object using the same parameters as the AudioRecord
				// object used to create the file.
				// Start playback
				audioTrack.play();
				int offset=0;
				
				while((offset=in.read(buff))>0)
				{
					// Write the music buffer to the AudioTrack object
					audioTrack.write(buff, 0, offset);
					//Pass what you hear to your followers
//					ServerData.send2Followers(buff,offset);
					ServerData.send2Followers(senders,buff,offset);
				}
				audioTrack.stop();
//            }
//            ss.setReuseAddress(true);
		}catch(Exception e){e.printStackTrace();}
	}
}
class ServerRequestHandler extends Thread implements Runnable
{
	private Socket s;
	private ServerData data;
	public ServerRequestHandler(Socket s)//,ServerData data)
	{
		this.s=s;
//		this.data=data;
	}
	public static boolean isPortAvailable(int port) 
	{
		ServerSocket ss = null;
		try 
		{
	        ss = new ServerSocket(port);
	        ss.setReuseAddress(true);
	        return true;
    		} catch (Exception e) {}
		finally
	    {
	        if (ss != null) 
	        {
	            try {
	                ss.close();
	            } catch (Exception e) {}
	        }
	    }
    return false;
	}
	@Override
	public void run()
	{
		try
		{
			System.out.println("Command from "+s.getInetAddress()+">");
			BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream()));
            String CMD=in.readLine();
//            while(CMD==null)
//            		CMD=in.readLine();
            System.out.println((CMD==null ? "NULL?!!":CMD)+"<");
//            if(CMD==null)
//            {
//            		System.out.println("NO CMD was sent");
//            		s.close();
//            		return;
//            }
            PrintWriter out=new PrintWriter(s.getOutputStream());
            
            //new stream post?
            if(CMD.equals("POST"))
            {
            		String txt="",tmp,senderIP;
            		System.out.println("POST");
//            		System.out.println("from IP:"+senderIP);
//            		System.out.println("Local IP:"+s.getLocalAddress().toString());
//            		System.out.println("Remote IP:"+s.getInetAddress().toString());
            		HashSet<String> receivedIPs=new HashSet<String>();
            		String sendersln=in.readLine();
            		String senders[]=sendersln.split(",");
            		int i;
            		for(i=0;i<senders.length;i++)
            			receivedIPs.add(senders[i]);
            		Iterator<String> it_friends=ServerData.followers.iterator();
            		String cur;
            		//include the people you'll notify, so no one will notify them later!
            		while(it_friends.hasNext())
            		{
            			cur=it_friends.next();
            			if(!receivedIPs.contains(cur))
            				sendersln+=","+cur;
            		}
            		
	            		while((tmp=in.readLine())!=null)
	            			txt+=tmp;
	            		System.out.println(txt);
	            		try{
	            			JSONObject x=Connect.getData();
	            			x.getJSONArray("posts").put(txt);
	            			Connect.setData(x);
	            		}catch(Exception e)
	            		{
	            			JSONObject x=Connect.getData().put("posts", new JSONArray().put(txt));
	            			Connect.setData(x);
	            		}
	            		ServerData.send2Followers(sendersln,txt);
            }
            else if(CMD.equals("LISTEN"))
            {
            		HashSet<String> receivedIPs=new HashSet<String>();
            		String sendersln=in.readLine();
            		String senders[]=sendersln.split(",");
            		int i;
            		for(i=0;i<senders.length;i++)
            			receivedIPs.add(senders[i]);
            		Iterator<String> it_friends=ServerData.followers.iterator();
            		String cur;
            		//include the people you'll notify, so no one will notify them later!
            		while(it_friends.hasNext())
            		{
            			cur=it_friends.next();
            			if(!receivedIPs.contains(cur))
            				sendersln+=","+cur;
            		}
            		
            		
            		System.out.println("My Address: "+s.getLocalAddress());
            		System.out.println("Senders addresses: "+sendersln);
            		
            		//if you are one of the senders....this will NEVER happen; handled b4 sending!
            		if(receivedIPs.contains(s.getLocalAddress().toString()))
            		{
            			System.out.println("DUPLICATION ignored from:"+sendersln);
            			s.close();
            			return;
            		}
            		
            		
	            	int port=1291;
	            	if(ServerData.preReservedPort.containsKey(s.getInetAddress().toString()))
	            		port=ServerData.preReservedPort.get(s.getInetAddress().toString());
	            	else
	            	{
	            		for(port=1291;!isPortAvailable(port);port++);
	            		ServerData.preReservedPort.put(s.getInetAddress().toString(),port);
//	            		ServerData.dataThread.put(s.getInetAddress().toString(), new DataTransferThread(port));
	            	}
				System.out.println("New LISTEN port at "+port);
	            	System.out.println("New LISTEN from "+s.getInetAddress().toString());
	            	out.println("YES "+port);
				new DataTransferThread(port,sendersln).start();
				System.out.println("YES "+port);
				out.flush();
            }

            //localhost:1290/foursquare?access_token=AAAAAAA
            else if(CMD.indexOf("?access_token=")>-1)
            {
            		String service=CMD.substring(0,CMD.indexOf("?access_token="));
            		service=service.substring(service.lastIndexOf("/")+1,service.length());
            		String access_token=CMD.substring(CMD.indexOf("?access_token=")+14);
            		if(access_token.indexOf(" ")!=-1)
            			access_token=access_token.substring(0,access_token.indexOf(" "));
            		while((CMD=in.readLine())!=null); //ignore the rest of input
            		
            		System.out.println("Access token of "+service);
            		Connect.setData(
            				Connect.getData().put(service, access_token));
            		
            		//TODO give the web browser a better response
            		OutputStream out2=s.getOutputStream();
            		
            		String msg="<html><head><title>Done :)</title></head><body>Now,you can close the browser<script>window.close();</script></body></html>";
            		
            		String	header = "HTTP/1.0 200 OK\r\n"+
                            "Allow: GET\r\n"+
                            "MIME-Version: 1.0\r\n"+
                            "Server : HMJ Basic HTTP Server\r\n"+
                            "Content-Type: "+"text/html" + "\r\n"+
                            "Content-Length: "+ msg.getBytes().length +
                            "\r\n\r\n";
            		out2.write((header+msg+"\r\n").getBytes());
            		out2.close();
            		s.close();
            		
            		if(service.equals("facebook"))
	            		new URLThread("https://graph.facebook.com/me?access_token="+access_token, new facebook(), "").start();
         
            }
            else if(CMD.equals("AddMe"))
            {
            		System.out.println("Adding new follower "+s.getInetAddress().getHostAddress());
        			ServerData.addClient(s.getInetAddress().getHostAddress());
            }
            
            in.close();
            out.close();
            s.close();
            System.out.println("closing socket");
		}catch(Exception e){e.printStackTrace();}
		
		
	}
	
}
public class Server extends Thread
{
	public ServerData data;
	public Server(ServerData data)
	{
		this.data=data;
	}
	@Override
	public void run()
	{
		try {
			System.out.println("Starting thread");
            ServerSocket ss = new ServerSocket(1290);
            while(true)
            {
            		Socket s = ss.accept();            		
            		new ServerRequestHandler(s).start();//new Thread to deal with this request
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}



/**
 * 
 * @author youssefgamil
 * @see http://developer.android.com/guide/topics/connectivity/bluetooth.html
 */
class BluetoothServer extends Thread
{
	private boolean running=true;
	private final BluetoothServerSocket mmServerSocket;
	private BluetoothAdapter mBluetoothAdapter;
	public static ArrayList<BluetoothDevice> followers=new ArrayList<BluetoothDevice>();
	public BluetoothServer()
	{
//		this.data=data;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
        	//TODO
        		String t=BluetoothAdapter.getDefaultAdapter().getAddress();
        		while(t.indexOf(":")!=-1)
        			t=t.replaceAll(":","0");
        		//t=17
        		t+="129000000000000";
        
        		t=t.substring(0,8)+"-"+t.substring(8,12)+"-"+t.substring(12,16)+"-"+t.substring(16, 20)+"-"+t.substring(20,32);
//            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(BluetoothAdapter.getDefaultAdapter().getName(), UUID.fromString("00001101-0000-1000-8000-00805f9b34fb") );
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(BluetoothAdapter.getDefaultAdapter().getName(), UUID.fromString(t) );
        } catch (Exception e) { e.printStackTrace();}
        mmServerSocket = tmp;

        
	}
	@Override
	public void run()
	{
			BluetoothSocket socket = null;
	        // Keep listening until exception occurs or a socket is returned
	        while (running)
	        {
	            try {
	                socket = mmServerSocket.accept();
	                if (socket == null) continue;
	                socket.connect();
		            // If a connection was accepted
//		            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
		            
		                // Do work to manage the connection (in a separate thread)
	//	                manageConnectedSocket(socket);
	                
		            new BluetoothServerRequestHandler(socket).start();
		            
		            
	            }catch(Exception e){
	            	// Unable to connect; close the socket and get out
	                try {
	                    socket.close();
	                } catch (Exception closeException) { }
	                return;
	            	}
	        }
	        
	        
	        try{
	        		mmServerSocket.close();
	        }catch(Exception e){
//	        		e.printStackTrace();
	        	}
	}
	
	//SenderIPs, including yours
	public static void send2Followers(final String senders,final String JSON)
	{
		final Iterator<BluetoothDevice> it=followers.iterator();
		HashSet<String> visitedIP=new HashSet<String>();
		String mac[]=senders.split(",");
		for(int i=0;i<mac.length;i++)
			visitedIP.add(mac[i]);
		
		while(it.hasNext())
		{
			final BluetoothDevice follower=it.next();
			
			//don't send it back!
			if(visitedIP.contains(follower.getAddress()))
				continue;
			
			new Thread(new Runnable() {
				
				@Override
				public void run() 
				{
					try
					{
						BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
						
						int p=0,o;
						System.out.println("Connecting to "+follower.getName());
			        		String t=follower.getAddress();
			        		while(t.indexOf(":")!=-1)
			        			t=t.replaceAll(":","0");
			        		//t=17
			        		t+="129000000000000";
			        
			        		t=t.substring(0,8)+"-"+t.substring(8,12)+"-"+t.substring(12,16)+"-"+t.substring(16, 20)+"-"+t.substring(20,32);
			        		
//						BluetoothSocket s=follower.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
						BluetoothSocket s=follower.createRfcommSocketToServiceRecord(UUID.fromString(t));
						s.connect();
						
						PrintWriter out=new PrintWriter(s.getOutputStream());
						out.println("POST\n"+senders+"\n");//CMD
						
						out.println(JSON);
//			            out.flush();
//			            out.close();
//			            System.out.println("waiting for response b4 data...");

//			            out.close();
			            s.close();
					}catch(Exception e){e.printStackTrace();}
					
				}
			}).start();
		}
	}
}

class BluetoothServerRequestHandler extends Thread implements Runnable
{
	private BluetoothSocket s;
	public BluetoothServerRequestHandler(BluetoothSocket s)//,ServerData data)
	{
		this.s=s;
//		this.data=data;
	}
	@Override
	public void run()
	{
		try
		{
//			s.connect();
			System.out.println("Command from "+s.getRemoteDevice().getAddress()+">");
			
			BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream()));
            String CMD=in.readLine();
//            while(CMD==null)
//            		CMD=in.readLine();
            System.out.println((CMD==null ? "NULL?!!":CMD)+"<");
//            if(CMD==null)
//            {
//            		System.out.println("NO CMD was sent");
//            		s.close();
//            		return;
//            }
            PrintWriter out=new PrintWriter(s.getOutputStream());
            
            //new stream post?
            if(CMD.equals("POST"))
            {
            		String txt="",tmp,senderIP;
            		System.out.println("POST");
//            		System.out.println("from IP:"+senderIP);
//            		System.out.println("Local IP:"+s.getLocalAddress().toString());
//            		System.out.println("Remote IP:"+s.getInetAddress().toString());
            		HashSet<String> receivedIPs=new HashSet<String>();
            		String sendersln=in.readLine();
            		String senders[]=sendersln.split(",");
            		int i;
            		for(i=0;i<senders.length;i++)
            			receivedIPs.add(senders[i]);
            		Iterator<BluetoothDevice> it_friends=BluetoothServer.followers.iterator();
            		String cur;
            		//include the people you'll notify, so no one will notify them later!
            		while(it_friends.hasNext())
            		{
            			cur=it_friends.next().getAddress();
            			if(!receivedIPs.contains(cur))
            				sendersln+=","+cur;
            		}
            		
	            		while((tmp=in.readLine())!=null)
	            			txt+=tmp;
	            		System.out.println(txt);
	            		try{
	            			JSONObject x=Connect.getData();
	            			x.getJSONArray("posts").put(txt);
	            			Connect.setData(x);
	            		}catch(Exception e)
	            		{
	            			JSONObject x=Connect.getData().put("posts", new JSONArray().put(txt));
	            			Connect.setData(x);
	            		}
	            		BluetoothServer.send2Followers(sendersln,txt);
            		
            }
            else if(CMD.equals("LISTEN"))
            {
            		//TODO
            }
//            else if(CMD.equals("AddMe"))
//            {
//            		System.out.println("Adding new follower "+s.getRemoteDevice().getAddress());
//        			ServerData.addClient(s.getRemoteDevice().getAddress());
//            }
            
//            in.close();
//            out.close();
            s.close();
            System.out.println("closing socket");
		}catch(Exception e){e.printStackTrace();}
		
		
	}
	
}