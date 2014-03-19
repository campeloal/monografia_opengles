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
			final Object3D[] _objects = new Object3D[5];
			public void run() 
			{
                try 
                {
        			_objects[0] = new Object3D(R.raw.hand_1000_tex, context);
        			_objects[1] = new Object3D(R.raw.hand_2500_tex, context);
        			_objects[2] = new Object3D(R.raw.hand_5000_tex, context);
        			_objects[3] = new Object3D(R.raw.hand_10000_tex, context);
        			_objects[4] = new Object3D(R.raw.hand_15000_tex, context);
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
