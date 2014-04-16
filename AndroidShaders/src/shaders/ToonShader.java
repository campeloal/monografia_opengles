package shaders;

import graphics.shaders.R;
import java.nio.FloatBuffer;

import android.annotation.TargetApi;
import android.opengl.GLES20;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.FROYO) public class ToonShader extends Shader{
	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 8 * FLOAT_SIZE_BYTES;
	
	public ToonShader()
	{
		super.vID = R.raw.toon_vs;
		super.fID = R.raw.toon_ps;
	}
	
	public void initShaderParams(int _program, FloatBuffer _vb,float[] lightDir)
	{
		int loc = GLES20.glGetUniformLocation(_program, "lightDir");
		GLES20.glUniform3fv(loc, 1, lightDir,0);
		// the normal info
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(_program, "aNormal"), 3, GLES20.GL_FLOAT, false,
				TRIANGLE_VERTICES_DATA_STRIDE_BYTES, _vb);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(_program, "aNormal"));
	}

}
