package shaders;

import graphics.shaders.R;
import graphics.shaders.R.raw;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

public class TextureShader extends Shader{
	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 8 * FLOAT_SIZE_BYTES;
	private static final int TRIANGLE_VERTICES_DATA_TEX_OFFSET = 6;
	
	public TextureShader()
	{
		super.vID = R.raw.simple_tex_vs;
		super.fID = R.raw.simple_tex_ps;
	}
	
	public void initShaderParams(int _program, FloatBuffer _vb,int[] simpleTexts,int currentText)
	{
		// Bind the texture
		GLES20.glActiveTexture ( GLES20.GL_TEXTURE0 );
        GLES20.glBindTexture ( GLES20.GL_TEXTURE_2D, simpleTexts[currentText] );
		GLES20.glUniform1i(GLES20.glGetUniformLocation(_program, "texture"), 0);
		
		_vb.position(TRIANGLE_VERTICES_DATA_TEX_OFFSET);
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(_program, "textCoord"), 2, GLES20.GL_FLOAT, false,
				TRIANGLE_VERTICES_DATA_STRIDE_BYTES, _vb);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(_program, "textCoord"));
	}

}
