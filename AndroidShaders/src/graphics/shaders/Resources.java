package graphics.shaders;

public class Resources {

	Object3D[] _objects;
	int normalMapTextures;
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
