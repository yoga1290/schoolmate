import java.io.InputStream;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PropertyContainer;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class schoolmate 
{
	public static void new4SqUser(String access_token) throws Exception
	{
			JSONObject userInfo=Foursquare.getUserInfo(access_token).getJSONObject("response").getJSONObject("user");
			String fsqid=userInfo.getString("id");
			
			Entity member=null;
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			try{
				member=datastore.get(KeyFactory.createKey("4sqr", fsqid));
			}catch(Exception e){
				member=new Entity("4sqr", fsqid);
				member.setProperty("access_token", access_token);
				datastore.put(member);
			}
	}
	public static void doGet(HttpServletRequest req, HttpServletResponse resp)
	{
		try{
		/* 
https://foursquare.com/oauth2/authenticate?client_id=BZ4QPVWSF213QA2ICE1QSHIGDMCNZBW20QD3EXBVH0OHG3IT&response_type=code&redirect_uri=http://yoga1290.appspot.com/schoolmate/oauth/foursquare/callback/
		    	    */
			if(req.getRequestURI().equals("/schoolmate/oauth/foursquare/callback/"))
			{
				String access_token=Foursquare.getAccessToken("BZ4QPVWSF213QA2ICE1QSHIGDMCNZBW20QD3EXBVH0OHG3IT", "***","http://yoga1290.appspot.com/schoolmate/oauth/foursquare/callback/", req.getParameter("code"));
				resp.getWriter().println("saving your access token,");
				new4SqUser(access_token);
				resp.getWriter().println("Please wait...<script>top.location.href='http://localhost:1290/foursquare?access_token="+access_token+"';</script>");
			}
		//https://www.facebook.com/dialog/oauth?client_id=122683301250532&redirect_uri=http://yoga1290.appspot.com/schoolmate/oauth/facebook/callback/&scope=user_about_me,email,user_education_history&state=
			else if(req.getRequestURI().equals("/schoolmate/oauth/facebook/callback/"))
			{
				String access_token=facebook.getAccessToken("122683301250532", "***","http://yoga1290.appspot.com/schoolmate/oauth/facebook/callback/", req.getParameter("code"));
				resp.getWriter().println("saving your access token");
				resp.getWriter().println("Please wait...<script>top.location.href='http://localhost:1290/facebook?access_token="+access_token+"';</script>");
			}
			
			//GET-ers:
			else if(req.getRequestURI().equals("/schoolmate/student"))
			{
				String studentId=req.getParameter("id");
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				resp.getWriter().println(
						new JSONObject(
								datastore.get(KeyFactory.createKey("schoolmate_student", studentId)).getProperties()
						).toString());
			}
			else if(req.getRequestURI().equals("/schoolmate/staff"))
			{
				String staffId=req.getParameter("id");
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				resp.getWriter().println(
						new JSONObject(
								datastore.get(KeyFactory.createKey("schoolmate_staff", staffId)).getProperties()
						).toString());
			}
			else if(req.getRequestURI().equals("/schoolmate/class"))
			{
				String classId=req.getParameter("id");
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				resp.getWriter().println(
						new JSONObject(
								datastore.get(KeyFactory.createKey("schoolmate_class", classId)).getProperties()
						).toString());
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			try{resp.getWriter().println("exception="+e.getMessage());}catch(Exception e2){}
		}
	}
	
	
	public static void doPost(HttpServletRequest req, HttpServletResponse resp)
	{
		try{
			InputStream in=req.getInputStream();
			byte buff[]=new byte[200];
			int o;
			//SET-ers:
			if(req.getRequestURI().equals("/schoolmate/student"))
			{
				String studentId=req.getParameter("id");
				String res="",k;
				while((o=in.read(buff))>0)
					res+=new String(buff, 0,o);
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				Entity student=new Entity(KeyFactory.createKey("schoolmate_student", studentId));
				JSONObject json=new JSONObject(res);
				Iterator<String> it=json.keys();
				
				while(it.hasNext())
				{
					k=it.next();
					student.setProperty(k, json.get(k));
				}
				datastore.put(student);
			}
			else if(req.getRequestURI().equals("/schoolmate/staff"))
			{
				String staffId=req.getParameter("id");
				String res="",k;
				while((o=in.read(buff))>0)
					res+=new String(buff, 0,o);
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				Entity staff=new Entity(KeyFactory.createKey("schoolmate_staff", staffId));
				JSONObject json=new JSONObject(res);
				Iterator<String> it=json.keys();
				
				while(it.hasNext())
				{
					k=it.next();
					staff.setProperty(k, json.get(k));
				}
				datastore.put(staff);
			}
			else if(req.getRequestURI().equals("/schoolmate/class"))
			{
				String classId=req.getParameter("id");
				String res="",k;
				while((o=in.read(buff))>0)
					res+=new String(buff, 0,o);
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				Entity classs=new Entity(KeyFactory.createKey("schoolmate_class", classId));
				JSONObject json=new JSONObject(res);
				Iterator<String> it=json.keys();
				
				while(it.hasNext())
				{
					k=it.next();
					classs.setProperty(k, json.get(k));
				}
				datastore.put(classs);
			}
			resp.getWriter().println("done ;)");
		}catch(Exception e){
			try{resp.getWriter().println("exception="+e.getMessage());}catch(Exception e2){}
		}
	}
}
