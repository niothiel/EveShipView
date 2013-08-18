package com.niothiel.eveshipview;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLUtils;
import android.util.Log;

public class ObjReader {
	public ArrayList<float[]> vertices = new ArrayList<float[]>();
	public ArrayList<float[]> textures = new ArrayList<float[]>();
	public ArrayList<float[]> normals = new ArrayList<float[]>();
	
	public ArrayList<int[]> vertexIndicies = new ArrayList<int[]>();
	public ArrayList<int[]> textureIndicies = new ArrayList<int[]>();
	public ArrayList<int[]> normalIndicies = new ArrayList<int[]>();
	
	public int floatsPerVertex;
	
	public ObjReader(Context context, String fileName) {
		try {
			InputStream is = context.getResources().getAssets().open(fileName);
	    	BufferedReader r = new BufferedReader(new InputStreamReader(is));
	    	
	    	for(String line = r.readLine(); line != null; line = r.readLine()) {
	    		if(line.length() == 0 || line.charAt(0) == '#')
	    			continue;
	    		
	    		String[] fields = line.split(" ");
	    		String command = fields[0];
	    		
	    		if(command.equals("v")) {
	    			float[] vertex = parseFloats(fields);
	    			floatsPerVertex = vertex.length;
	    			vertices.add(vertex);
	    		}
	    		else if(command.equals("vt"))
	    			textures.add(parseFloats(fields));
	    		else if(command.equals("vn"))
	    			normals.add(parseFloats(fields));
	    		else if(command.equals("f"))
	    			parseFace(fields);
	    	}
    	}
    	catch(Exception e) {
    		Log.e("asdf", "Can't open the requested file!");
    		return;
    	}
	}
	
	public FloatBuffer getVerticesBuffer() {
		// Create the float buffer, The number of floats that we have is: numVertexIndicies * 3 vertexes per index * n floats per vertex
		FloatBuffer buffer = GLHelper.createFloatBuffer(vertexIndicies.size() * 3 * floatsPerVertex);
		
		for(int[] indexes : vertexIndicies) {
			for(int index : indexes) {
				buffer.put(vertices.get(index - 1));
			}
		}
		
		buffer.position(0);
		return buffer;
	}
	
	public FloatBuffer getTextureBuffer() {
		FloatBuffer buffer = GLHelper.createFloatBuffer(textureIndicies.size() * 3 * 2);
		
		for(int[] indexes : textureIndicies) {
			for(int index : indexes) {
				buffer.put(textures.get(index - 1));
			}
		}
		
		buffer.position(0);
		return buffer;
	}
	
	private void parseFace(String[] fields) {
    	int[] vs = new int[fields.length - 1];
    	int[] vts = new int[fields.length - 1];
    	int[] vns = new int[fields.length - 1];
    	
    	for(int x = 1; x < fields.length; x++) {
    		String[] field = fields[x].split("/");
    		int v = Integer.parseInt(field[0]);
    		int vt = Integer.parseInt(field[1]);
    		int vn = Integer.parseInt(field[2]);
    		
    		vs[x - 1] = v;
    		vts[x - 1] = vt;
    		vns[x - 1] = vn;
    		
    	}
    	vertexIndicies.add(vs);
    	textureIndicies.add(vts);
    	normalIndicies.add(vns);
    }
	
	private float[] parseFloats(String[] fields) {
		float[] result = new float[fields.length - 1];
		
		for(int x = 1; x < fields.length; x++)
			result[x - 1] = Float.parseFloat(fields[x]);
		
		return result;
	}
}
