package opengles.android;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class Resources {

	ArrayList<Object3D> objects;
	Bitmap[] reflectText;
	Bitmap[] cubeMapText;
	Bitmap[] simpleTexts;
	private static Resources instance;
	
	private Resources(){
		
	}
	
	public static Resources getInstance() {
	      if (instance == null)
	         instance = new Resources();
	      return instance;
	   }
	
	public void setObjects(ArrayList<Object3D> objects){
		this.objects = objects;
	}
	
	public ArrayList<Object3D> getObjects(){
		return this.objects;
	}
	
}
