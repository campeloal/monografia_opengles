package shaders;

import graphics.shaders.R;
import java.nio.FloatBuffer;
import java.util.Hashtable;

import android.annotation.TargetApi;
import android.opengl.GLES20;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.FROYO) public class PhongShader extends Shader{
	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int TRIANGLE_VERTICES_DATA_NOR_OFFSET = 3;
	private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 8 * FLOAT_SIZE_BYTES;
	private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
	private int lightColorAddr;
	private int matAmbientAddr;
	private int lightPosAddr;
	private int normalMatrixAddr;
	private int matDiffuseAddr;
	private int matSpecularAddr;
	private int matShininessAddr;
	private int eyePosAddr;
	private int aNormalAddr;
	private int uMVPMatrix;
	private int aPositionAddr;
	
	
	public PhongShader()
	{
		super.vID = R.raw.phong_vs;
		super.fID = R.raw.phong_ps;
	}
	
	public void initShaderParams(@SuppressWarnings("rawtypes") Hashtable params)
	{
		
		// send to the shader
		GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, (float[]) params.get("mMVPMatrix"), 0);
		GLES20.glUniformMatrix4fv(normalMatrixAddr, 1, false, ((float[]) params.get("mMVPMatrix")), 0);
		
		// lighting variables
		// send to shaders
		GLES20.glUniform4fv(lightPosAddr, 1, ((float[]) params.get("lightPos")), 0);
		GLES20.glUniform4fv(lightColorAddr, 1, ((float[]) params.get("lightColor")), 0);
		
		// material 
		GLES20.glUniform4fv(matAmbientAddr, 1, ((float[]) params.get("matAmbient")), 0);
		GLES20.glUniform4fv(matDiffuseAddr, 1, ((float[]) params.get("matDiffuse")), 0);
		GLES20.glUniform4fv(matSpecularAddr, 1, ((float[]) params.get("matSpecular")), 0);
		GLES20.glUniform1f(matShininessAddr, ((Float) params.get("matShininess")));
		
		// eye position
		GLES20.glUniform3fv(eyePosAddr, 1, (float[]) params.get("eyePos"), 0);
		// the normal info
		((FloatBuffer) params.get("vertex buffer")).position(TRIANGLE_VERTICES_DATA_NOR_OFFSET);
		GLES20.glVertexAttribPointer(aNormalAddr, 3, GLES20.GL_FLOAT, false,
				TRIANGLE_VERTICES_DATA_STRIDE_BYTES, ((FloatBuffer) params.get("vertex buffer")));
		GLES20.glEnableVertexAttribArray(aNormalAddr);
		// the vertex coordinates
		((FloatBuffer) params.get("vertex buffer")).position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
		GLES20.glVertexAttribPointer(aPositionAddr, 3, GLES20.GL_FLOAT, false,
				TRIANGLE_VERTICES_DATA_STRIDE_BYTES, ((FloatBuffer) params.get("vertex buffer")));
		GLES20.glEnableVertexAttribArray(aPositionAddr);
	}

	public void getParamsLocations() {
		normalMatrixAddr = GLES20.glGetUniformLocation(_program, "normalMatrix");
		lightPosAddr = GLES20.glGetUniformLocation(_program, "lightPos");
		lightColorAddr = GLES20.glGetUniformLocation(_program, "lightColor");
		matAmbientAddr = GLES20.glGetUniformLocation(_program, "matAmbient");
		matDiffuseAddr = GLES20.glGetUniformLocation(_program, "matDiffuse");
		matSpecularAddr = GLES20.glGetUniformLocation(_program, "matSpecular");
		matShininessAddr = GLES20.glGetUniformLocation(_program, "matShininess");
		eyePosAddr = GLES20.glGetUniformLocation(_program, "eyePos");
		aNormalAddr = GLES20.glGetAttribLocation(_program, "aNormal");	
		uMVPMatrix = GLES20.glGetUniformLocation(_program, "uMVPMatrix");
		aPositionAddr = GLES20.glGetAttribLocation(_program, "aPosition");
	}

}
