package graphics.shaders;

import java.io.IOException;
import java.io.InputStream;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SplashActivity extends Activity {
    
    private View mContentView;
    private View mLoadingView;
    private int mShortAnimationDuration;
    int[] texFiles = {R.raw.texture_1000, R.raw.texture_2500, R.raw.texture_5000,
    		R.raw.texture_10000, R.raw.texture_15000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spinner);

        mLoadingView = findViewById(R.id.loading_spinner);

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        final Context context = this;
        
        Thread background = new Thread() 
		{
			final Object3D[] _objects = new Object3D[5];
			Bitmap[] cubeMap = new Bitmap[5];
			Bitmap[] reflect = new Bitmap[5];
			
			public void run() 
			{
                try 
                {
        			_objects[0] = new Object3D(R.raw.hand_1000_tex, context);
        			_objects[1] = new Object3D(R.raw.hand_2500_tex, context);
        			_objects[2] = new Object3D(R.raw.hand_5000_tex, context);
        			_objects[3] = new Object3D(R.raw.hand_10000_tex, context);
        			_objects[4] = new Object3D(R.raw.hand_15000_tex, context);
        			setSimpleTexture();
        			cubeMap = setCubeMapTexture(R.raw.negative_x,R.raw.positive_x,R.raw.negative_y,R.raw.positive_y,R.raw.negative_z,R.raw.positive_z);
        			reflect = setCubeMapTexture(R.raw.left,R.raw.right,R.raw.bottom,R.raw.top,R.raw.back,R.raw.front);
                } catch (Exception e) 
                {
                 
                }
                
                Resources r = Resources.getInstance();
                r.setObjects(_objects);
        		r.setCubeMapText(cubeMap);
        		r.setReflectText(reflect);
                //Change Activity
                Intent i=new Intent(getBaseContext(),ShaderActivity.class);
                startActivity(i);
                  
                //Remove activity
                finish();
            }
        };
         
        // start thread
        background.start();
     
    }
    
    public void setSimpleTexture() throws IOException{
    	Bitmap simpleTex[] = new Bitmap[5];
    	for(int i = 0; i < 5; i++)
    	{
    		InputStream is = this.getResources().openRawResource(texFiles[i]);
    		Bitmap texture = BitmapFactory.decodeStream(is); 
    		simpleTex[i] = texture;
    		is.close();
    	}
    
    	Resources r = Resources.getInstance();
    	r.setSimpleTexts(simpleTex);
    	
    }
    
    public Bitmap[] setCubeMapTexture(int n_x, int p_x, int n_y, int p_y, int n_z, int p_z) throws IOException{
    	Bitmap positiveX,negativeX,positiveY,negativeY,positiveZ,negativeZ;
		InputStream is = this.getResources().openRawResource(p_x);
		positiveX = BitmapFactory.decodeStream(is);
		is = this.getResources().openRawResource(n_x);
		negativeX = BitmapFactory.decodeStream(is);
		is = this.getResources().openRawResource(p_y);
		positiveY = BitmapFactory.decodeStream(is);
		is = this.getResources().openRawResource(n_y);
		negativeY = BitmapFactory.decodeStream(is);
		is = this.getResources().openRawResource(p_z);
		positiveZ = BitmapFactory.decodeStream(is);
		is = this.getResources().openRawResource(n_z);
		negativeZ = BitmapFactory.decodeStream(is);
		is.close();
		
		Bitmap[] cubeMap = new Bitmap[6];
		cubeMap[0] = negativeX;
		cubeMap[1] = positiveX;
		cubeMap[2] = negativeY;
		cubeMap[3] = positiveY;
		cubeMap[4] = negativeZ;
		cubeMap[5] = positiveZ;
		
		return cubeMap;
		
    }

}
