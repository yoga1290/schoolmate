package yoga1290.schoolmate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;

import android.support.v4.app.Fragment;

interface URLThread_CallBack
{
	public void URLCallBack(String response);
}


public class URLThread extends Thread
{
	
	
	private String url,POSTdata="";
	private URLThread_CallBack callback;
	public URLThread(String url,URLThread_CallBack callback,String POSTdata)
	{
		this.url=url;
		this.callback=callback;
		this.POSTdata=POSTdata;
	}
	
	public static String HTTPPost(String url,String POSTdata) throws Exception
    {
        String host=url.substring(0,url.indexOf("/"));
        String req=url.substring(url.indexOf("/"),url.length());
        
        Socket s=new Socket(InetAddress.getByName(host), 80);
        OutputStream out=s.getOutputStream();
        InputStream in=s.getInputStream();
        
        String header=  "POST "+req+" HTTP/1.0\r\n" +
                        "HOST: "+host+"\r\n" +
                        "Content-length: "+POSTdata.getBytes().length+" \r\n" +
                        "Content-type: application/x-http-form-urlencoded\r\n\r\n";
        out.write((header).getBytes());
        out.write((POSTdata+"\r\n\r\n\n").getBytes());
        int i;
        String res="";
        byte buff[]=new byte[100];
        while((i=in.read(buff))>0)
            res+=new String(buff,0,i);
        return res;
    }
	
	@Override
	public void run()
	{
			String res="";
			URL url;
		    HttpURLConnection connection = null;  
		    try {
		      //Create connection
		      url = new URL(this.url);
		      connection = (HttpURLConnection)url.openConnection();
		      if(POSTdata.length()<=0)
		    	  	connection.setRequestMethod("GET");
		      else
		      {
		    	  	res= HTTPPost(this.url,POSTdata);
		    	  	if(callback!=null) //thread still alive?
			    	  	callback.URLCallBack(res);
		    	  	return;
//		    	  	connection.setRequestMethod("POST");
//		    	  	connection.setRequestProperty("Content-Type", 
//		           "application/x-www-form-urlencoded");
//					
//		    	  	connection.setRequestProperty("Content-Length", "" + 
//		               Integer.toString(POSTdata.getBytes().length)); 
//		    	  	
//		    	  	connection.setUseCaches (false);
//				connection.setDoInput(true);
//				      
//			      connection.setDoOutput(true);
//
////			      Send request
//			      DataOutputStream wr = new DataOutputStream (
//			                  connection.getOutputStream ());
//			      wr.writeBytes(POSTdata);
//			      wr.flush ();
//			      wr.close ();				      
		    	  }

		      

		      //Get Response	
		      InputStream is = connection.getInputStream();
		      byte buff[]=new byte[500];
		      int i;
		      while((i=is.read(buff))>-1)
		    	  		res+=new String(buff,0,i);
		      is.close();
		      
		      if(callback!=null) //still alive?
		    	  	callback.URLCallBack(res);
		      
		    } catch (Exception e) {
		    	// Counter for Bad Requests (Google Analytics)
//		    	_ea.tracker.trackPageView("/BadRequest");
		      e.printStackTrace();
		      System.out.println("Error:"+e);
		      callback.URLCallBack("Error:"+e.getMessage());
		    } finally {
		      if(connection != null) {
		        connection.disconnect(); 
		      }
		    }
	    
	}
}