package shaders;

import graphics.shaders.R;
import java.nio.FloatBuffer;
import java.util.Hashtable;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.opengl.GLES20;
import android.os.Build;

@SuppressLint("InlinedApi") @TargetApi(Build.VERSION_CODES.FROYO) public class TextureShader extends Shader{
	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 8 * FLOAT_SIZE_BYTES;
	private static final int TRIANGLE_VERTICES_DATA_TEX_OFFSET = 6;
	private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
	private int textureAddr;
	private int textureCoordAddr;
	private int uMVPMatrix;
	private int aPositionAddr;
	
	public TextureShader()
	{
		super.vID = R.raw.simple_tex_vs;
		super.fID = R.raw.simple_tex_ps;
	}
	
	public void initShaderParams(@SuppressWarnings("rawtypes") Hashtable params)
	{		
		GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, (float[]) params.get("mMVPMatrix"), 0);	
		// Bind the texture
		GLES20.glActiveTexture ( GLES20.GL_TEXTURE0 );
        GLES20.glBindTexture ( GLES20.GL_TEXTURE_2D, (Integer) params.get("simpleText"));
		GLES20.glUniform1i(textureAddr, 0);
		
		((FloatBuffer) params.get("vertex buffer")).position(TRIANGLE_VERTICES_DATA_TEX_OFFSET);
		GLES20.glVertexAttribPointer(textureCoordAddr, 2, GLES20.GL_FLOAT, false,
				TRIANGLE_VERTICES_DATA_STRIDE_BYTES, ((FloatBuffer) params.get("vertex buffer")));
		GLES20.glEnableVertexAttribArray(textureCoordAddr);
		
		// the vertex coordinates
		((FloatBuffer) params.get("vertex buffer")).position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
		GLES20.glVertexAttribPointer(aPositionAddr, 3, GLES20.GL_FLOAT, false,
				TRIANGLE_VERTICES_DATA_STRIDE_BYTES, ((FloatBuffer) params.get("vertex buffer")));
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(_program, "aPosition"));
	}

	public void getParamsLocations() {
		uMVPMatrix = GLES20.glGetUniformLocation(_program, "uMVPMatrix");
		textureAddr = GLES20.glGetUniformLocation(_program, "texture");
		textureCoordAddr = GLES20.glGetAttribLocation(_program, "textCoord");
		aPositionAddr = GLES20.glGetAttribLocation(_program, "aPosition");
	}

}
