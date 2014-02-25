package graphics.shaders;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
 
public class SplashActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        final Context context = this;
        
        Thread background = new Thread() {
            public void run() {
                 
                try {
                	final Object3D[] _objects = new Object3D[4];
            		Thread background = new Thread() {
                        public void run() {
                            try {
                            	int[] normalMapTextures = {R.raw.diffuse_old, R.raw.sphere};
                    			_objects[0] = new Object3D(R.raw.number_polygons0, false, context);
                    			_objects[1] = new Object3D(R.raw.number_polygons1, false, context);
                    			_objects[2] = new Object3D(R.raw.number_polygons2, false, context);
                    			//_objects[3] = new Object3D(R.raw.number_polygons0, false, context);
                    			_objects[3] = new Object3D(normalMapTextures, R.raw.texturedcube, true, context);
                            } catch (Exception e) {
                             
                            }
                            Resources r = Resources.getInstance();
                            r.setObjects(_objects);
                            Intent i=new Intent(getBaseContext(),ShaderActivity.class);
                            startActivity(i);
                              
                            //Remove activity
                            finish();
                        }
                    };
                     
                    // start thread
                    background.start();
                     
                } catch (Exception e) {
                 
                }
            }
        };
         
        // start thread
        background.start();
        
   } 
    
    @Override
    protected void onDestroy() {
         
        super.onDestroy();
         
    }
}
