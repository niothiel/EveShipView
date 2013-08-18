package com.niothiel.eveshipview;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class GLHelper {
	public static int loadShader(int type, String shaderCode) {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
        
        // If for some reason we can't compile the shader
        if(shader == 0)
        	throw new RuntimeException("Unable to create shader.\n" + shaderCode);

        // Add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        
        // Check if we successfully compiled the shader. Error out if not.
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if(compiled[0] == GLES20.GL_FALSE)
        	throw new RuntimeException("Failed to compile shader.\n" + GLES20.glGetShaderInfoLog(shader));

        return shader;
    }
	
	public static int loadProgram(String vertexShaderCode, String fragmentShaderCode) {
		int vertexShaderHandle = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShaderHandle = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
		
		int program = GLES20.glCreateProgram();             	// create empty OpenGL ES Program
		if(program == 0)
			throw new RuntimeException("Unable to create program.");
		
        GLES20.glAttachShader(program, vertexShaderHandle);		// add the vertex shader to program
        checkGLError("glAttachShader");
        GLES20.glAttachShader(program, fragmentShaderHandle);	// add the fragment shader to program
        checkGLError("glAttachShader");
        GLES20.glLinkProgram(program);                  		// creates OpenGL ES program executables
        
        int[] linkStatus = new int[1];							// Check link status
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if(linkStatus[0] == GLES20.GL_FALSE)
        	throw new RuntimeException("Failed to link program\n" + GLES20.glGetProgramInfoLog(program));
        
        return program;
	}
	
	public static void checkGLError(String op) {
		int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            throw new RuntimeException(op + ": glError " + error);
        }
	}
	
	public static FloatBuffer createFloatBuffer(int numFloats) {
		ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of faces * 3 indexes per face * 3 vertices per index * 4 bytes per float)
                numFloats * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        
        return bb.asFloatBuffer();
	}
	
	public static int loadTexture(Bitmap bitmap) {
		
	    int[] textureHandle = new int[1];
	 
	    GLES20.glGenTextures(1, textureHandle, 0);
	 
	    if(textureHandle[0] == 0)
	    	throw new RuntimeException("Unable to create a texture handle.");

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   // No pre-scaling
 
        // Bind to the texture in OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
 
        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        
        //GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        //GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
 
        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
 
        // Recycle the bitmap, since its data has been loaded into OpenGL.
        bitmap.recycle();
	 
	    return textureHandle[0];
	}
}
