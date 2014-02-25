package graphics.shaders;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SplashActivity extends Activity {
    
    private View mContentView;
    private View mLoadingView;
    private int mShortAnimationDuration;

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
			final Object3D[] _objects = new Object3D[4];
			public void run() 
			{
                try 
                {
                	int[] normalMapTextures = {R.raw.diffuse_old, R.raw.sphere};
        			_objects[0] = new Object3D(R.raw.number_polygons0, false, context);
        			_objects[1] = new Object3D(R.raw.number_polygons1, false, context);
        			_objects[2] = new Object3D(R.raw.number_polygons2, false, context);
        			//_objects[3] = new Object3D(R.raw.number_polygons0, false, context);
        			_objects[3] = new Object3D(normalMapTextures, R.raw.texturedcube, true, context);
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


    private void showContentOrLoadingIndicator() {
        final View showView = mLoadingView;

        showView.setAlpha(0f);
        showView.setVisibility(View.VISIBLE);

        showView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

    }
}
