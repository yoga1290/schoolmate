package yoga1290.schoolmate;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.sax.TextElementListener;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class RecorderView extends Fragment implements OnAudioFocusChangeListener,android.view.View.OnClickListener
{
	
	//AudioRecord code from http://code.google.com/p/krvarma-android-samples/source/browse/trunk/AudioRecorder.2/src/com/varma/samples/audiorecorder/RecorderActivity.java
	
	private static final int RECORDER_BPP = 16;
  private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
  private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
  private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
  private AudioRecord recorder = null;
//  private int bufferSize = 0;
  private Thread recordingThread = null;
  private boolean isRecording = false;
	
	
	View v;
	AudioManager am;
	Button button_ok,stopbutton;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) 
    {
        v=inflater.inflate(R.layout.view_record, container, false);
        
        am = (AudioManager) this.getActivity().getSystemService(Context.AUDIO_SERVICE);
        int result	=	am.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        
        button_ok=(Button) v.findViewById(R.id.audioPeerOk);
        button_ok.setOnClickListener(this);
        
        stopbutton=(Button) v.findViewById(R.id.stopbutton);
        stopbutton.setOnClickListener(this);
//        bufferSize = AudioRecord.getMinBufferSize(AudioProperties.sampleRateInHz,AudioProperties.channelConfig,AudioProperties.audioFormat);
        
        return v;
    }

	@Override
	public void onAudioFocusChange(int arg0)
	{
		//TODO
//		if(isRecording)stopRecording();
		
	}
	
	private void debug(String txt)
	{
//		try
//		{
//			EditText et=(EditText) v.findViewById(R.id.audioPeer);
//			et.setText(txt);
//		}catch(Exception e){}
	}
	
	
	
	
	
	private void startRecording(){
		try{
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                        AudioProperties.sampleRateInHz, AudioProperties.channelConfigIN,AudioProperties.audioFormat, AudioProperties.bufferSizeIN);
        
        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
                
                @Override
                public void run() {
                	System.out.println("start talk");
                			debug("Start talking");
                        writeAudioDataToFile();
                }
        },"AudioRecorder Thread");
        
        recordingThread.start();
		}catch(Exception e){e.printStackTrace();}
}

private void writeAudioDataToFile(){
//		if(bufferSize==0)
//			bufferSize=AudioRecord.getMinBufferSize(AudioProperties.sampleRateInHz,AudioProperties.channelConfig,AudioProperties.audioFormat);
        byte data[] = new byte[AudioProperties.bufferSizeIN];
//        String filename = getTempFilename();
//        FileOutputStream os = null;
        
//        try {
//                os = new FileOutputStream(filename);
//        } catch (Exception e) {
//        			debug(1+">"+e);
//                e.printStackTrace();
//        }
        int read = 0;
//        if(null != os)
        //{
                while(isRecording){
                        read = recorder.read(data, 0, AudioProperties.bufferSizeIN);
                        
                       
                        //Share Audio to all followers
//                        System.out.println("Sharing audioÉ");
                        
                        
                        if(AudioRecord.ERROR_INVALID_OPERATION != read){
                        		ServerData.send2Followers(data,read);
//                        	ServerData.broadcast(data,read, 1292);
                        		
//                                try {
//                                	
////                                        os.write(data);
//                                        os.write(data, 0, read);
//                                } catch (Exception e) {
//                                	debug(2+">"+e);
//                                        e.printStackTrace();
//                                }
                        }
                }                
//                try {
//                        os.close();
//                } catch (Exception e) {
//                	debug(3+">"+e);
//                        e.printStackTrace();
//                }
       // }
}
private void stopRecording()
{
//	AudioTrack at=new AudioTrack(streamType, AudioProperties.sampleRateInHz, AudioProperties.channelConfig, AudioProperties.audioFormat, bufferSize, mode);
//			(MediaRecorder.AudioSource.MIC,
//            AudioProperties.sampleRateInHz, AudioProperties.channelConfig,AudioProperties.audioFormat, bufferSize);
    if(null != recorder)
    {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
    	}
    
//    copyWaveFile(getTempFilename(),getFilename());
//    private void copyWaveFile(String inFilename,String outFilename){
    /*
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = AudioProperties.sampleRateInHz;
        int channels = 2;
        long byteRate = RECORDER_BPP * AudioProperties.sampleRateInHz * channels/8;
        
        byte[] data = new byte[bufferSize];
        
        try {
                in = new FileInputStream(getTempFilename());
                out = new FileOutputStream(getFilename());
                totalAudioLen = in.getChannel().size();
                totalDataLen = totalAudioLen + 36;
                
                WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                                longSampleRate, channels, byteRate);
                
                while(in.read(data) != -1){
                        out.write(data);
                }
                
                in.close();
                out.close();
        } catch (Exception e) {
        	debug(4+">"+e);
                e.printStackTrace();
        }
    */
    
//    File file = new File(getTempFilename());
//    file.delete();
}

private void WriteWaveFileHeader(
        FileOutputStream out, long totalAudioLen,
        long totalDataLen, long longSampleRate, int channels,
        long byteRate) throws Exception
        {
			byte[] header = new byte[44];	
			header[0] = 'R';  // RIFF/WAVE header
			header[1] = 'I';
			header[2] = 'F';
			header[3] = 'F';
			header[4] = (byte) (totalDataLen & 0xff);
			header[5] = (byte) ((totalDataLen >> 8) & 0xff);
			header[6] = (byte) ((totalDataLen >> 16) & 0xff);
			header[7] = (byte) ((totalDataLen >> 24) & 0xff);
			header[8] = 'W';
			header[9] = 'A';
			header[10] = 'V';
			header[11] = 'E';
			header[12] = 'f';  // 'fmt ' chunk
			header[13] = 'm';
			header[14] = 't';
			header[15] = ' ';
			header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
			header[17] = 0;
			header[18] = 0;
			header[19] = 0;
			header[20] = 1;  // format = 1
			header[21] = 0;
			header[22] = (byte) channels;
			header[23] = 0;
			header[24] = (byte) (longSampleRate & 0xff);
			header[25] = (byte) ((longSampleRate >> 8) & 0xff);
			header[26] = (byte) ((longSampleRate >> 16) & 0xff);
			header[27] = (byte) ((longSampleRate >> 24) & 0xff);
			header[28] = (byte) (byteRate & 0xff);
			header[29] = (byte) ((byteRate >> 8) & 0xff);
			header[30] = (byte) ((byteRate >> 16) & 0xff);
			header[31] = (byte) ((byteRate >> 24) & 0xff);
			header[32] = (byte) (2 * 16 / 8);  // block align
			header[33] = 0;
			header[34] = RECORDER_BPP;  // bits per sample
			header[35] = 0;
			header[36] = 'd';
			header[37] = 'a';
			header[38] = 't';
			header[39] = 'a';
			header[40] = (byte) (totalAudioLen & 0xff);
			header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
			header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
			header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
			
			out.write(header, 0, 44);
}

private String getFilename(){
    String filepath = Environment.getExternalStorageDirectory().getPath();
    File file = new File(filepath,AUDIO_RECORDER_FOLDER);
    if(!file.exists())	file.mkdirs();
   return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_WAV);
}
private String getTempFilename(){
    String filepath = Environment.getExternalStorageDirectory().getPath();
    File file = new File(filepath,AUDIO_RECORDER_FOLDER);
    if(!file.exists())	file.mkdirs();
    File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);
    if(tempFile.exists())	tempFile.delete();
    return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
}

@Override
public void onClick(View v) {
	if(v.getId()==button_ok.getId())
	{
		try{
			String masterIP=((EditText) this.v.findViewById(R.id.audioPeer)).getText().toString();
			if(masterIP.length()<=0)
			{
				if(recordingThread==null)
				{
					startRecording();
				}
				return;
			}
			
			
			final byte serverIP[]=new byte[4];
			int o,p=0;
			while((o=masterIP.indexOf("."))>-1)
			{
				System.out.println(masterIP.substring(0,o));
				serverIP[p++]=(byte) Integer.parseInt(masterIP.substring(0,o));
				masterIP=masterIP.substring(o+1);
			}
			serverIP[p++]=(byte) Integer.parseInt(masterIP);
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try
					{
						System.out.println("AddMe on "+InetAddress.getByAddress(serverIP));
						Socket s=new Socket(InetAddress.getByAddress(serverIP) , ServerProperties.port);
						PrintWriter out=new PrintWriter(s.getOutputStream());
						out.println("AddMe");
						out.close();
						s.close();
						
					}catch(Exception e){e.printStackTrace();}
				}
			}).start();
		}catch(Exception e){e.printStackTrace();}
		
	}
	else
	{
		stopRecording();
//		try
//		{
//			File tmpfile=new File(getTempFilename());
//			FileInputStream in=new FileInputStream(tmpfile);
//			byte buff[]=new byte[bufferSize];
//			// Create a new AudioTrack object using the same parameters as the AudioRecord
//			// object used to create the file.
//			AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 
//			AudioProperties.sampleRateInHz,//11025, 
//			AudioProperties.channelConfig,//AudioFormat.CHANNEL_CONFIGURATION_MONO,
//			AudioProperties.audioFormat,//AudioFormat.ENCODING_PCM_16BIT, 
//			bufferSize,// 
//			AudioTrack.MODE_STREAM);
//			// Start playback
//			audioTrack.play();
//			int offset=-1;
//	
//			while((offset=in.read(buff))>0)
//				// Write the music buffer to the AudioTrack object
//				audioTrack.write(buff, 0, offset);
//			audioTrack.stop();
//			in.close();
//		}catch(Exception e){e.printStackTrace();}

	}
}
}

