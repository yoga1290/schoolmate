# Handling URLConnections vs UI-Thread

URLConnections are carried on separate threads and UI updates are packed in a new thread that will be queued and "runOnUiThread":

     final Activity currentActivity=this;
     new [URLThread](src/yoga1290/schoolmate/URLThread.java) ("URL HERE", new URLThread_CallBack() 
     { 
     	@Override 
     	public void [URLCallBack](src/yoga1290/schoolmate/URLThread.java) (String response) 
     	{ 
    		//queue this back on in the UI 
    		currentActivity.runOnUiThread
    		( 
    			new Runnable() 
    			{ 
    				@Override 
    				public void run() 
    				{ 
    					findViewById(R.id.SOME_UI_Comp) 
    						.doSomething(); 
    				} 
    			} 
    		); 
    	}
    }, "Optional POST DATA HERE,otherwise GET is used").start();

# [Connect](src/yoga1290/schoolmate/Connect.java)

+	Locally stored file, used for storing user data for later use and transfer data between Activities
+	Implementation: getData():JSONObject & setData(JSONObject):void in [Connect](src/yoga1290/schoolmate/Connect.java)

# [In-App Server](src/yoga1290/schoolmate/Server.java)

In-App Socket Server that handles couple of things:

+	If OAuth permission dialog was accepted, Service provider will make callback to the Web server (my AppEngine) handling the access token, the web server will later on pass the access token to the in-App Server [localhost:1290?ServiceName_HERE?access_token=TOKEN_HERE](src/yoga1290/schoolmate/Server.java#L605) where the user access token will be extracted.
![Authorizing](readme/readme1.png)

+	Later, the access token is used to establish a URLConnection to retrieve the users data (e.g: Facebook/Foursqaure ID)… but KEYS from their JSON responses may clash with others in the [Connect.json](src/yoga1290/schoolmate/Connect.java)… so, before storing any data, there will be URLConnection CallBack Handlers to avoid JSON Keys collision
![Avoiding JSON Key Collision](readme/URLConnectionThread.png)

+	Avoiding duplicated messages

![Avoiding duplication](readme/duplication.png)

# Charts

![Visulazation](readme/readme3.png)

+	[getClassTimetable](src/yoga1290/schoolmate/Charts.java#L55)
+	[getGPAGraph](src/yoga1290/schoolmate/Charts.java#L104)

# UI

![Activities](readme/activities.png)

+	[**Connect/MainActivity**](src/yoga1290/schoolmate/MainActivity.java) , It should be starting as the very 1st view and whenever there's a need for an access token
+	[**ProfileActivity**](src/yoga1290/schoolmate/ProfileActivity.java) , starts after the Connect Activity and on profile picture click in the Class Stream view

# WebServer

+	[Google AppEngine & NoSQL datastore](AppEngine/schoolmate.java)
+	[Server + Oracle SQL](WebServer/DBtest.java)

+	GET method for getters & POST method w id/pin 
Example of requests/responses for getters & setters:
+	GET [/schoolmate/student?id=…&pin=…](http://yoga1290.appspot.com/schoolmate/student?id=1&pin=1)
>	{
>		"classes":"1,2,3,…",
>		"fbid":"[870205250](http://facebook.com/870205250)"
>	}
>>	classes: IDs of joined classes
>>	fbid: Facebook id; used to send notifications

+	GET /schoolmate/staff?id=…&pin=…

+	GET [/schoolmate/class?id=…](http://yoga1290.appspot.com/schoolmate/class?id=1)
>	{
>		"schedule":"39,100,25,94,110,74,…",
>		"students":"1,2,3,…"
>	}
>>	students: IDs of students in this class
>>	used for generating [charts](src/yoga1290/schoolmate/Charts.java) where (schedule) is a  6-element array representing the 6 daily periods, each integer is a binary mask in form of 0bSMTWtFs (Sunday,Monday,Tuesday,Wednesday,thursday,Friday & saturday in order)

+ POST /schoolmate/student?id=…&pin=…
+ POST /schoolmate/staff?id=…&pin=…
+ POST /schoolmate/class?id=…