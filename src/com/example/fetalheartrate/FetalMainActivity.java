package com.example.fetalheartrate;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import phfi.fetal.fetal_doppler.GaussianFilter;
import phfi.fetal.fetal_doppler.Hilbert_Transform;
import FFT_Classes.DSP;
import android.app.Activity;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FetalMainActivity extends Activity implements OnClickListener{
public Button startRecord,stopRecord;
public final int sampleRateInHz = 8000;
@SuppressWarnings("deprecation")
public final int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
public final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
public boolean isRecording;
public double [] arrayToFill1 = new double[1024];
public double [] afterConvulation = new double[1024];
public double [] envelope1 = new double[1024];
public double [] arrayToPlay = new double[1024];
public double [] arrayToPlayNew = new double[1024];
public double [] realPartHilbert = new double[1024];
public double [] imgPartHilbert = new double[1024];
public double [] envelopeHilbertData = new double[1024];
public double [] crossCorelatedData = new double[2049];
public double [] newcrossCorelatedData = new double[2049];
public double [] peakmanifestArray = new double[2049];
public double [] peakmanifestArray1 = new double[2049];
public double [] modifiedCorrelated = new double[2049];
public double [] arrayForBpm = new double[100];
public int [] slopearray=new int[1000];
public float bpm,bpmNew; 
public RecorderAsync recordAudio;
public AudioRecord audioRecord ;
public float [] bpmFinal = new float[60];
public double slope=0;
public double slopemax=0;
public double max =0.0; 
public double min =0.0;
public double [] diff = new double[300];
public double [] arrayInd = new double[300];
public double diff1;
public double mindiff =0.0;
public int count=0;
public float x=0;
public double y=0;
public TextView displayText;
public int counter=1;
public int entranceVar=0;
public int index=0;
public MediaRecorder recorder;
public String dateNew;
public Date date;
public List<Float> values;
public AudioBroadcast audioBroadcast;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startRecord= (Button) findViewById(R.id.recordaudio);
		startRecord.setOnClickListener(this);
		displayText = (TextView) findViewById(R.id.textView1);
		values =new ArrayList<Float>();
		recorder = new MediaRecorder();
		date =  new Date();
		dateNew =   Long.toString(date.getTime());
		audioBroadcast = new AudioBroadcast();
		
		registerReceiver(audioBroadcast, new IntentFilter("android.intent.action.HEADSET_PLUG"));
		recordAndStore();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public class RecorderAsync extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params) 
		{
			// TODO Auto-generated method stub
			//change it afterwards boolean variable 
	    	isRecording = true;
	    	int bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioEncoding);
	    	Log.d("buffer size", String.valueOf(bufferSize));
	    	
	    	audioRecord  = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
	   			sampleRateInHz, channelConfig, audioEncoding, bufferSize*2);
	    	short[] audioBuffer = new short[bufferSize];
	    	
	    	audioRecord.startRecording();
	      	while(audioRecord.getState()!=AudioRecord.STATE_UNINITIALIZED)
	      	{
	      		  audioRecord.read(audioBuffer,0,audioBuffer.length);
	      		  int i=7,j=0;
	      		  do
	      		  {	      		
	      			 arrayToFill1[j]=audioBuffer[i];
	      			 i=i+8;
	      			 j++;
	      		  }while(j<arrayToFill1.length);
	      		 
         	      GaussianFilter gfilt1 = new GaussianFilter();  
         	      afterConvulation=gfilt1.GaussianFilterMethod(arrayToFill1);
         	      arrayToPlay= DSP.conv(arrayToFill1, afterConvulation); 
         	  
         	      realPartHilbert=Hilbert_Transform.transform(arrayToPlay, 1024).real;         	    
         	      imgPartHilbert=Hilbert_Transform.transform(arrayToPlay, 1024).imag;
         	      
         	      for(int hilbert=0;hilbert<1024;hilbert++)
         	      {
         	    	  envelopeHilbertData[hilbert]=Math.abs(Math.sqrt(((realPartHilbert[hilbert]*realPartHilbert[hilbert])+
         	    			(imgPartHilbert[hilbert]*imgPartHilbert[hilbert]))));
         	    	
         	      }

         	      for(int a=0;a<1024;a++)
         	      {
         	    	  envelope1[a]=(short) envelopeHilbertData[a];
         	      }
 	              
	      		crossCorelatedData =DSP.xcorr(arrayToPlay,arrayToPlay);	      	    
	      		
	
	      		for(int m=0;m<crossCorelatedData.length;m++)
	      		{
	      			if(crossCorelatedData[m]>max)
	      			{
	      				max = crossCorelatedData[m];
	      				x=m;
	      			}		
	      		}
	      		
                //code in a file 	      		
	      	    /**y calc/
	      	     * 
	      	     */
	      		
	      	    for(int par=(int)x,par1=0;par>x-300;par--,par1++)      	    	
	      	    {
	      	    	arrayInd[par1]=par;
	      	    	
	      	    }
	      	    for(int calc =0;calc<arrayInd.length-1;calc++)
	      	    {
	      	    	diff[calc]=crossCorelatedData[(int) x]-crossCorelatedData[(int) arrayInd[calc]];
	      	    	
	      	    }
	      	    min=diff[0];
	      		for(int m1=0;m1<diff.length;m1++)
	      		{
	      			if(diff[m1]>min)
	      			{
	      				min = diff[m1];
	      				y=m1;
	      			}	      			
	      		}	      		  		
	      		bpm=(float) (y/256);	      		
	      		bpm=60/bpm;
	      		count=0;	              
	      		values.add(bpm);	             
	      		for(Float iny:values)
	      		{
	      		   bpmNew +=iny/values.size();	      		 
	      		}
	      		runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub 
						displayText.setText(String.valueOf((int)(bpmNew+30)));
						bpmNew=0;
						
					}
				   });
	      		if(isCancelled())
	    		{
	    			break;
	    		}
	        }
	      	
			return null;
		}	
	}
	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		   case R.id.recordaudio:
		   {
			   //call audio record async task here
			   recordAudio = new RecorderAsync();
			   recordAudio.execute();		
			   break;
		   }
		   case R.id.stopaudio:
		   { 
			   recordAudio.cancel(true);
			   audioRecord.stop();
			   audioRecord.release();
			   break;
		   }
		}
	 }
	 public synchronized void recordAndStore()
	 {
	       
	 	   String f = new String(Environment.getExternalStorageDirectory()+"/swasthyaslate/audio"+dateNew+".mp3");
	 	   File newFile = new File(f);
	       recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
	       recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
	       recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
	       recorder.setOutputFile(newFile.getPath());
	       recorder.getMaxAmplitude();
	      
	       try {
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
		new Timer().schedule(new TimerTask() {

	        @Override
	        public void run() {
	            runOnUiThread(new Runnable() {
	                @Override
	                public void run() 
	                {
	                	Toast.makeText(getApplicationContext(), "recording stopped ", 10).show();
	                    //recorder.stop();
	                    recorder.reset();
	                    recorder.release();
	                    
	                }
	            });

	        }
	    }, 30000);	  
	    }

}
