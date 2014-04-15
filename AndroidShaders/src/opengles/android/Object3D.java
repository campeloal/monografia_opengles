package opengles.android;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.Context;
import android.util.Log;

public class Object3D {
	/*************************
	 * PROPERTIES
	 ************************/
	// Constants
	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int SHORT_SIZE_BYTES = 2;

	// Vertices
	private float _vertices[];
		
	// Indices
	private short _indices[];	
	
	// Buffers - index, vertex, normals and texcoords
	private FloatBuffer _vb;
	private FloatBuffer _nb;
	private ShortBuffer _ib;

	// Store the context
	Context activity; 
	
	ArrayList<Float> mainBuffer;
	ArrayList<Short> indicesB;
	// keep reading vertices
	ArrayList<Float> vs = new ArrayList<Float>(1000); // vertices
	ArrayList<Float> tc = new ArrayList<Float>(1000); // texture coords
	ArrayList<Float> ns = new ArrayList<Float>(1000); // normals
	ArrayList<Integer> vertIndex = new ArrayList<Integer>(1000);
	ArrayList<Integer> normalIndex = new ArrayList<Integer>(1000);
	ArrayList<Integer> texIndex = new ArrayList<Integer>(1000);
	int numVertices = 0;
	int numNormals = 0;
	int numTexCoords = 0;
	boolean hasTexture = false;
	short index = 0;
	int objID;
	
	public Object3D(int objID,Context activity){
		this.activity = activity;
		this.objID = objID;
		loadFile();
	}
	
	private int loadFile() {
		//Log.d("Start-loadFile", "Starting loadFile");
		try {
			// Read the file from the resource
			//Log.d("loadFile", "Trying to buffer read");
			InputStream inputStream = activity.getResources().openRawResource(objID);

			// setup Bufferedreader
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

			loadOBJ(in);
			
			// Generate your vertex, normal and index buffers
			// vertex buffer
			_vb = ByteBuffer.allocateDirect(_vertices.length
					* FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
			_vb.put(_vertices);
			_vb.position(0);
			

			// index buffer
			_ib = ByteBuffer.allocateDirect(_indices.length
					* SHORT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asShortBuffer();
			_ib.put(_indices);
			_ib.position(0);
			
			//Log.d("loadFile - size", _indices.length/3 + "," + _vertices.length);
			// close the reader
			in.close();
			return 1;
		} catch (Exception e) {
			//Log.d("Error-LoadFile", "FOUND ERROR: " + e.toString());
			return 0;
		}
	}
	
	private int loadOBJ(BufferedReader in) throws Exception {
		try {
			//Log.d("In OBJ:", "First");
			/* read vertices first */
			String str;
			mainBuffer = new ArrayList<Float>(numVertices * 6);
			indicesB = new ArrayList<Short>(numVertices * 3);
			
			while((str = in.readLine()) != null)
			{
				StringTokenizer t = new StringTokenizer(str);
				String type = t.nextToken();
				if (type.equals("v")) {
				
					vs.add(Float.parseFloat(t.nextToken())); 	// x
					vs.add(Float.parseFloat(t.nextToken()));	// y
					vs.add(Float.parseFloat(t.nextToken()));	// z
					numVertices++;
				}
				// read tex coords
				
				if (type.equals("vt")) 
				{						
					if(!hasTexture)
						hasTexture = true;
					tc.add(Float.parseFloat(t.nextToken())); 	// u
					tc.add(Float.parseFloat(t.nextToken()));	// v
					numTexCoords++;
				}
								
				// read vertex normals
				if (type.equals("vn")) 
				{
					ns.add(Float.parseFloat(t.nextToken())); 	// x
					ns.add(Float.parseFloat(t.nextToken()));	// y
					ns.add(Float.parseFloat(t.nextToken()));	// y
					numNormals++;
				}
				
				// now read all the faces
				
				if (type.equals("f")) 
				{
					if(hasTexture)
					{
						loadFaceTexture(t);
					}
					else
					{
						loadFaceNoTexture(t);
					}
				}
			}		
			
			for (int i =0; i<vertIndex.size();i++)
			{
				// Add all the vertex info
				mainBuffer.add(vs.get(vertIndex.get(i) * 3)); 	 // x
				mainBuffer.add(vs.get(vertIndex.get(i) * 3 + 1));// y
				mainBuffer.add(vs.get(vertIndex.get(i) * 3 + 2));// z
				
				// add the normal info
				mainBuffer.add(-ns.get(normalIndex.get(i) * 3)); 	  // x
				mainBuffer.add(-ns.get(normalIndex.get(i) * 3 + 1)); // y
				mainBuffer.add(-ns.get(normalIndex.get(i) * 3 + 2)); // z
				
				// add the tex coord info
				mainBuffer.add(tc.get(texIndex.get(i) * 2)); 	  // u
				mainBuffer.add(tc.get(texIndex.get(i) * 2 + 1)); // v
			}
			
			mainBuffer.trimToSize();
			
			_vertices = new float[mainBuffer.size()];
			
			// copy over the mainbuffer to the vertex + normal array
			for(int i = 0; i < mainBuffer.size(); i++)
			{
				_vertices[i] = mainBuffer.get(i);
			}
			
			Log.d("COMPLETED TRANSFER:", "VERTICES: " + _vertices.length);
		
			
			// copy over indices buffer
			indicesB.trimToSize();
			_indices = new short[indicesB.size()];
			for(int i = 0; i < indicesB.size(); i++) {
				_indices[i] = indicesB.get(i);
			}
			
			return 1;
			
		} catch(Exception e) {
			throw e;
		}
	}
	
	public void loadFaceTexture(StringTokenizer t){
		String fFace;
		StringTokenizer ft;
		for (int j = 0; j < 3; j++) 
		{
			fFace = t.nextToken();
			// another tokenizer - based on /
			ft = new StringTokenizer(fFace, "/");
			vertIndex.add(Integer.parseInt(ft.nextToken()) - 1); 
			texIndex.add(Integer.parseInt(ft.nextToken()) - 1);
			normalIndex.add(Integer.parseInt(ft.nextToken()) - 1);
			
			// Add to the index buffer
			indicesB.add(index++);
			
		}
				
	}
	
	public void loadFaceNoTexture(StringTokenizer t){
		String fFace;
		StringTokenizer ft;
		for (int j = 0; j < 3; j++) 
		{
			fFace = t.nextToken();
			// another tokenizer - based on /
			ft = new StringTokenizer(fFace, "//");
			vertIndex.add(Integer.parseInt(ft.nextToken()) - 1); 
			texIndex.add(0);
			normalIndex.add(Integer.parseInt(ft.nextToken()) - 1);
			
			// Add to the index buffer
			indicesB.add(index++);
								
		}
		
		if(tc.isEmpty())
		{
			tc.add((float) 1.0); 	// u
			tc.add((float) 1.0);	// v
		}
	}
	

	public float[] get_vertices() {
		return _vertices;
	}

	public void set_vertices(float[] _vertices) {
		this._vertices = _vertices;
	}
	public short[] get_indices() {
		return _indices;
	}

	public FloatBuffer get_vb() {
		return this._vb;
	}
	
	public FloatBuffer get_nb() {
		return this._nb;
	}
	
	public ShortBuffer get_ib() {
		return this._ib;
	}

}
