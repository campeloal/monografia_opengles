package shaders;

import graphics.shaders.R;
import java.nio.FloatBuffer;

import android.annotation.TargetApi;
import android.opengl.GLES20;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.FROYO) public class ReflectionShader extends Shader{
	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 8 * FLOAT_SIZE_BYTES;
	
	public ReflectionShader()
	{
		super.vID = R.raw.reflection_vs;
		super.fID = R.raw.reflection_ps;
	}
	
	public void initShaderParams(int _program, FloatBuffer _vb,int reflectText, float[] mMVMatrix, float[] normalMatrix)
	{
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(_program, "aNormal"), 3, GLES20.GL_FLOAT, false,
				TRIANGLE_VERTICES_DATA_STRIDE_BYTES, _vb);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(_program, "aNormal"));
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(_program, "aNormal"), 3, GLES20.GL_FLOAT, false,
				TRIANGLE_VERTICES_DATA_STRIDE_BYTES, _vb);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(_program, "aNormal"));
        GLES20.glActiveTexture ( GLES20.GL_TEXTURE0 );
        GLES20.glBindTexture ( GLES20.GL_TEXTURE_CUBE_MAP, reflectText );
        // send to the shader
		GLES20.glUniform1i(GLES20.glGetUniformLocation(_program, "s_texture"), 0);
		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(_program, "MVMatrix"), 1, false, mMVMatrix, 0);
		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(_program, "NMatrix"), 1, false, normalMatrix, 0);
	}

}
