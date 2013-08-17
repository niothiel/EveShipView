package com.niothiel.eveshipview;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

class ObjRender {
	private final String vertexShaderCode =
			"uniform mat4 uMVPMatrix;" + 
		    "attribute vec4 vPosition;" +
		    "void main() {" +
		    "  gl_Position = uMVPMatrix * vPosition;" +
		    "}";

	private final String fragmentShaderCode =
		    "precision mediump float;" +
		    "uniform vec4 vColor;" +
		    "void main() {" +
		    "  gl_FragColor = vColor;" +
		    "}";
	

    private FloatBuffer vertexBuffer;
    private ArrayList<float[]> vertices;
    private ArrayList<int[]> faces;
    
    private int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = { // in counterclockwise order:
         0.0f,  0.622008459f, 0.0f,   // top
        -0.5f, -0.311004243f, 0.0f,   // bottom left
         0.5f, -0.311004243f, 0.0f    // bottom right
    };

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    public ObjRender(Context context, String fileName) {
    	vertices = new ArrayList<float[]>();
    	faces = new ArrayList<int[]>();
    	
    	try {
	    	final InputStream is = context.getResources().getAssets().open(fileName);
	    	BufferedReader r = new BufferedReader(new InputStreamReader(is));
	    	
	    	for(String line = r.readLine(); line != null; line = r.readLine()) {
	    		if(line.length() == 0 || line.charAt(0) == '#')
	    			continue;
	    		
	    		String[] fields = line.split(" ");
	    		String command = fields[0];
	    		
	    		if(command.equals("v")) {
	    			float[] vertex = {
	    					Float.parseFloat(fields[1]),
	    					Float.parseFloat(fields[2]),
	    					Float.parseFloat(fields[3])
	    			};
	    			vertices.add(vertex);
	    		}
	    		else if(command.equals("f")) {
	    			parseFace(fields);
	    		}
	    	}
    	}
    	catch(Exception e) {
    		Log.e("asdf", "Can't open the requested file!");
    		return;
    	}
    	
    	Log.d("test", "Number of faces: " + faces.size());
    	
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of faces * 3 indexes per face * 3 vertices per index * 4 bytes per float)
                faces.size() * 3 * 3 * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        for(int[] indexes : faces) {
        	for(int index : indexes) {
        		vertexBuffer.put(vertices.get(index - 1));
        	}
        }
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
        
        // Load up the the vertex and fragment shaders.
        mProgram = GLHelper.loadProgram(vertexShaderCode, fragmentShaderCode);
    }
    
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT, false,
                                     0, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        
        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, faces.size() * 3);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
    
    private void parseFace(String[] fields) {
    	int[] vs = new int[fields.length - 1];
    	
    	for(int x = 1; x < fields.length; x++) {
    		String[] field = fields[x].split("/");
    		int v = Integer.parseInt(field[0]);
    		int vt = Integer.parseInt(field[1]);
    		int vn = Integer.parseInt(field[2]);
    		
    		vs[x - 1] = v;
    	}
    	faces.add(vs);
    }
}
