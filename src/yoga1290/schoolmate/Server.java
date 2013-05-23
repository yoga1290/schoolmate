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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
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
class ServerData // implements Parcelable
{
	public static LinkedList<String> followers=new LinkedList<String>();
	public static LinkedList<String> stream=new LinkedList<String>();
	public static TreeMap<String,Integer> preReservedPort=new TreeMap<String, Integer>();
	public static TreeMap<String,DataTransferThread> dataThread=new TreeMap<String, DataTransferThread>();
	public static String following="";
	
	public boolean isFree2Listen=true;
	public byte buff[];
	public static int lastUsedPort=1291;
	public static long lastPckTime=0;
	
	public static void addClient(String IP)
	{
		followers.add(IP);
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
			            if(resp.equals("NO"))	return;
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
						out.println("POST\n");//CMD
						
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
	@Override
	public void run()
	{
		try
		{
			ServerSocket ss = new ServerSocket(port);
            System.out.println("Waiting at port#"+port);
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
					ServerData.send2Followers(buff,offset);
				}
				audioTrack.stop();
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
            
            if(CMD.equals("POST"))
            {
				
            		String txt="",tmp;
            		System.out.println("POST");
            		while((tmp=in.readLine())!=null)
            			txt+=tmp;
            		ServerData.stream.add(txt); 
            		System.out.println(txt);
            		//TODO serverdata send to all excepted "recieved" list
            		
            		try{
            			JSONObject x=Connect.getData();
            			x.getJSONArray("posts").put(txt);
            			Connect.setData(x);
            		}catch(Exception e)
            		{
            			JSONObject x=Connect.getData().put("posts", new JSONArray().put(txt));
            			Connect.setData(x);
            		}
            		ServerData.send2Followers(txt);
            }
            else if(CMD.indexOf("LISTEN")>-1)
            {
            		long pckTime=new Long(CMD.split(" ")[1]);
//            		if(pckTime<=ServerData.lastPckTime)
//            		{
//            			out.println("NO");
//            			out.flush();
//            		}
//            		else
//            		{
//            			ServerData.lastPckTime=new Date().getTime();
//	            		System.out.println("LISTEN request...");
//					for(ServerData.lastUsedPort++; isPortAvailable(ServerData.lastUsedPort)==false;ServerData.lastUsedPort++);//port<65535 &&
//	            		int port=1291;
//	            		for(;!isPortAvailable(port);port++);
	            		int port=1291;
	            		
	            		
	            		if(ServerData.preReservedPort.containsKey(s.getInetAddress().toString()))
	            			port=ServerData.preReservedPort.get(s.getInetAddress().toString());
	            		else
	            		{
	            			for(port=1291;!isPortAvailable(port);port++);
	            			ServerData.preReservedPort.put(s.getInetAddress().toString(),port);
	            			ServerData.dataThread.put(s.getInetAddress().toString(), new DataTransferThread(port));
	            		}
					
	            		
	            		System.out.println("New LISTEN port at "+port);
	            		System.out.println("New LISTEN from "+s.getInetAddress().toString());
					//TODO LISTEN & pass it to everyone
	            		out.println("YES "+port);
//					ServerData.dataThread.get(s.getInetAddress().toString()).start();
	            		
//					ServerData.dataThread.get(s.getInetAddress().toString()).start();
					
					
					
					new DataTransferThread(port).start();	
					
					System.out.println("YES "+port);
					out.flush();
//            		}
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
            		
//            		System.err.println("Access token of "+service);
            		Connect.addService(service, access_token);
            		if(service.equals("facebook"))
	            		new URLThread("https://graph.facebook.com/me?access_token="+access_token, new facebook(), "").start();
            		
            		
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
