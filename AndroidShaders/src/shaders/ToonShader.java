package shaders;

import graphics.shaders.R;
import java.nio.FloatBuffer;
import java.util.Hashtable;

import android.annotation.TargetApi;
import android.opengl.GLES20;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.FROYO) public class ToonShader extends Shader{
	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 8 * FLOAT_SIZE_BYTES;
	private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
	private int aNormalAddr;
	private int lightDirAddr;
	private int uMVPMatrix;
	private int aPositionAddr;
	
	public ToonShader()
	{
		super.vID = R.raw.toon_vs;
		super.fID = R.raw.toon_ps;
	}
	
	public void initShaderParams(@SuppressWarnings("rawtypes") Hashtable params)
	{
		GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, (float[]) params.get("mMVPMatrix"), 0);	
		GLES20.glUniform3fv(lightDirAddr, 1, (float[]) params.get("lightDir"),0);
		// the normal info
		GLES20.glVertexAttribPointer(aNormalAddr, 3, GLES20.GL_FLOAT, false,
				TRIANGLE_VERTICES_DATA_STRIDE_BYTES, ((FloatBuffer) params.get("vertex buffer")));
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(_program, "aNormal"));
		// the vertex coordinates
		((FloatBuffer) params.get("vertex buffer")).position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
		GLES20.glVertexAttribPointer(aPositionAddr, 3, GLES20.GL_FLOAT, false,
				TRIANGLE_VERTICES_DATA_STRIDE_BYTES, ((FloatBuffer) params.get("vertex buffer")));
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(_program, "aPosition"));
	}

	public void getParamsLocations() {
		uMVPMatrix = GLES20.glGetUniformLocation(_program, "uMVPMatrix");
		aNormalAddr = GLES20.glGetAttribLocation(_program, "aNormal");
		lightDirAddr =  GLES20.glGetUniformLocation(_program, "lightDir");
		aPositionAddr = GLES20.glGetAttribLocation(_program, "aPosition");
	}

}
