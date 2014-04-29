package opengles.android;

import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Build;

@SuppressLint("InlinedApi") @TargetApi(Build.VERSION_CODES.FROYO) public class Texture {
	Bitmap[] cubeMap;
	Bitmap[] simpleTex;
	Bitmap[] reflect;
	Bitmap positiveX,negativeX,positiveY,negativeY,positiveZ,negativeZ, texture;
	int simpleTexId, cubeMapId, reflectId;
	
	
	public void createSimpleTexture(){
		// Texture object handle
        int[] textureId = new int[1];

        //  Generate a texture object
        GLES20.glGenTextures ( 1, textureId, 0 );
        
        // Bind the texture object
        GLES20.glBindTexture ( GLES20.GL_TEXTURE_2D, textureId[0] );

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);

        //  Load the texture
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, simpleTex[0], 0);
        texture.recycle();
        
        simpleTexId = textureId[0];
        
	}
	
	@TargetApi(Build.VERSION_CODES.FROYO) @SuppressLint("InlinedApi") public void createCubeMapTexture(String type)
    {

		int[] textureId = new int[1];		
		Bitmap[] chosenTex = new Bitmap[6];
		
		if(type.equals("Cube Map"))
		{
			chosenTex = cubeMap;
		}
		else
		{
			chosenTex = reflect;
		}
		
        // Generate a texture object
        GLES20.glGenTextures ( 1, textureId, 0 );

        // Bind the texture object
        GLES20.glBindTexture ( GLES20.GL_TEXTURE_CUBE_MAP, textureId[0] );
    
        // Load the cube face - Positive X
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, chosenTex[1], 0);

        // Load the cube face - Negative X
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, chosenTex[0], 0);
        
        // Load the cube face - Positive Y
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, chosenTex[3], 0);

        // Load the cube face - Negative Y
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, chosenTex[2], 0);

        // Load the cube face - Positive Z
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, chosenTex[5], 0);

        // Load the cube face - Negative Z
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, chosenTex[4], 0);
        

        // Set the filtering mode
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST );
        

        if(type.equals("Cube Map"))
		{
        	cubeMapId = textureId[0];
		}
		else
		{
			reflectId = textureId[0];
			positiveX.recycle();
			negativeX.recycle();
			positiveY.recycle();
			negativeY.recycle();
			positiveZ.recycle();
			negativeZ.recycle();
		}
        
    }

	 public void readSimpleTexture(int texFile, Context mContext) throws IOException{
	    	simpleTex = new Bitmap[1];
	    	InputStream is = mContext.getResources().openRawResource(texFile);
	    	texture = BitmapFactory.decodeStream(is); 
	    	simpleTex[0] = texture;
	    	is.close();
	    
	 }
	 
	 public void readCubeMapTexture(int[] files, Context mContext, String type) throws IOException{
			InputStream is = mContext.getResources().openRawResource(files[1]);
			positiveX = BitmapFactory.decodeStream(is);
			is = mContext.getResources().openRawResource(files[0]);
			negativeX = BitmapFactory.decodeStream(is);
			is = mContext.getResources().openRawResource(files[3]);
			positiveY = BitmapFactory.decodeStream(is);
			is = mContext.getResources().openRawResource(files[2]);
			negativeY = BitmapFactory.decodeStream(is);
			is = mContext.getResources().openRawResource(files[5]);
			positiveZ = BitmapFactory.decodeStream(is);
			is = mContext.getResources().openRawResource(files[4]);
			negativeZ = BitmapFactory.decodeStream(is);
			is.close();
			
			if(type.equals("Cube Map"))
			{
				cubeMap = new Bitmap[6];
				cubeMap[0] = negativeX;
				cubeMap[1] = positiveX;
				cubeMap[2] = negativeY;
				cubeMap[3] = positiveY;
				cubeMap[4] = negativeZ;
				cubeMap[5] = positiveZ;
			}
			else
			{
				reflect = new Bitmap[6];
				reflect[0] = negativeX;
				reflect[1] = positiveX;
				reflect[2] = negativeY;
				reflect[3] = positiveY;
				reflect[4] = negativeZ;
				reflect[5] = positiveZ;
			}
			
	    }
	 	 
	 public int getSimpleTexture()
	 {
		 return simpleTexId;
	 }
	 
	 public int getCubeMapTexture()
	 {
		 return cubeMapId;
	 }
	 
	 public int getReflectTexture()
	 {
		 return reflectId;
	 }
	 
}
