package com.niothiel.eveshipview;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

public class MyRenderer implements GLSurfaceView.Renderer {
	Triangle mTriangle;
	ObjRender mObj;
	Context mContext;
	
	float[] mProjMatrix = new float[16];
	float[] mVMatrix = new float[16];
	float[] mMVPMatrix = new float[16];
	float[] mRotationMatrix = new float[16];

	public MyRenderer(Context context) 
	{
		mContext = context;
	}
	
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        
        mTriangle = new Triangle();
        mObj = new ObjRender(mContext, "ab3_t1.obj");
    }

    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, -1000, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Create a rotation transformation for the triangle
        Matrix.setIdentityM(mRotationMatrix, 0);
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, -1.0f);
        
        // Combine the rotation matrix with the projection and camera view
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mRotationMatrix, 0);
        
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        
        //mTriangle.draw(mMVPMatrix);
        mObj.draw(mMVPMatrix);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        
        float ratio = (float) width / height;
        // this projection matrix is applied to object coordinates
    	// in the onDrawFrame() method
    	Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 1000);
    }
}