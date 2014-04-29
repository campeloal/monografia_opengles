package opengles.android;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import shaders.CubeMapShader;
import shaders.FlatShader;
import shaders.GouraudShader;
import shaders.PhongShader;
import shaders.RandColorShader;
import shaders.RedShader;
import shaders.ReflectionShader;
import shaders.Shader;
import shaders.TextureShader;
import shaders.ToonShader;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.FROYO) class Renderer implements GLSurfaceView.Renderer {

	int[] texIDs;
	// rotation 
	public float mAngleX;
	public float mAngleY;

	// shader constants
	private final int GOURAUD_SHADER = 0;
	private final int PHONG_SHADER = 1;
	private final int RED_SHADER = 2;
	private final int TOON_SHADER = 3;
	private final int FLAT_SHADER = 4;
	private final int CUBEMAP_SHADER = 5;
	private final int REFLECTION_SHADER = 6;
	private final int TEXTURE_SHADER = 7;
	private final int RANDCOLOR_SHADER = 8;

	// array of shaders
	Shader _shaders[] = new Shader[9];
	private int _currentShader;

	// The objects
	ArrayList<Object3D> objects;

	// current object
	private int _currentObject;

	// Modelview/Projection matrices
	private float[] mMVPMatrix = new float[16];		//modelviewprojection
	private float[] mProjMatrix = new float[16];
	private float[] mScaleMatrix = new float[16];   // scaling
	private float[] mRotXMatrix = new float[16];	// rotation x
	private float[] mRotYMatrix = new float[16];	// rotation y
	private float[] mMMatrix = new float[16];		// model
	private float[] mVMatrix = new float[16]; 		// view
	private float[] mMVMatrix = new float[16]; 		// modelview
	private float[] normalMatrix = new float[16]; 	// modelview normal
	// light parameters
	private float[] lightPos = {0.0f, 0.0f, 0.0f, 1};
	private float[] lightDir = {0.0f,1.0f,1.0f};
	private float[] lightColor = {0.53f, 0.33f, 0.33f,1.0f};

	// material properties
	private float[] matAmbient = {1.0f, 0.5f, 0.5f, 1.0f};
	private float[] matDiffuse = {0.75f, 0.75f, 0.75f, 1.0f};
	private float[] matSpecular = {1.0f, 1.0f, 1.0f, 1.0f};
	private float matShininess = 5.0f;

	// eye pos
	private float[] eyePos = {-5.0f, 0.0f, 0.0f};
	// scaling
	float scaleX = 1.0f;
	float scaleY = 1.0f;
	float scaleZ = 1.0f;


	private int reflectText, cubeMapText, simpleText;

	private Context mContext;
	@SuppressWarnings("rawtypes")
	Hashtable shaderParams;
	
	//Measuring the performance
	Timer timer;
	private static String TAG = "Renderer";

	@SuppressWarnings("rawtypes")
	public Renderer(Context context) {
		
		mContext = context;
		Resources r = Resources.getInstance();
		this.objects = r.getObjects();
		
		//set current object and shader
		_currentShader = this.GOURAUD_SHADER;
		_currentObject = 0;
		shaderParams = new Hashtable();
		timer = Timer.getInstance();
	}

	@SuppressWarnings("unchecked")

	@SuppressLint("NewApi") public void onDrawFrame(GL10 glUnused) {
		
		// Ignore the passed-in GL10 interface, and use the GLES20
		// class's static methods instead.
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

		GLES20.glUseProgram(0);

		// the current shader
		Shader shader = _shaders[this._currentShader]; // PROBLEM!
		int _program = shader.getProgram();
		
		// Start using the shader
		GLES20.glUseProgram(_program);
		checkGlError("glUseProgram");
		
		changeModelViewMatrix();
		
		// Get buffers from mesh
		Object3D ob = objects.get(_currentObject);
		FloatBuffer _vb = ob.getVertexBuffer();
		
		reflectText = objects.get(_currentObject).getTexture().getReflectTexture();
		cubeMapText = objects.get(_currentObject).getTexture().getCubeMapTexture();
		simpleText = objects.get(_currentObject).getTexture().getSimpleTexture();
		
		
		if(_shaders[this.GOURAUD_SHADER].isActivated())
		{
			shaderParams.put("vertex buffer", _vb); shaderParams.put("mMVPMatrix", mMVPMatrix);
			shaderParams.put("lightPos", lightPos); shaderParams.put("lightColor", lightColor);
			shaderParams.put("matAmbient", matAmbient); shaderParams.put("matSpecular", matSpecular);
			shaderParams.put("matDiffuse", matDiffuse); shaderParams.put("matShininess", matShininess);
			shaderParams.put("eyePos", eyePos);
	
			((GouraudShader) _shaders[this.GOURAUD_SHADER]).initShaderParams(shaderParams);
		}
		
		else if(_shaders[this.PHONG_SHADER].isActivated())
		{
			shaderParams.put("vertex buffer", _vb); shaderParams.put("mMVPMatrix", mMVPMatrix);
			shaderParams.put("lightPos", lightPos); shaderParams.put("lightColor", lightColor);
			shaderParams.put("matAmbient", matAmbient); shaderParams.put("matSpecular", matSpecular);
			shaderParams.put("matDiffuse", matDiffuse); shaderParams.put("matShininess", matShininess);
			shaderParams.put("eyePos", eyePos);
			((PhongShader) _shaders[this.PHONG_SHADER]).initShaderParams(shaderParams);
		}
		
		else if(_shaders[TOON_SHADER].isActivated())
		{
			shaderParams.put("mMVPMatrix", mMVPMatrix); shaderParams.put("lightDir", lightDir);
			shaderParams.put("vertex buffer", _vb);
			((ToonShader) _shaders[this.TOON_SHADER]).initShaderParams(shaderParams);
		}
		
		else if(_shaders[CUBEMAP_SHADER].isActivated())
		{
			shaderParams.put("mMVPMatrix", mMVPMatrix); shaderParams.put("vertex buffer", _vb);
			shaderParams.put("cubeMapText", cubeMapText);
			((CubeMapShader) _shaders[this.CUBEMAP_SHADER]).initShaderParams(shaderParams);
		}
		
		else if(_shaders[REFLECTION_SHADER].isActivated())
		{
			shaderParams.put("mMVPMatrix", mMVPMatrix); shaderParams.put("vertex buffer",_vb);
			shaderParams.put("reflectText",reflectText); shaderParams.put("mMVMatrix",mMVMatrix);
			shaderParams.put("normalMatrix",normalMatrix); 
			((ReflectionShader) _shaders[this.REFLECTION_SHADER]).initShaderParams(shaderParams);
		}
		
		else if(_shaders[TEXTURE_SHADER].isActivated())
		{
			shaderParams.put("mMVPMatrix", mMVPMatrix); shaderParams.put("simpleText", simpleText);
			shaderParams.put("vertex buffer", _vb);
			((TextureShader) _shaders[this.TEXTURE_SHADER]).initShaderParams(shaderParams);
			
		}
		else if(_shaders[RED_SHADER].isActivated())
		{
			shaderParams.put("mMVPMatrix", mMVPMatrix); shaderParams.put("vertex buffer", _vb);
			((RedShader) _shaders[this.RED_SHADER]).initShaderParams(shaderParams);	
		}
		else if(_shaders[FLAT_SHADER].isActivated())
		{
			shaderParams.put("mMVPMatrix", mMVPMatrix);shaderParams.put("vertex buffer", _vb);
			((FlatShader) _shaders[this.FLAT_SHADER]).initShaderParams(shaderParams);	
		}
		else if(_shaders[RANDCOLOR_SHADER].isActivated())
		{
			shaderParams.put("mMVPMatrix", mMVPMatrix);shaderParams.put("vertex buffer", _vb);
			((RandColorShader) _shaders[this.RANDCOLOR_SHADER]).initShaderParams(shaderParams);	
		}

		String openglExtensions =  GLES20.glGetString(GLES20.GL_EXTENSIONS);
				
		timer.startTime(openglExtensions);
		// Draw with indices
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, objects.get(_currentObject).getTotalNumberVertices());
		
		timer.stopTime();
				
		checkGlError("glDrawElements");
		
	}
	
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		float ratio = (float) width / height;
		Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 0.5f, 10);
	}

	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		
		// initialize shaders
		try {
			_shaders[GOURAUD_SHADER] = new GouraudShader(); 
			_shaders[PHONG_SHADER] = new PhongShader(); 
			_shaders[RED_SHADER] = new RedShader(); 
			_shaders[TOON_SHADER] = new ToonShader(); 
			_shaders[FLAT_SHADER] = new FlatShader(); 
			_shaders[TEXTURE_SHADER] = new TextureShader();
			_shaders[CUBEMAP_SHADER] = new CubeMapShader();
			_shaders[REFLECTION_SHADER] = new ReflectionShader();
			_shaders[RANDCOLOR_SHADER] = new RandColorShader();
			
			for(int i = 0; i < _shaders.length; i++)
			{
			_shaders[i].readShader(mContext);
			_shaders[i].getParamsLocations();
			}
			_shaders[_currentShader].setIsActivated(true);
		} catch (Exception e) {
			Log.d("SHADER 0 SETUP", e.getLocalizedMessage());
		}
		

		GLES20.glClearDepthf(1.0f);
		GLES20.glDepthFunc( GLES20.GL_LEQUAL );
		GLES20.glDepthMask( true );

		// cull backface
		GLES20.glEnable( GLES20.GL_CULL_FACE );
		GLES20.glCullFace(GLES20.GL_BACK); 
		GLES20.glFrontFace(GLES20.GL_CCW);
		
		//Create textures for shaders
				
		for(int i = 0; i< objects.size();i++)
		{
			objects.get(i).getTexture().createSimpleTexture();
			objects.get(i).getTexture().createCubeMapTexture("Cube Map");
			objects.get(i).getTexture().createCubeMapTexture("Reflect");
		}	

		// set the view matrix
		Matrix.setLookAtM(mVMatrix, 0, 0, 0, -5.0f, 0.0f, 0f, 0f, 0f, 1.0f, 0.0f);		
				
	}

	public void setShader(int shader) {
		_currentShader = shader;
	}

	public void setObject(int object) {
		_currentObject = object;
	}
	
	public Object3D getObject(){
		return objects.get(_currentObject);
	}
	
	public void setActivated(int shader, boolean isActivated){
		this._shaders[shader].setIsActivated(isActivated);
	}
	
	private void changeModelViewMatrix() {
		// scaling
		Matrix.setIdentityM(mScaleMatrix, 0);
		Matrix.scaleM(mScaleMatrix, 0, scaleX, scaleY, scaleZ);

		// Rotation along x
		Matrix.setRotateM(mRotXMatrix, 0, this.mAngleY, -1.0f, 0.0f, 0.0f);
		Matrix.setRotateM(mRotYMatrix, 0, this.mAngleX, 0.0f, 1.0f, 0.0f);


		// Set the ModelViewProjectionMatrix
		float tempMatrix[] = new float[16]; 
		Matrix.multiplyMM(tempMatrix, 0, mRotYMatrix, 0, mRotXMatrix, 0);		
		Matrix.multiplyMM(mMMatrix, 0, mScaleMatrix, 0, tempMatrix, 0);
		Matrix.multiplyMM(mMVMatrix, 0, mVMatrix, 0, mMMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVMatrix, 0);	
		
		
		// Create the normal modelview matrix
		// Invert + transpose of mvpmatrix
		Matrix.invertM(normalMatrix, 0, mMVPMatrix, 0);
		Matrix.transposeM(normalMatrix, 0, normalMatrix, 0);		
	}

	
	public void changeScale(float scale) {
		if (scaleX * scale > 1.4f)
			return;
		scaleX *= scale;scaleY *= scale;scaleZ *= scale;

	}
	
	public int getLastPolygon(){
		return this.objects.size() -1;
	}

	// debugging opengl
	private void checkGlError(String op) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(TAG, op + ": glError " + error);
			throw new RuntimeException(op + ": glError " + error);
		}
	}

} 
