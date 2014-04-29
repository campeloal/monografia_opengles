package shaders;

import java.nio.FloatBuffer;
import java.util.Hashtable;

import android.annotation.TargetApi;
import android.opengl.GLES20;
import android.os.Build;

import graphics.shaders.R;

@TargetApi(Build.VERSION_CODES.FROYO) public class FlatShader extends Shader{

	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 8 * FLOAT_SIZE_BYTES;
	private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
	private int uMVPMatrix;
	private int aPositionAddr;

	public FlatShader()
	{
		super.vID = R.raw.flat_vs;
		super.fID = R.raw.flat_ps;
	}

	
	public void getParamsLocations() {
		uMVPMatrix = GLES20.glGetUniformLocation(_program, "uMVPMatrix");	
		aPositionAddr = GLES20.glGetAttribLocation(_program, "aPosition");
	}
	
	public void initShaderParams(@SuppressWarnings("rawtypes") Hashtable params)
	{
		GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, (float[]) params.get("mMVPMatrix"), 0);
		// the vertex coordinates
		((FloatBuffer) params.get("vertex buffer")).position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
		GLES20.glVertexAttribPointer(aPositionAddr, 3, GLES20.GL_FLOAT, false,
				TRIANGLE_VERTICES_DATA_STRIDE_BYTES, ((FloatBuffer) params.get("vertex buffer")));
		GLES20.glEnableVertexAttribArray(aPositionAddr);
	}

}
