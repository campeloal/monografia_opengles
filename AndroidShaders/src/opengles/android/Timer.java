package opengles.android;

import android.content.Context;
import android.widget.Toast;

public class Timer {

	//Measuring the performance
	NativeLib nativeLib;
	int finalTime;
	int accumulatedTime;
	int measures;
	boolean queryStarted = false;
	private static Timer instance;	
	boolean extensionActivated = false;
	
	public Timer()
	{
		nativeLib = new NativeLib();
		finalTime = 0;
		accumulatedTime =0;
		measures = 0;		
	}
	
	public static Timer getInstance() {
	      if (instance == null)
	         instance = new Timer();
	      return instance;
	   }
	
	public void startTime(String openglExtensions)
	{
		
		if((measures < 10) && (openglExtensions.contains("GL_EXT_disjoint_timer_query")))
		{
			extensionActivated = true;
			nativeLib.startGPUTime();
			queryStarted = true;
		} 
	}
	
	public void stopTime()
	{
		if((measures < 10) && (queryStarted == true) && (extensionActivated == true))
		{
			nativeLib.stopGPUTime();
			accumulatedTime += nativeLib.getTime();
			measures++;
			queryStarted = false;
			if(measures == 10)
			{
				finalTime = accumulatedTime/10;
			}
			
		}
	}
	
	public void printTime(Context mContext)
	{
		String text, time;
		int duration = 2;
		if(extensionActivated == true)
		{
			System.out.println("TIME " + finalTime);
			time = String.valueOf(finalTime);
			text = "The GPU time to render this polygon is " + time + " nanoseconds.";			
		}
		else
		{
			text = "This feature is not supported by this device.";
		}
		Toast toast = Toast.makeText(mContext, text, duration);
		toast.show();
	}
	
	public void restartTimer(){
		accumulatedTime = 0;
		finalTime = 0;
		measures = 0;
		queryStarted = false;
	}
}
