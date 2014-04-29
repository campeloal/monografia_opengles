package opengles.android;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.Context;
import android.util.Log;

public class Object3D {

	// Constants
	private static final int FLOAT_SIZE_BYTES = 4;

	// Vertices
	private float _vertices[];
			
	// Buffers
	private FloatBuffer vertexBuffer;

	// Store the context
	Context activity; 
	
	Texture texture;
	
	// keep reading vertices
	ArrayList<Float> vs = new ArrayList<Float>(10000); // vertices
	ArrayList<Float> tc = new ArrayList<Float>(10000); // texture coords
	ArrayList<Float> ns = new ArrayList<Float>(10000); // normals
	ArrayList<Integer> vertIndex = new ArrayList<Integer>(10000);
	ArrayList<Integer> normalIndex = new ArrayList<Integer>(10000);
	ArrayList<Integer> texIndex = new ArrayList<Integer>(10000);
	
	boolean hasTexture = false;
	short index = 0;
	int objID;
	
	public Object3D(int objID,Context activity){
		this.activity = activity;
		this.objID = objID;
		texture = new Texture();
		loadFile();
	}
	
	private int loadFile() {
		try {
			// Read the file from the resource
			InputStream inputStream = activity.getResources().openRawResource(objID);

			// setup Bufferedreader
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

			loadOBJ(in);			
			readVerticesBasedIndex();			
			createBuffer();
			
			in.close();
			return 1;
		} catch (Exception e) {
			return 0;
		}
	}
		
	private int loadOBJ(BufferedReader in) throws Exception {
		try {
			String str;
			
			while((str = in.readLine()) != null)
			{
				StringTokenizer t = new StringTokenizer(str);
				String type = t.nextToken();
				if (type.equals("v")) {
				
					vs.add(Float.parseFloat(t.nextToken())); 	// x
					vs.add(Float.parseFloat(t.nextToken()));	// y
					vs.add(Float.parseFloat(t.nextToken()));	// z
				}
				// read tex coords
				
				if (type.equals("vt")) 
				{						
					if(!hasTexture)
						hasTexture = true;
					tc.add(Float.parseFloat(t.nextToken())); 	// u
					tc.add(Float.parseFloat(t.nextToken()));	// v
				}
								
				// read vertex normals
				if (type.equals("vn")) 
				{
					ns.add(Float.parseFloat(t.nextToken())); 	// x
					ns.add(Float.parseFloat(t.nextToken()));	// y
					ns.add(Float.parseFloat(t.nextToken()));	// y
				}
				
				// now read all the faces
				
				if (type.equals("f")) 
				{
					if(hasTexture)
					{
						loadFace(t,"/");
					}
					else
					{
						loadFace(t, "//");
					}
				}
			}
			
			vs.trimToSize();
			ns.trimToSize();
			tc.trimToSize();
			vertIndex.trimToSize();
			normalIndex.trimToSize();
			texIndex.trimToSize();
			
			return 1;
			
		} catch(Exception e) {
			throw e;
		}
	}
	
	public void loadFace(StringTokenizer t, String symbol){
		String fFace;
		StringTokenizer ft;
		for (int j = 0; j < 3; j++) 
		{
			fFace = t.nextToken();
			// another tokenizer - based on /
			ft = new StringTokenizer(fFace, symbol);
			
			vertIndex.add(Integer.parseInt(ft.nextToken()) - 1); 
			if(symbol.equals("//"))
			{
				texIndex.add(0);
			}
			else
			{
				texIndex.add(Integer.parseInt(ft.nextToken()) - 1);
			}
			normalIndex.add(Integer.parseInt(ft.nextToken()) - 1);
						
		}
		
		//If there's no texture add random value
		if(tc.isEmpty())
		{
			tc.add((float) 1.0); 	// u
			tc.add((float) 1.0);	// v
		}
				
	}
	
	private void readVerticesBasedIndex(){
		
		int totalNumberVertices = vertIndex.size();
		//vertex + normals + textures number of coordinates
		int numberOfElements = 3 + 3 + 2;
		_vertices = new float[totalNumberVertices*numberOfElements];
		
		for (int i =0; i< totalNumberVertices;i++)
		{
			// Add all the vertex info
			_vertices[i*numberOfElements] = vs.get(vertIndex.get(i) * 3);
			_vertices[i*numberOfElements + 1] = vs.get(vertIndex.get(i) * 3 + 1);
			_vertices[i*numberOfElements + 2] = vs.get(vertIndex.get(i) * 3 + 2);
			
			// add the normal info
			_vertices[i*numberOfElements + 3] = -ns.get(normalIndex.get(i) * 3);
			_vertices[i*numberOfElements + 4] = -ns.get(normalIndex.get(i) * 3 + 1);
			_vertices[i*numberOfElements + 5] = -ns.get(normalIndex.get(i) * 3 + 2);
							
			// add the tex coord info
			_vertices[i*numberOfElements + 6] = tc.get(texIndex.get(i) * 2);
			_vertices[i*numberOfElements + 7] = tc.get(texIndex.get(i) * 2 + 1);				
		}
								
		
		Log.d("COMPLETED TRANSFER:", "VERTICES: " + vs.size() + "INDEXES: " + vertIndex.size() + "TRIANGLES " + 
				vertIndex.size()/3);
		
	}
	
	private void createBuffer()
	{
		// Generate your vertex, normal and index buffers
		// vertex buffer
		vertexBuffer = ByteBuffer.allocateDirect(_vertices.length
				* FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
		vertexBuffer.put(_vertices);
		vertexBuffer.position(0);
		
	}
	
	public Texture getTexture(){
		return texture;
	}
	

	public int getTotalNumberVertices() {
		return vertIndex.size();
	}
	
	public int getNumberPolygons(){
		return vertIndex.size()/3;
	}

	public FloatBuffer getVertexBuffer() {
		return this.vertexBuffer;
	}
		

}
