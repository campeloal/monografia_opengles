package graphics.shaders;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

class Renderer implements GLSurfaceView.Renderer {
	/******************************
	 * PROPERTIES
	 ******************************/
	// rotation 
	public float mAngleX;
	public float mAngleY;

	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 8 * FLOAT_SIZE_BYTES;
	private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
	private static final int TRIANGLE_VERTICES_DATA_NOR_OFFSET = 3;
	private static final int TRIANGLE_VERTICES_DATA_TEX_OFFSET = 6;

	// shader constants
	private final int GOURAUD_SHADER = 0;
	private final int PHONG_SHADER = 1;
	private final int RED_SHADER = 2;
	private final int TOON_SHADER = 3;
	private final int FLAT_SHADER = 4;
	private final int CUBEMAP_SHADER = 5;

	// array of shaders
	Shader _shaders[] = new Shader[6];
	private int _currentShader;

	// The objects
	Object3D[] _objects = new Object3D[4];

	// current object
	private int _currentObject;

	// Modelview/Projection matrices
	private float[] mMVPMatrix = new float[16];
	private float[] mProjMatrix = new float[16];
	private float[] mScaleMatrix = new float[16];   // scaling
	private float[] mRotXMatrix = new float[16];	// rotation x
	private float[] mRotYMatrix = new float[16];	// rotation x
	private float[] mMMatrix = new float[16];		// rotation
	private float[] mVMatrix = new float[16]; 		// modelview
	private float[] normalMatrix = new float[16]; 	// modelview normal
	private float[] invView = new float[16];

	// textures enabled?
	private boolean enableTexture = true;
	private int[] _texIDs;
	
	//which shader is enabled?
	private boolean gouraudShader = false;
	private boolean phongShader = false;
	private boolean normalMapShader = false;
	private boolean redShader = false;
	private boolean toonShader = false;
	private boolean cubeMapShader = false;

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
	private float[] nEye = {1.0f, 0.0f, 0.0f};

	// scaling
	float scaleX = 1.0f;
	float scaleY = 1.0f;
	float scaleZ = 1.0f;

	Dialog loader_dialog;

	private int mTextureId;

	private Context mContext;
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
		gouraudShader = true;
		_currentObject = 0;
		
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
		Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);	
		
		// send to the shader
		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(_program, "uMVPMatrix"), 1, false, mMVPMatrix, 0);
		
		// Create the normal modelview matrix
		// Invert + transpose of mvpmatrix
		Matrix.invertM(normalMatrix, 0, mMVPMatrix, 0);
		Matrix.transposeM(normalMatrix, 0, normalMatrix, 0);
		
		/*** DRAWING OBJECT **/
		// Get buffers from mesh
		Object3D ob = this._objects[this._currentObject];
		Mesh mesh = ob.getMesh();
		FloatBuffer _vb = mesh.get_vb();
		ShortBuffer _ib = mesh.get_ib();
		short[] _indices = mesh.get_indices();
		
		// Vertex buffer

		// the vertex coordinates
		_vb.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(_program, "aPosition"), 3, GLES20.GL_FLOAT, false,
				TRIANGLE_VERTICES_DATA_STRIDE_BYTES, _vb);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(_program, "aPosition"));
		
		if(gouraudShader || phongShader || normalMapShader)
		{
			// send to the shader
			GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(_program, "normalMatrix"), 1, false, mMVPMatrix, 0);
			
			// lighting variables
			// send to shaders
			GLES20.glUniform4fv(GLES20.glGetUniformLocation(_program, "lightPos"), 1, lightPos, 0);
			GLES20.glUniform4fv(GLES20.glGetUniformLocation(_program, "lightColor"), 1, lightColor, 0);
			
			
			// material 
			GLES20.glUniform4fv(GLES20.glGetUniformLocation(_program, "matAmbient"), 1, matAmbient, 0);
			GLES20.glUniform4fv(GLES20.glGetUniformLocation(_program, "matDiffuse"), 1, matDiffuse, 0);
			GLES20.glUniform4fv(GLES20.glGetUniformLocation(_program, "matSpecular"), 1, matSpecular, 0);
			GLES20.glUniform1f(GLES20.glGetUniformLocation(_program, "matShininess"), matShininess);
			
			// eye position
			GLES20.glUniform3fv(GLES20.glGetUniformLocation(_program, "eyePos")/*shader.eyeHandle*/, 1, eyePos, 0);
			// enable texturing? [fix - sending float is waste]
			GLES20.glUniform1f(GLES20.glGetUniformLocation(_program, "hasTexture")/*shader.hasTextureHandle*/, ob.hasTexture() && enableTexture ? 2.0f : 0.0f);
			// the normal info
			_vb.position(TRIANGLE_VERTICES_DATA_NOR_OFFSET);
			GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(_program, "aNormal"), 3, GLES20.GL_FLOAT, false,
					TRIANGLE_VERTICES_DATA_STRIDE_BYTES, _vb);
			GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(_program, "aNormal"));
		}
		
		if(toonShader)
		{
			int loc = GLES20.glGetUniformLocation(_program, "lightDir");
			GLES20.glUniform3fv(loc, 1, lightDir,0);
			// the normal info
			_vb.position(TRIANGLE_VERTICES_DATA_NOR_OFFSET);
			GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(_program, "aNormal"), 3, GLES20.GL_FLOAT, false,
					TRIANGLE_VERTICES_DATA_STRIDE_BYTES, _vb);
			GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(_program, "aNormal"));
		}
		
		if(cubeMapShader)
		{
			GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(_program, "aNormal"), 3, GLES20.GL_FLOAT, false,
					TRIANGLE_VERTICES_DATA_STRIDE_BYTES, _vb);
			GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(_program, "aNormal"));
			// Bind the texture
	        GLES20.glActiveTexture ( GLES20.GL_TEXTURE0 );
	        GLES20.glBindTexture ( GLES20.GL_TEXTURE_CUBE_MAP, mTextureId );
			GLES20.glUniform1i(GLES20.glGetUniformLocation(_program, "s_texture"), 0);
			
		}

		// Draw with indices
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, _indices.length, GLES20.GL_UNSIGNED_SHORT, _ib);
		
		
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
			_shaders[GOURAUD_SHADER] = new Shader(R.raw.gouraud_vs,R.raw.gouraud_ps, mContext, false, 0); // gouraud
			_shaders[PHONG_SHADER] = new Shader(R.raw.phong_vs, R.raw.phong_ps, mContext, false, 0); // phong
			_shaders[RED_SHADER] = new Shader(R.raw.red_vs, R.raw.red_ps, mContext, false, 0);
			_shaders[TOON_SHADER] = new Shader(R.raw.toon_vs, R.raw.toon_ps, mContext, false, 0);
			_shaders[FLAT_SHADER] = new Shader(R.raw.flat_vs, R.raw.flat_ps, mContext, false, 0);
			_shaders[CUBEMAP_SHADER] = new Shader(R.raw.cubemap_vs, R.raw.cubmap_ps, mContext, false, 0);
		} catch (Exception e) {
			Log.d("SHADER 0 SETUP", e.getLocalizedMessage());
		}

		GLES20.glClearDepthf(1.0f);
		GLES20.glDepthFunc( GLES20.GL_LEQUAL );
		GLES20.glDepthMask( true );

		// cull backface
		GLES20.glEnable( GLES20.GL_CULL_FACE );
		GLES20.glCullFace(GLES20.GL_BACK); 

        try {
			mTextureId = createSimpleTextureCubemap ();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public void enableRedShader(boolean enable){
		this.redShader = enable;
	}
	
	public void enableGouraudShader(boolean enable){
		this.gouraudShader = enable;
	}
	
	public void enablePhongShader(boolean enable){
		this.phongShader = enable;
	}
	
	public void enableToonShader(boolean enable){
		this.toonShader = enable;
	}
	
	public void enableCubeMapShader(boolean enable){
		this.cubeMapShader = enable;
	}
	
	private int createSimpleTextureCubemap( ) throws IOException
    {
		
		
		Bitmap positiveX,negativeX,positiveY,negativeY,positiveZ,negativeZ;
		InputStream is = mContext.getResources().openRawResource(R.raw.positive_x);
		positiveX = BitmapFactory.decodeStream(is);
		is = mContext.getResources().openRawResource(R.raw.negative_x);
		negativeX = BitmapFactory.decodeStream(is);
		is = mContext.getResources().openRawResource(R.raw.positive_y);
		positiveY = BitmapFactory.decodeStream(is);
		is = mContext.getResources().openRawResource(R.raw.negative_y);
		negativeY = BitmapFactory.decodeStream(is);
		is = mContext.getResources().openRawResource(R.raw.positive_z);
		positiveZ = BitmapFactory.decodeStream(is);
		is = mContext.getResources().openRawResource(R.raw.negative_z);
		negativeZ = BitmapFactory.decodeStream(is);
		is.close();

		int[] textureId = new int[1];
                    
        // Generate a texture object
        GLES20.glGenTextures ( 1, textureId, 0 );

        // Bind the texture object
        GLES20.glBindTexture ( GLES20.GL_TEXTURE_CUBE_MAP, textureId[0] );
    
        // Load the cube face - Positive X
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, positiveX, 0);

        // Load the cube face - Negative X
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, negativeX, 0);
        // Load the cube face - Positive Y
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, positiveY, 0);

        // Load the cube face - Negative Y
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, negativeY, 0);

        // Load the cube face - Positive Z
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, positiveZ, 0);

        // Load the cube face - Negative Z
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, negativeZ, 0);

        // Set the filtering mode
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST );

        return textureId[0];
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