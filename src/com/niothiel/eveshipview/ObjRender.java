package com.niothiel.eveshipview;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.util.Log;

class ObjRender {
	private final String vertexShaderCode =
			"uniform mat4 uMVPMatrix;" + 
		    "attribute vec4 vPosition;" +
		    "attribute vec2 a_TexCoordinate;" + 
		    "varying vec2 v_TexCoordinate;" + 
		    "" +
		    "void main() {" +
		    "  gl_Position = uMVPMatrix * vPosition;" +
		    "  v_TexCoordinate = a_TexCoordinate;" +
		    "}";

	private final String fragmentShaderCode =
		    "precision mediump float;" +
		    "uniform sampler2D u_Texture;" + 
		    "varying vec2 v_TexCoordinate;" + 
		    "" +
		    "void main() {" +
		    "  gl_FragColor = texture2D(u_Texture, v_TexCoordinate);" +
		    "}";

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private ObjReader objReader;
    private TGAImageData imageData;
    
    private int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mTextureHandle;
    private int mMVPMatrixHandle;
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    public ObjRender(Context context, String objFile, String tgaFile) {
    	objReader = new ObjReader(context, objFile);
    	vertexBuffer = objReader.getVerticesBuffer();
    	textureBuffer = objReader.getTextureBuffer();
    	        
        // Load up the the vertex and fragment shaders.
        mProgram = GLHelper.loadProgram(vertexShaderCode, fragmentShaderCode);
        
        imageData = new TGAImageData();
        try {
        	// Open the input stream to the .tga file.
	        InputStream is = context.getResources().getAssets().open(tgaFile);
	        Bitmap texture = BitmapFactory.decodeStream(is);
	        
	        if(texture.getHeight() != texture.getWidth())
	        	Log.d("test", "Texture dimensions don't match!");
	        else
	        	Log.d("test", "Texture dimensions DO match!");
	        
	        /*
	        // Load the image using the TGA reader. 
	        ByteBuffer imageBuffer = imageData.loadImage(is);
	        
	        // Convert the buffer into a bitmap.
	        byte[] textureArray = imageBuffer.array();
	        Bitmap texture = BitmapFactory.decodeByteArray(textureArray, 0, textureArray.length);
	        */
	        Log.d("test", texture.getWidth() + " " + texture.getHeight());
	        
	        // Get a texture handle from opengl.
	        mTextureHandle = GLHelper.loadTexture(texture);
        }
        catch(Exception e){
        	throw new RuntimeException(e.getMessage() + "\n" + e.getStackTrace());
        }
    }
    
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // Grab uniform handles.
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        
        // Grab attrib handles.
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, objReader.floatsPerVertex,
                                     GLES20.GL_FLOAT, false,
                                     0, vertexBuffer);
        
        
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
     
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
     
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureHandle);
     
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        
        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        
        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, objReader.vertexIndicies.size() * 3);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);
    }
}
