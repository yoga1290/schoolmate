import java.applet.Applet;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import  org.json.*;
import oracle.jdbc.*;
import oracle.jdbc.driver.*;
import oracle.jdbc.driver.OracleDriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

class jfr implements ActionListener,Runnable
{
    public JFrame mfr;
    public JPanel jp1;
    public JTextField port;
    public JTextField pw;
    public JButton ok,exit;
    private Thread runner;
    public jfr()
    {
       
            mfr = new JFrame("WebServer");
            mfr.setSize(300, 300);
            mfr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ok=new JButton("OK");
            ok.addActionListener(this);

            mfr.add(ok);
            mfr.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==ok)
        {
            if(runner==null)
            {
                runner=new Thread(this);
                runner.start();
            }
        }
        else
            System.exit(0);
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void run() {
        try{
                //Integer.parseInt(port.getText())
            ServerSocket ss = new ServerSocket(80);
            while(true){
               // display("listening again ** port: "+port+"\n");
                Socket s = ss.accept();
                new Thread(new FileRequest(s)).start();
                //display("got a file request \n");
            }
            }catch(Exception ex){System.err.println(ex);}
//        throw new UnsupportedOperationException("Not supported yet.");
    }
}
class FileRequest implements Runnable{
    public Robot rob;
    public boolean send=true;

    FileRequest(Socket s){
     //   this.app=app;
        client = s;
    } //*/
    public void run(){

        if(requestRead()){
            if(fileOpened()){
                constructHeader();
                if(fileSent()){
// app.display("*File: "+fileName+" File Transfer Complete*Bytes Sent:"+bytesSent+"\n");
                }
            }
        }
          try{
            dis.close();
            client.close();
        }catch(Exception e){}

    }


    private boolean fileSent()
    {
        try{
DataOutputStream clientStream = new DataOutputStream
(new BufferedOutputStream(client.getOutputStream()));
            clientStream.writeBytes(header);
      //      app.display("******** File Request *********\n"+
          //              "******* "+ fileName +"*********\n"+header);
            int i;
            bytesSent = 0;
            while((i=requestedFile.read()) != -1){
                clientStream.writeByte(i);
                bytesSent++;
            }
            clientStream.flush();
            clientStream.close();
                 }catch(IOException e){return false;}
                 return true;

    }
    private boolean fileOpened()
    {
//        ByteInputStream bis=new ByteInputStream("".getBytes(), 0);
  //      DataInputStream dis=new DataInputStream(bis);
//        if(send)
//        {
//        try{
//requestedFile = new DataInputStream(new BufferedInputStream
//((this.getClass().getResourceAsStream("/"+fileName))));
//                fileLength = requestedFile.available();
//     //           app.display(fileName+" is "+fileLength+" bytes long.\n");
//            }catch(FileNotFoundException e){
//                if(fileName.equals("filenfound.html")){return false;}
//                fileName="filenfound.html";
//                if(!fileOpened()){return false;}
//            }catch(Exception e){return false;}
//
//        }
//        else
//        {

            try{
            	
            	//connecting to Database:
		try {
 
			Class.forName("oracle.jdbc.driver.OracleDriver");
 
		} catch (ClassNotFoundException e) {
 
			System.out.println("Where is your Oracle JDBC Driver?");
			e.printStackTrace();
			return false;
 
		}
 
		System.out.println("Oracle JDBC Driver Registered!");
 
		Connection connection = null;
 
		try {
 
			connection = DriverManager.getConnection(
					"jdbc:oracle:thin:@localhost:1521:CRM", "CRM",
					"Aymansabra8102032$");
 
		} catch (Exception e) {
 
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return false;
 
		}
 
		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}            	
            	
            	
            	
            	/*
            	 @see http://docs.oracle.com/javase/1.3/docs/guide/jdbc/getstart/resultset.html#998035
            	 */
            	String data="nothing";
            	//TODO SQL Query response HERE; in this part
            	String id=fileName.substring(fileName.indexOf("id=")+3,fileName.length());
            	fileName=fileName.substring(0,fileName.indexOf("?"));
            	if(fileName.equals("student"))
            	{
            		Statement stmt = connection.createStatement();
 					ResultSet rs = stmt.executeQuery("SELECT * FROM student where student.studentid='"+id+"'");
 					JSONObject response=new JSONObject();
 					
 					 ResultSetMetaData rsMetaData = rs.getMetaData();
				    int numberOfColumns = rsMetaData.getColumnCount();

 					while (rs.next()) 
 					{
 						
 						//response.put("name",rs.getString("studentname"));
						for (int i = 1; i < numberOfColumns + 1; i++)
      						response.put( rsMetaData.getColumnName(i) , rs.getString(rsMetaData.getColumnName(i)) );
							// retrieve and print the values for the current row
							//int i = rs.getInt("a");
							//String s = rs.getString("b");
							//float f = rs.getFloat("c");
							//System.out.println("ROW = " + i + " " + s + " " + f);
					}
					data=response.toString();
            	}
            requestedFile = new DataInputStream(new ByteArrayInputStream(data.getBytes()));
                fileLength = requestedFile.available();
                
            }catch(Exception e){e.printStackTrace();return false;}
//        }
                        return true;

    }
    private boolean requestRead()
    {
         try{
            //Open inputStream and read(parse) the request
            dis = new DataInputStream(client.getInputStream());
            String line;
            send=true;
            while((line=dis.readLine())!=null){

                StringTokenizer tokenizer = new StringTokenizer(line," ");
                if(!tokenizer.hasMoreTokens()){ break;}

                        if(tokenizer.nextToken().equals("GET")){

                            fileName = tokenizer.nextToken();
                            if(fileName.equals("/")){ //will not be used
                                fileName = "index.html";
                            }else{
                                fileName = fileName.substring(1);// exclude the "/" (e.g: /student?id=)
                                
                            }

                        }

            }

         }catch(Exception e){

            return false;
         }
     //      app.display("finished file request");

         return true;
    }


    private void constructHeader(){
        String contentType;
        if((fileName.toLowerCase().endsWith(".jpg"))||(fileName.toLowerCase().endsWith(".jpeg"))
||(fileName.toLowerCase().endsWith(".jpe"))){contentType = "image/jpg";}
        else if((fileName.toLowerCase().endsWith(".gif"))){contentType = "image/gif";}
        else if((fileName.toLowerCase().endsWith(".htm"))||
                (fileName.toLowerCase().endsWith(".html"))){contentType = "text/html";}
        else if((fileName.toLowerCase().endsWith(".qt"))||
                (fileName.toLowerCase().endsWith(".mov"))){contentType = "video/quicktime";}
        else if((fileName.toLowerCase().endsWith(".class"))){contentType = "application/octet-stream";}
        else if((fileName.toLowerCase().endsWith(".mpg"))||
(fileName.toLowerCase().endsWith(".mpeg"))||(fileName.toLowerCase().endsWith(".mpe")))
{contentType = "video/mpeg";}
        else if((fileName.toLowerCase().endsWith(".au"))||(fileName.toLowerCase().endsWith(".snd")))
            {contentType = "audio/basic";}
        else if((fileName.toLowerCase().endsWith(".wav")))
            {contentType = "audio/x-wave";}
        else {contentType = "text/plain";} //default

        header = "HTTP/1.0 200 OK\n"+
                 "Allow: GET\n"+
                 "MIME-Version: 1.0\n"+
                 "Server : HMJ Basic HTTP Server\n"+
                 "Content-Type: "+contentType + "\n"+
                 "Content-Length: "+ fileLength +
                 "\n\n";
    }

  //  private Serve app;
    private Socket client;
    private String fileName,header;
    private DataInputStream requestedFile, dis;
        private int fileLength, bytesSent;

}
public class DBtest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new jfr();

        // TODO code application logic here
    }

}