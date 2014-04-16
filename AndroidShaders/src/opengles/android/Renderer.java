package opengles.android;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import shaders.CubeMapShader;
import shaders.FlatShader;
import shaders.GouraudShader;
import shaders.PhongShader;
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
	/******************************
	 * PROPERTIES
	 ******************************/
	int[] texIDs;
	// rotation 
	public float mAngleX;
	public float mAngleY;

	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 8 * FLOAT_SIZE_BYTES;
	private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
	// shader constants
	private final int GOURAUD_SHADER = 0;
	private final int PHONG_SHADER = 1;
	private final int RED_SHADER = 2;
	private final int TOON_SHADER = 3;
	private final int FLAT_SHADER = 4;
	private final int CUBEMAP_SHADER = 5;
	private final int REFLECTION_SHADER = 6;
	private final int TEXTURE_SHADER = 7;

	// array of shaders
	Shader _shaders[] = new Shader[8];
	private int _currentShader;

	// The objects
	Object3D[] _objects;

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
	
	//Measuring the performance
	Timer timer;
	private static String TAG = "Renderer";

	/***************************
	 * CONSTRUCTOR(S)
	 **************************/
	public Renderer(Context context) {
		
		//this.loader_dialog = loader_dialog;
		mContext = context;
		
		Resources r = Resources.getInstance();
		this._objects = r.getObjects();
		//set current object and shader
		_currentShader = this.GOURAUD_SHADER;
		_currentObject = 0;
		timer = Timer.getInstance();
	}

	/*****************************
	 * GL FUNCTIONS
	 ****************************/
	/*
	 * Draw function - called for every frame
	 */
	@SuppressLint("NewApi") public void onDrawFrame(GL10 glUnused) {
		
		// Ignore the passed-in GL10 interface, and use the GLES20
		// class's static methods instead.
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

		GLES20.glUseProgram(0);

		// the current shader
		Shader shader = _shaders[this._currentShader]; // PROBLEM!
		int _program = shader.get_program();
		
		// Start using the shader
		GLES20.glUseProgram(_program);
		checkGlError("glUseProgram");

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
		
		// send to the shader
		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(_program, "uMVPMatrix"), 1, false, mMVPMatrix, 0);
		
		// Create the normal modelview matrix
		// Invert + transpose of mvpmatrix
		Matrix.invertM(normalMatrix, 0, mMVPMatrix, 0);
		Matrix.transposeM(normalMatrix, 0, normalMatrix, 0);
		
		/*** DRAWING OBJECT **/
		// Get buffers from mesh
		Object3D ob = this._objects[this._currentObject];
		FloatBuffer _vb = ob.get_vb();
		ShortBuffer _ib = ob.get_ib();
		short[] _indices = ob.get_indices();
		
		reflectText = _objects[_currentObject].getTexture().getReflectTexture();
		cubeMapText = _objects[_currentObject].getTexture().getCubeMapTexture();
		simpleText = _objects[_currentObject].getTexture().getSimpleTexture();
		
		// Vertex buffer

		// the vertex coordinates
		_vb.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(_program, "aPosition"), 3, GLES20.GL_FLOAT, false,
				TRIANGLE_VERTICES_DATA_STRIDE_BYTES, _vb);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(_program, "aPosition"));
		
		if(_shaders[this.GOURAUD_SHADER].isActivated())
		{
			
			((GouraudShader) _shaders[this.GOURAUD_SHADER]).initShaderParams(_program, _vb,mMVPMatrix, lightPos, lightColor, matAmbient, 
			matSpecular, matDiffuse, matShininess, eyePos);
		}
		
		else if(_shaders[this.PHONG_SHADER].isActivated())
		{
			((PhongShader) _shaders[this.PHONG_SHADER]).initShaderParams(_program, _vb,mMVPMatrix, lightPos, lightColor, matAmbient, 
					matSpecular, matDiffuse, matShininess, eyePos);
		}
		
		else if(_shaders[TOON_SHADER].isActivated())
		{
			((ToonShader) _shaders[this.TOON_SHADER]).initShaderParams(_program, _vb, lightDir);
		}
		
		else if(_shaders[CUBEMAP_SHADER].isActivated())
		{
			
			((CubeMapShader) _shaders[this.CUBEMAP_SHADER]).initShaderParams(_program, _vb, cubeMapText);
		}
		
		else if(_shaders[REFLECTION_SHADER].isActivated())
		{
			((ReflectionShader) _shaders[this.REFLECTION_SHADER]).initShaderParams(_program, _vb, reflectText, mMVMatrix, normalMatrix);
		}
		
		else if(_shaders[TEXTURE_SHADER].isActivated())
		{
			((TextureShader) _shaders[this.TEXTURE_SHADER]).initShaderParams(_program, _vb, simpleText);
			
		}

		String openglExtensions =  GLES20.glGetString(GLES20.GL_EXTENSIONS);
				
		timer.startTime(openglExtensions);
		// Draw with indices
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, _indices.length, GLES20.GL_UNSIGNED_SHORT, _ib);
		timer.stopTime();
				
		checkGlError("glDrawElements");
		
	}

	/*
	 * Called when viewport is changed
	 * @see android.opengl.GLSurfaceView$Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
	 */
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		float ratio = (float) width / height;
		Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 0.5f, 10);
	}

	/**
	 * Initialization function
	 */
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		
		// initialize shaders
		try {
			_shaders[GOURAUD_SHADER] = new GouraudShader(); 
			_shaders[GOURAUD_SHADER].readShader(mContext);
			_shaders[PHONG_SHADER] = new PhongShader();
			_shaders[PHONG_SHADER].readShader(mContext);
			_shaders[RED_SHADER] = new RedShader();
			_shaders[RED_SHADER].readShader(mContext);
			_shaders[TOON_SHADER] = new ToonShader();
			_shaders[TOON_SHADER].readShader(mContext);
			_shaders[FLAT_SHADER] = new FlatShader();
			_shaders[FLAT_SHADER].readShader(mContext);
			_shaders[CUBEMAP_SHADER] = new CubeMapShader();
			_shaders[CUBEMAP_SHADER].readShader(mContext);
			_shaders[REFLECTION_SHADER] = new ReflectionShader();
			_shaders[REFLECTION_SHADER].readShader(mContext);
			_shaders[TEXTURE_SHADER] = new TextureShader();
			_shaders[TEXTURE_SHADER].readShader(mContext);
			
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
		
		for(int i = 0; i< _objects.length;i++)
		{
			_objects[i].getTexture().createSimpleTexture();
			_objects[i].getTexture().createCubeMapTexture("Cube Map");
			_objects[i].getTexture().createCubeMapTexture("Reflect");
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
	
	public void setActivated(int shader, boolean isActivated){
		this._shaders[shader].setIsActivated(isActivated);
	}
	
		
	/**
	 * Scaling
	 */
	public void changeScale(float scale) {
		if (scaleX * scale > 1.4f)
			return;
		scaleX *= scale;scaleY *= scale;scaleZ *= scale;

		Log.d("SCALE: ", scaleX + "");
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

// END CLASS