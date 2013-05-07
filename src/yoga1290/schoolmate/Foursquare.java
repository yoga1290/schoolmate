package yoga1290.schoolmate;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
public class Foursquare 
{
	/**
	 * @see 	https://developer.foursquare.com/overview/auth
	 * @param CLIENT_ID
	 * @param CLIENT_SECRET
	 * @param code
	 * @return access_token
	 * @throws Exception
	 */
	public static String getAccessToken(String CLIENT_ID,String CLIENT_SECRET,String code) throws Exception
	{
		String res="";
		/*
		https://foursquare.com/oauth2/authenticate
		    ?client_id=YOUR_CLIENT_ID
		    	    &response_type=code
		    	    &redirect_uri=YOUR_REGISTERED_REDIRECT_URI
	    	*/
			URL url = new URL("https://foursquare.com/oauth2/access_token?client_id="+CLIENT_ID
					+"&client_secret="+CLIENT_SECRET
					+"&grant_type=authorization_code"
					+"&redirect_uri=http://yoga1290.appspot.com/oauth/foursquare/callback?state=test"
					+"&code="+code);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream in=connection.getInputStream();
            byte buff[]=new byte[in.available()];
            int ch;
            while((ch=in.read(buff))!=-1)
            		res+=new String(buff,0,ch);
            in.close();
            
            //Extract the access token
            return new JSONObject(res).getString("access_token");
	}
	
	
	/**
	 * @see https://developer.foursquare.com/docs/venues/search
	 * @param access_token
	 * @param Latitude
	 * @param longitude
	 * @return
	 * @throws Exception
	 * response: {
					venues: [
						{
							id: "503de4dce4b0857b003af5f7"
							name: "monkeyHut"
							contact: { }
							location: {
							lat: 40.7
							lng: -74
							distance: 0
							postalCode: "10004"
							city: "New York"
							state: "NY"
							country: "United States"
							cc: "US"
						}
						canonicalUrl: "https://foursquare.com/v/monkeyhut/503de4dce4b0857b003af5f7"
						categories: [
							{
								id: "4bf58dd8d48988d1e1931735"
								name: "Arcade"
								pluralName: "Arcades"
								shortName: "Arcade"
								icon: {
								prefix: "https://foursquare.com/img/categories_v2/arts_entertainment/arcade_"
								suffix: ".png"
							}
							primary: true
							}
						]
						verified: false
						stats: {
							checkinsCount: 2
							usersCount: 2
							tipCount: 2
						}
						likes: {
							count: 0
							groups: [ ]
						}
						specials: {
							count: 0
							items: [ ]
						}
						hereNow: {
							count: 0
							groups: [ ]
						}
					referralId: "v-1356999154"
					},É]}

	 */
	public static JSONObject search(String access_token,double Latitude ,double longitude) throws Exception
	{
		String res="";
		URL url = new URL("https://api.foursquare.com/v2/venues/search?ll="+Latitude+","+longitude+"&oauth_token="+access_token);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream in=connection.getInputStream();
        byte buff[]=new byte[in.available()];
        int ch;
        while((ch=in.read(buff))!=-1)
        		res+=new String(buff,0,ch);
        in.close();
        return new JSONObject(res);
	}
	
	
	/**
	 * @see	https://developer.foursquare.com/docs/checkins/add
	 * @param access_token
	 * @param venueId
	 * @return
	 * @throws Exception
	 */
	public static JSONObject checkin(String access_token,String venueId) throws Exception
	{
		String res="";
		URL url = new URL("https://api.foursquare.com/v2/checkins/add?venueId="+venueId+"&oauth_token="+access_token);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream in=connection.getInputStream();
        byte buff[]=new byte[in.available()];
        int ch;
        while((ch=in.read(buff))!=-1)
        		res+=new String(buff,0,ch);
        in.close();
        return new JSONObject(res);
	}
}
