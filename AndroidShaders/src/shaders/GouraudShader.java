package shaders;

import graphics.shaders.R;
import graphics.shaders.R.raw;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

public class GouraudShader extends Shader{
	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int TRIANGLE_VERTICES_DATA_NOR_OFFSET = 3;
	private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 8 * FLOAT_SIZE_BYTES;
	
	public GouraudShader()
	{
		super.vID = R.raw.gouraud_vs;
		super.fID = R.raw.gouraud_ps;
	}
	
	public void initShaderParams(int _program, FloatBuffer _vb,float[] mMVPMatrix, float[] lightPos, float[] lightColor, float[] matAmbient, 
			float[] matSpecular, float[] matDiffuse, float matShininess, float[] eyePos)
	{
		// send to the shader
		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(_program, "normalMatrix"), 1, false, mMVPMatrix, 0);
		
		// lighting variables
		// send to shaders
		GLES20.glUniform4fv(GLES20.glGetUniformLocation(_program, "lightPos"), 1, lightPos, 0);
		GLES20.glUniform4fv(GLES20.glGetUniformLocation(_program, "lightColor"), 1, lightColor, 0);
		
		
		// material 
		GLES20.glUniform4fv(GLES20.glGetUniformLocation(_program, "matAmbient"), 1, matAmbient, 0);
		GLES20.glUniform4fv(GLES20.glGetUniformLocation(_program, "matDiffuse"), 1, matDiffuse, 0);
		GLES20.glUniform4fv(GLES20.glGetUniformLocation(_program, "matSpecular"), 1, matSpecular, 0);
		GLES20.glUniform1f(GLES20.glGetUniformLocation(_program, "matShininess"), matShininess);
		
		// eye position
		GLES20.glUniform3fv(GLES20.glGetUniformLocation(_program, "eyePos")/*shader.eyeHandle*/, 1, eyePos, 0);
		// the normal info
		_vb.position(TRIANGLE_VERTICES_DATA_NOR_OFFSET);
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(_program, "aNormal"), 3, GLES20.GL_FLOAT, false,
				TRIANGLE_VERTICES_DATA_STRIDE_BYTES, _vb);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(_program, "aNormal"));
	}

}
