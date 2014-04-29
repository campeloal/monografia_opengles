package opengles.android;

import java.util.ArrayList;

import graphics.shaders.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

@TargetApi(Build.VERSION_CODES.CUPCAKE) public class SplashActivity extends Activity {
    
    int[] texFiles = {R.raw.texture_2500, R.raw.texture_2500, R.raw.texture_5000,
    		R.raw.texture_10000, R.raw.texture_15000,R.raw.texture_15000,R.raw.texture_15000,R.raw.texture_15000};
    int[] cubeMapFiles = {R.raw.negative_x,R.raw.positive_x,R.raw.negative_y,R.raw.positive_y,
    		R.raw.negative_z,R.raw.positive_z};
    int[] reflectFiles = {R.raw.left,R.raw.right,R.raw.bottom,R.raw.top,R.raw.back,
    		R.raw.front};
    
    final ArrayList<Object3D> objects = new ArrayList<Object3D>();
    String loadMoreObj;

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
                	Intent myIntent = getIntent(); // gets the previously created intent
                	String moreObj = ""; 
                	if(myIntent.getExtras()!=null)
                		moreObj = myIntent.getStringExtra("Shader Activity"); 

                	if (moreObj.equals("Load more obj - 50000/60000"))
                	{
                		objects.add( new Object3D(R.raw.hand_50000, context));
                		objects.add( new Object3D(R.raw.hand_60000, context));
                    	loadMoreObj = "Load more obj - 50000/60000";
                	}
                	else if(moreObj.equals("Load more obj - 80000"))
                	{
                		objects.add( new Object3D(R.raw.hand_80000, context));
                		loadMoreObj = "Load more obj - 80000";
                	}
                	else if(moreObj.equals("Load more obj - 100000"))
                	{
                		objects.add( new Object3D(R.raw.head_100000, context));
                		loadMoreObj = "Load more obj - 100000";
                	}                	
                	else 
                	{
                		objects.add( new Object3D(R.raw.hand_1250_tex, context));
                		objects.add( new Object3D(R.raw.hand_10000, context));
                		objects.add( new Object3D(R.raw.hand_20000, context));
                		objects.add( new Object3D(R.raw.hand_30000, context));
                		objects.add( new Object3D(R.raw.hand_40000, context));
	                	loadMoreObj = "Load obj";
                	}
	                	
                	for(int i =0; i< objects.size();i++)
                	{
                		objects.get(i).getTexture().readSimpleTexture(texFiles[i], context);
                		objects.get(i).getTexture().readCubeMapTexture(cubeMapFiles,context,"Cube Map");
                		objects.get(i).getTexture().readCubeMapTexture(reflectFiles,context, "Reflect");
                	}
                	  	
                } catch (Exception e) 
                {
                 
                }
                
                Resources r = Resources.getInstance();
                r.setObjects(objects);
                //Change Activity
                Intent i=new Intent(getBaseContext(),ShaderActivity.class);
                i.putExtra("Splash Activity",loadMoreObj);
                startActivity(i);
                  
                //Remove activity
                finish();
            }
        };
         
        // start thread
        background.start();
     
    }
    

}
