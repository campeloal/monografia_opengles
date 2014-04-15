package opengles.android;

public class NativeLib {

	static {
	    System.loadLibrary("ndkOpenGLES");
	}
	
	public native void startGPUTime();
	
	public native void stopGPUTime();
	
	public native int getTime();
}
