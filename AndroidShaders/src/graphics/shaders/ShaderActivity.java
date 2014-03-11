package graphics.shaders;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;

public class ShaderActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;		
		
		// Create a new GLSurfaceView - this holds the GL Renderer
		mGLSurfaceView = new GLSurfaceView(this);
		
		// detect if OpenGL ES 2.0 support exists - if it doesn't, exit.
		if (detectOpenGLES20()) {
			// Tell the surface view we want to create an OpenGL ES 2.0-compatible
			// context, and set an OpenGL ES 2.0-compatible renderer.
			mGLSurfaceView.setEGLContextClientVersion(2);
	        renderer = new Renderer(this);
			mGLSurfaceView.setRenderer(renderer);
			
		} 
		else { 
			this.finish();
		} 
		 
		// set the content view
		setContentView(mGLSurfaceView);
		createButtons(width,height,this);
		
	}

	/**
	 * Detects if OpenGL ES 2.0 exists
	 * @return true if it does
	 */
	private boolean detectOpenGLES20() {
		ActivityManager am =
			(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = am.getDeviceConfigurationInfo();
		Log.d("OpenGL Ver:", info.getGlEsVersion());
		return (info.reqGlEsVersion >= 0x20000);
	}
	
	private void createButtons(int width, int height, Context context){
		
		final Drawable draw_add = this.getResources().getDrawable(R.raw.add);
		final Drawable draw_add_press = this.getResources().getDrawable(R.raw.add_press);
		final Drawable draw_dec = this.getResources().getDrawable(R.raw.dec);
		final Drawable draw_dec_press = this.getResources().getDrawable(R.raw.dec_press);
		
		final Button add = new Button(this);
		final Button dec = new Button(this);
		add.setX((float) (width * 0.025));
		add.setY((float) (height*0.05));
		add.setBackground(draw_add);
		dec.setX((float) (width * 0.025));
		dec.setY((float) (height*0.2));
		dec.setBackground(draw_dec);
		
		
		add.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
			    if (event.getAction() == MotionEvent.ACTION_DOWN) {
			        add.setBackground(draw_add_press);
			    }
			    else if (event.getAction() == MotionEvent.ACTION_UP) {
			    	add.setBackground(draw_add);
			    	addPolygons();
			    }

			    return true;
			}
			});
		
		dec.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
			    if (event.getAction() == MotionEvent.ACTION_DOWN) {
			        dec.setBackground(draw_dec_press);
			    }
			    else if (event.getAction() == MotionEvent.ACTION_UP) {
			    	dec.setBackground(draw_dec);
			    	decPolygons();
			    }

			    return true;
			}
			});
		
		
		addContentView(add, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		addContentView(dec, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

	}
	
	public void addPolygons(){
		
		if(CURRENT_POLYGON < LAST_POLYGON)
		{
			CURRENT_POLYGON++;
			renderer.setObject(CURRENT_POLYGON);
		}
		
	}
	
public void decPolygons(){
		
		if(CURRENT_POLYGON > FIRST_POLYGON)
		{
			CURRENT_POLYGON--;
			renderer.setObject(CURRENT_POLYGON);
		}
		
	}

	/************
	 *  MENU FUNCTIONS
	 **********/
	/*
	 * Creates the menu and populates it via xml
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	/*
	 * On selection of a menu item
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.gouraud: 			// Gouraud Shading
			renderer.setShader(this.GOURAUD_SHADER);
			renderer.enableGouraudShader(true);
			renderer.enablePhongShader(false);
			renderer.enableRedShader(false);
			renderer.enableToonShader(false);
			renderer.enableCubeMapShader(false);
			renderer.enableReflectionShader(false);
			return true;
		case R.id.phong: 			// Phong Shading
			renderer.setShader(this.PHONG_SHADER);
			renderer.enableGouraudShader(false);
			renderer.enablePhongShader(true);
			renderer.enableRedShader(false);
			renderer.enableToonShader(false);
			renderer.enableCubeMapShader(false);
			renderer.enableReflectionShader(false);
			return true;
		case R.id.flat:
			renderer.setShader(this.FLAT_SHADER);
			renderer.enableGouraudShader(false);
			renderer.enablePhongShader(false);
			renderer.enableRedShader(false);
			renderer.enableToonShader(false);
			renderer.enableCubeMapShader(false);
			renderer.enableReflectionShader(false);
			return true;
		case R.id.red:
			renderer.setShader(this.RED_SHADER);
			renderer.enableGouraudShader(false);
			renderer.enablePhongShader(false);
			renderer.enableRedShader(true);
			renderer.enableToonShader(false);
			renderer.enableCubeMapShader(false);
			renderer.enableReflectionShader(false);
			return true;
		case R.id.toon:
			renderer.setShader(this.TOON_SHADER);
			renderer.enableGouraudShader(false);
			renderer.enablePhongShader(false);
			renderer.enableRedShader(false);
			renderer.enableToonShader(true);
			renderer.enableCubeMapShader(false);
			renderer.enableReflectionShader(false);
			return true;
		case R.id.cubemap:
			renderer.setShader(this.CUBEMAP_SHADER);
			renderer.enableGouraudShader(false);
			renderer.enablePhongShader(false);
			renderer.enableRedShader(false);
			renderer.enableToonShader(false);
			renderer.enableCubeMapShader(true);
			renderer.enableReflectionShader(false);
			return true;
		case R.id.reflection:
			renderer.setShader(this.REFLECTION_SHADER);
			renderer.enableGouraudShader(false);
			renderer.enablePhongShader(false);
			renderer.enableRedShader(false);
			renderer.enableToonShader(false);
			renderer.enableCubeMapShader(false);
			renderer.enableReflectionShader(true);
			return true;
		case R.id.quit:				// Quit the program
			quit();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/************
	 * TOUCH FUNCTION - Should allow user to rotate the environment
	 **********/
	@Override public boolean onTouchEvent(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:			// one touch: drag
			Log.d("ShaderActivity", "mode=DRAG" );
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:	// two touches: zoom
			Log.d("ShaderActivity", "mode=ZOOM" );
			oldDist = spacing(e);
			if (oldDist > 10.0f) {
				mode = ZOOM; // zoom
			}
			break;
		case MotionEvent.ACTION_UP:		// no mode
			mode = NONE;
			Log.d("ShaderActivity", "mode=NONE" );
			oldDist = 100.0f;
			break;
		case MotionEvent.ACTION_POINTER_UP:		// no mode
			mode = NONE;
			Log.d("ShaderActivity", "mode=NONE" );
			oldDist = 100.0f;
			break;
		case MotionEvent.ACTION_MOVE:						// rotation
			if (e.getPointerCount() > 1 && mode == ZOOM) {
				newDist = spacing(e);
				Log.d("SPACING: ", "OldDist: " + oldDist + ", NewDist: " + newDist);
				if (newDist > 10.0f) {
					float scale = newDist/oldDist; // scale
					// scale in the renderer
					renderer.changeScale(scale);

					oldDist = newDist;
				}
			}
			else if (mode == DRAG){
				float dx = x - mPreviousX;
				float dy = y - mPreviousY;
				renderer.mAngleX += dx * TOUCH_SCALE_FACTOR;
				renderer.mAngleY += dy * TOUCH_SCALE_FACTOR;
				mGLSurfaceView.requestRender();
			}
			break;
		}
		mPreviousX = x;
		mPreviousY = y;
		return true;
	}

	// finds spacing
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}


	// Quit the app
	private void quit() {
		//super.onDestroy();
		this.finish();
	}

	/********************************
	 * PROPERTIES
	 *********************************/

	private GLSurfaceView mGLSurfaceView;

	// The Renderer
	Renderer renderer;

	// rotation
	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	private float mPreviousX;
	private float mPreviousY;

	// shader constants
	private final int GOURAUD_SHADER = 0;
	private final int PHONG_SHADER = 1;
	private final int RED_SHADER = 2;
	private final int TOON_SHADER = 3;
	private final int FLAT_SHADER = 4;
	private final int CUBEMAP_SHADER = 5;
	private final int REFLECTION_SHADER = 6;

	//private final int POLYGON_3 = 3;
	private int CURRENT_POLYGON = 0;
	private final int LAST_POLYGON = 4;
	private final int FIRST_POLYGON = 0;
	

	// touch events
	private final int NONE = 0;
	private final int DRAG = 0;
	private final int ZOOM = 0;

	// pinch to zoom
	float oldDist = 100.0f;
	float newDist;

	int mode = 0;
}
