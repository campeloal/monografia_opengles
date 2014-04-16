package opengles.android;

import graphics.shaders.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

@TargetApi(Build.VERSION_CODES.CUPCAKE) public class SplashActivity extends Activity {
    
    int[] texFiles = {R.raw.texture_1000, R.raw.texture_2500, R.raw.texture_5000,
    		R.raw.texture_10000, R.raw.texture_15000,R.raw.texture_15000,R.raw.texture_15000,R.raw.texture_15000};
    int[] cubeMapFiles = {R.raw.negative_x,R.raw.positive_x,R.raw.negative_y,R.raw.positive_y,
    		R.raw.negative_z,R.raw.positive_z};
    int[] reflectFiles = {R.raw.left,R.raw.right,R.raw.bottom,R.raw.top,R.raw.back,
    		R.raw.front};
    
    final Object3D[] _objects = new Object3D[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spinner);

        findViewById(R.id.loading_spinner);

        getResources().getInteger(android.R.integer.config_shortAnimTime);
        final Context context = this;
        
        Thread background = new Thread() 
		{
			public void run() 
			{
                try 
                {
                	_objects[0] = new Object3D(R.raw.hand_500_tex, context);
                	_objects[1] = new Object3D(R.raw.hand_1250_tex, context);
                	_objects[2] = new Object3D(R.raw.hand_2500_tex, context);
                	_objects[3] = new Object3D(R.raw.hand_5000_tex, context);
                	_objects[4] = new Object3D(R.raw.hand_8500_tex, context);
                	
                	for(int i =0; i<_objects.length;i++)
                	{
                		
                    	_objects[i].getTexture().readSimpleTexture(texFiles[i], context);
                    	_objects[i].getTexture().readCubeMapTexture(cubeMapFiles,context,"Cube Map");
                    	_objects[i].getTexture().readCubeMapTexture(reflectFiles,context, "Reflect");

                	}
                	  	
                } catch (Exception e) 
                {
                 
                }
                
                Resources r = Resources.getInstance();
                r.setObjects(_objects);
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
    

}
