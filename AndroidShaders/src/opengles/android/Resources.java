package opengles.android;

import android.graphics.Bitmap;

public class Resources {

	Object3D[] _objects;
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
	
	public void setObjects(Object3D[] _objects){
		this._objects = _objects;
	}
	
	public Object3D[] getObjects(){
		return this._objects;
	}
	
}
