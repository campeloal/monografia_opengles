package shaders;

import graphics.shaders.R;
import graphics.shaders.R.raw;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

public class CubeMapShader extends Shader{
	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 8 * FLOAT_SIZE_BYTES;
	
	public CubeMapShader()
	{
		super.vID = R.raw.cubemap_vs;
		super.fID = R.raw.cubmap_ps;
	}
	
	public void initShaderParams(int _program, FloatBuffer _vb,int cubeMapText)
	{
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(_program, "aNormal"), 3, GLES20.GL_FLOAT, false,
				TRIANGLE_VERTICES_DATA_STRIDE_BYTES, _vb);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(_program, "aNormal"));
		// Bind the texture
        GLES20.glActiveTexture ( GLES20.GL_TEXTURE0 );
        GLES20.glBindTexture ( GLES20.GL_TEXTURE_CUBE_MAP, cubeMapText );
		GLES20.glUniform1i(GLES20.glGetUniformLocation(_program, "s_texture"), 0);
	}

}
