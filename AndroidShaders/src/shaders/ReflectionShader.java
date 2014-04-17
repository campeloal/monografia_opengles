package shaders;

import graphics.shaders.R;
import java.nio.FloatBuffer;
import java.util.Hashtable;

import android.annotation.TargetApi;
import android.opengl.GLES20;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.FROYO) public class ReflectionShader extends Shader{
	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 8 * FLOAT_SIZE_BYTES;
	private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
	private int sTextureAddr;
	private int aNormalAddr;
	private int mMVMatrixAddr;
	private int normalMatrixAddr;
	private int uMVPMatrix;
	private int aPositionAddr;
	
	public ReflectionShader()
	{
		super.vID = R.raw.reflection_vs;
		super.fID = R.raw.reflection_ps;
	}
	
	public void initShaderParams(@SuppressWarnings("rawtypes") Hashtable params)
	{
		GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, (float[]) params.get("mMVPMatrix"), 0);	
		GLES20.glVertexAttribPointer(aNormalAddr, 3, GLES20.GL_FLOAT, false,
				TRIANGLE_VERTICES_DATA_STRIDE_BYTES, ((FloatBuffer) params.get("vertex buffer")));
		GLES20.glEnableVertexAttribArray(aNormalAddr);
        GLES20.glActiveTexture ( GLES20.GL_TEXTURE0 );
        GLES20.glBindTexture ( GLES20.GL_TEXTURE_CUBE_MAP, (Integer) params.get("reflectText") );
        // send to the shader
		GLES20.glUniform1i(sTextureAddr, 0);
		GLES20.glUniformMatrix4fv(mMVMatrixAddr, 1, false, (float[]) params.get("mMVMatrix"), 0);
		GLES20.glUniformMatrix4fv(normalMatrixAddr, 1, false, (float[]) params.get("normalMatrix"), 0);
		// the vertex coordinates
		((FloatBuffer) params.get("vertex buffer")).position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
		GLES20.glVertexAttribPointer(aPositionAddr, 3, GLES20.GL_FLOAT, false,
				TRIANGLE_VERTICES_DATA_STRIDE_BYTES, ((FloatBuffer) params.get("vertex buffer")));
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(_program, "aPosition"));
	}

	public void getParamsLocations() {
		aNormalAddr = GLES20.glGetAttribLocation(_program, "aNormal");
		sTextureAddr = GLES20.glGetUniformLocation(_program, "s_texture");
		mMVMatrixAddr = GLES20.glGetUniformLocation(_program, "MVMatrix");
		normalMatrixAddr = GLES20.glGetUniformLocation(_program, "NMatrix");
		uMVPMatrix = GLES20.glGetUniformLocation(_program, "uMVPMatrix");
		aPositionAddr = GLES20.glGetAttribLocation(_program, "aPosition");
	}

}
