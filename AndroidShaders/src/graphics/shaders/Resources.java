package graphics.shaders;

import android.graphics.Bitmap;

public class Resources {

	Object3D[] _objects;
	Bitmap reflectText[] = new Bitmap[6];
	Bitmap cubeMapText[] = new Bitmap[6];
	Bitmap simpleTexts[] = new Bitmap[5];
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
	
	public void setReflectText(Bitmap[] reflectText){
		this.reflectText = reflectText;
	}
	
	public void setCubeMapText(Bitmap[] cubeMapText){
		this.cubeMapText = cubeMapText;
	}
	
	public void setSimpleTexts(Bitmap[] simpleTexts){
		this.simpleTexts = simpleTexts;
	}
	
	public Bitmap[] getReflectText(){
		return reflectText;
	}
	
	public Bitmap[] getCubeMapText(){
		return cubeMapText;
	}
	
	public Bitmap[] getSimpleTexts(){
		return simpleTexts;
	}
}
