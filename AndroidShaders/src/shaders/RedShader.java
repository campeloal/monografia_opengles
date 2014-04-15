package shaders;

import graphics.shaders.R;
import graphics.shaders.R.raw;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

public class RedShader extends Shader{

	public RedShader()
	{
		super.vID = R.raw.red_vs;
		super.fID = R.raw.red_ps;
	}
	

}
