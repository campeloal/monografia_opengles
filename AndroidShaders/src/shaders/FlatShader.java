package shaders;

import graphics.shaders.R;
import graphics.shaders.R.raw;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

public class FlatShader extends Shader{

	public FlatShader()
	{
		super.vID = R.raw.flat_vs;
		super.fID = R.raw.flat_ps;
	}
	

}
