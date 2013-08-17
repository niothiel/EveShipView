package com.niothiel.eveshipview;

import android.opengl.GLES20;

public class GLHelper {
	public static int loadShader(int type, String shaderCode) {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
        
        if(shader == 0)
        	throw new RuntimeException("Unable to create shader.\n" + shaderCode);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
	
	public static int loadProgram(String vertexShaderCode, String fragmentShaderCode) {
		int vertexShaderHandle = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShaderHandle = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
		
		int program = GLES20.glCreateProgram();             	// create empty OpenGL ES Program
        GLES20.glAttachShader(program, vertexShaderHandle);		// add the vertex shader to program
        GLES20.glAttachShader(program, fragmentShaderHandle);	// add the fragment shader to program
        GLES20.glLinkProgram(program);                  		// creates OpenGL ES program executables
        
        return program;
	}
}
