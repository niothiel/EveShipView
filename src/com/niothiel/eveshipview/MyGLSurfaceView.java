package com.niothiel.eveshipview;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {
	private MyRenderer mRenderer;
	private float mPreviousX;
	private float mPreviousY;
	
	private final float TOUCH_SCALE_FACTOR = 180.0f / 800;
	
	public MyGLSurfaceView(Context context) {
		super(context);
		mRenderer = new MyRenderer(context);
		
		setEGLContextClientVersion(2);
		setRenderer(mRenderer);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
	    // MotionEvent reports input details from the touch screen
	    // and other input controls. In this case, you are only
	    // interested in events where the touch position changed.

	    float x = e.getX();
	    float y = e.getY();

	    switch (e.getAction()) {
	        case MotionEvent.ACTION_MOVE:

	            float dx = x - mPreviousX;
	            float dy = y - mPreviousY;

	            // reverse direction of rotation above the mid-line
	            if (y > getHeight() / 2) {
	              dx = dx * -1 ;
	            }

	            // reverse direction of rotation to left of the mid-line
	            if (x < getWidth() / 2) {
	              dy = dy * -1 ;
	            }

	            mRenderer.mAngleYaw += dx * TOUCH_SCALE_FACTOR;  // = 180.0f / 320
	            mRenderer.mAnglePitch += dy * TOUCH_SCALE_FACTOR;
	            requestRender();
	    }

	    mPreviousX = x;
	    mPreviousY = y;
	    return true;
	}
}
