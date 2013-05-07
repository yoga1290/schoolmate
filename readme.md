# Authorizing:

![Authorizing](readme1.png)
At any point in the App where the Access Token is needed and couldn't be retrieved through the static getData() function in [Connect](https://github.com/yoga1290/schoolmate/blob/master/src/yoga1290/schoolmate/Connect.java) , a new [ConnectActivity](https://github.com/yoga1290/schoolmate/blob/master/src/yoga1290/schoolmate/ConnectActivity.java) Activity is started asking user to pick a service for authorization. 

Data are locally stored in form of JSON in the connect.txt (..not in the local DB,for now,for some debugging reasons)
…Actually, this is cute as most of social networks responses are in JSON w few fields collisions here so I'm storing whatever comes to hand & ask for [ConnectActivity](https://github.com/yoga1290/schoolmate/blob/master/src/yoga1290/schoolmate/ConnectActivity.java) is smth is missing! :)

The in-App [Server](https://github.com/yoga1290/schoolmate/blob/master/src/yoga1290/schoolmate/Server.java) will be notified when the native browser is redirected to "http://localhost:1290/SOME_SERVICES?access_token=TOKEN_TO_BE_STORED"..as explained in the chart


# Audio/Chat protocol
![chat](readme2.png)

Actually, there are still some problems here… 1st time I used to loop for an available data port to start a new Thread that plays the received AudioTrack bytes, it gets noisy over time (at least you can hear the audio in my old [blog post](http://yoga1290.blogspot.com/2013/02/rocking-trip-sharing-audiotracks-across.html) ) so, I thought to prevent the overlapping noise by providing a unique data port per peer (IP as key)…but, this made it even worse :D!..while a peer is sending new data, it crushed w old thread using the same port for playing audio & none of new & old thread will continue working… (may be,I should rollback if I can't find better solution!)

# Data Visualization
![Visualization](readme3.png)

These charts are generated through static functions in [Charts](https://github.com/yoga1290/schoolmate/blob/master/src/yoga1290/schoolmate/Charts.java) where you can generate it anytime, any place & in any view!