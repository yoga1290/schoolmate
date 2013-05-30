package yoga1290.schoolmate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.TreeMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

public class Connect
{
	public static String KEY_STUDENTID="studentId";
	public static boolean isConnected()
	{
		//TODO check aast id
		try{
			File file=new File(Environment.getExternalStorageDirectory().getPath()+"/connect.txt");
			return file.exists();
		}catch(Exception e){return false;}
	}
	public static String getAccessTokenFor(String service) throws Exception
	{
			return getData().getString(service);
//			FileInputStream in=new FileInputStream(Environment.getExternalStorageDirectory().getPath()+"/connect.txt");
//			byte buff[]=new byte[200];
//			int o;
//			String res="";
//			while((o=in.read(buff))>0)
//				res+=new String(buff, 0, o);
//			return new JSONObject(res).getString(service);
	}
	public static String OAuthFacebookURI="https://www.facebook.com/dialog/oauth?client_id=122683301250532&redirect_uri=http://yoga1290.appspot.com/schoolmate/oauth/facebook/callback/&scope=user_about_me,email,user_education_history&state=signup";
	public static String OAuthFoursquareURI="https://foursquare.com/oauth2/authenticate?client_id=BZ4QPVWSF213QA2ICE1QSHIGDMCNZBW20QD3EXBVH0OHG3IT&response_type=code&redirect_uri=http://yoga1290.appspot.com/schoolmate/oauth/foursquare/callback/";
	
	// get AppAccessToken from https://graph.facebook.com/oauth/access_token?client_id=122683301250532&client_secret=***&grant_type=client_credentials
	public static String OAuthFacebook_AppAccessToken="******";
	
	//Const:
	public static String facebook="facebook";
	public static String foursquare="foursquare";
	public static JSONObject getData()// throws Exception
	{
		try{
			String res="";
			JSONObject json=new JSONObject();
			File file=new File(Environment.getExternalStorageDirectory().getPath()+"/connect.txt");
			if(file.exists())
			{
				FileInputStream in=new FileInputStream(Environment.getExternalStorageDirectory().getPath()+"/connect.txt");
				byte buff[]=new byte[200];
				int o;
				while((o=in.read(buff))>0)
					res+=new String(buff, 0, o);
				json=new JSONObject(res);
				in.close();
			}
			return json;
		}catch(Exception e){e.printStackTrace();return new JSONObject();}
//		return new JSONObject();
	}
	public static void setData(JSONObject json) throws Exception
	{
		FileOutputStream out=new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/connect.txt");
		out.write(json.toString().getBytes());
		out.close();
	}
	public static void addService(String name,String access_token) throws Exception
	{
			setData(getData().put(name, access_token));
//			String res="";
//			JSONObject json=new JSONObject();
//			File file=new File(Environment.getExternalStorageDirectory().getPath()+"/connect.txt");
//			if(file.exists())
//			{
//				FileInputStream in=new FileInputStream(Environment.getExternalStorageDirectory().getPath()+"/connect.txt");
//				byte buff[]=new byte[200];
//				int o;
//				while((o=in.read(buff))>0)
//					res+=new String(buff, 0, o);
//				json=new JSONObject(res);
//				in.close();
//			}
//			
//			FileOutputStream out=new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/connect.txt");
//			json=json.put(name, access_token);
//			out.write(json.toString().getBytes());
//			out.close();
	}
	
}

class facebook implements URLThread_CallBack
{
	@Override
	public void URLCallBack(String response)
	{
		TreeMap<String,String> replaceKeyNameMap=new TreeMap<String,String>();
		replaceKeyNameMap.put("id","fbid");
		//TODO
		try{
			System.out.println("Facebook resp:\n"+response);
			JSONObject resp=new JSONObject(response);
			JSONObject res=Connect.getData();
			
			Iterator<String> it=resp.keys();
			String tmp;
			while(it.hasNext())
			{
				tmp=it.next();
				if(replaceKeyNameMap.containsKey(tmp))
					res.put(replaceKeyNameMap.get(tmp), resp.get(tmp));
				else
					res.put(tmp, resp.get(tmp));
			}
			Connect.setData(res);
			

			//try updating user's data on the server
				new URLThread("yoga1290.appspot.com/schoolmate/student?id="+res.getString("id")+"&pin="+res.getString("pin"), 
						new URLThread_CallBack() {
							@Override
							public void URLCallBack(String response)
							{
								System.out.println("Updating user's data:\n"+response);
							}
						},
						res.toString()).start();
			
			
		}catch(Exception e){e.printStackTrace();}
	}
}
