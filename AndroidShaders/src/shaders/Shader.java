package shaders;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

import android.annotation.TargetApi;
import android.content.Context;
import android.opengl.GLES20;
import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.FROYO) public abstract class Shader {

	// program/vertex/fragment handles
	protected int _program;
	private int _vertexShader;
	private int _pixelShader;
	boolean isActivated = false;
	int vID, fID;


	public abstract void getParamsLocations();
	public abstract void initShaderParams(@SuppressWarnings("rawtypes") Hashtable shaderParams);
	
	// Takes in ids for files to be read
	public void readShader(Context context) 
	{
			StringBuffer vs = new StringBuffer();
			StringBuffer fs = new StringBuffer();

			// read the files
			try {
				// Read the file from the resource
				//Log.d("loadFile", "Trying to read vs");
				// Read VS first
				InputStream inputStream = context.getResources().openRawResource(vID);
				// setup Bufferedreader
				BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

				String read = in.readLine();
				while (read != null) {
					vs.append(read + "\n");
					read = in.readLine();
				}

				vs.deleteCharAt(vs.length() - 1);

				// Now read FS
				inputStream = context.getResources().openRawResource(fID);
				// setup Bufferedreader
				in = new BufferedReader(new InputStreamReader(inputStream));

				read = in.readLine();
				while (read != null) {
					fs.append(read + "\n");
					read = in.readLine();
				}

				fs.deleteCharAt(fs.length() - 1);
			} catch (Exception e) {
				Log.d("ERROR-readingShader", "Could not read shader: " + e.getLocalizedMessage());
			}

			createProgram(vs.toString(), fs.toString());
	}

	public int createProgram(String vs, String fs) {
		// Vertex shader
		_vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vs);
		if (_vertexShader == 0) {
			return 0;
		}

		// pixel shader
		_pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fs);
		if (_pixelShader == 0) {
			return 0;
		}

		// Create the program
		_program = GLES20.glCreateProgram();
		if (_program != 0) {
			GLES20.glAttachShader(_program, _vertexShader);
			//checkGlError("glAttachShader VS " + this.toString());
			GLES20.glAttachShader(_program, _pixelShader);
			//checkGlError("glAttachShader PS");
			GLES20.glLinkProgram(_program);
			int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(_program, GLES20.GL_LINK_STATUS, linkStatus, 0);
			if (linkStatus[0] != GLES20.GL_TRUE) {
				Log.e("Shader", "Could not link _program: ");
				Log.e("Shader", GLES20.glGetProgramInfoLog(_program));
				GLES20.glDeleteProgram(_program);
				_program = 0;
				return 0;
			}
		}
		else
			Log.d("CreateProgram", "Could not create program");

		return 1;
	}

	public int loadShader(int shaderType, String source) {
		int shader = GLES20.glCreateShader(shaderType);
		if (shader != 0) {
			GLES20.glShaderSource(shader, source);
			GLES20.glCompileShader(shader);
			int[] compiled = new int[1];
			GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
			if (compiled[0] == 0) {
				Log.e("Shader", "Could not compile shader " + shaderType + ":");
				Log.e("Shader", GLES20.glGetShaderInfoLog(shader));
				GLES20.glDeleteShader(shader);
				shader = 0;
			}
		}
		return shader;
	}

	public void checkGlError(String op) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e("Shader", op + ": glError " + error);
			throw new RuntimeException(op + ": glError " + error);
		}
	}

	public int getProgram() {
		return _program;
	}
	
	public void setIsActivated(boolean isActivated){
		this.isActivated = isActivated;
	}
	
	public boolean isActivated(){
		return this.isActivated;
	}
}
