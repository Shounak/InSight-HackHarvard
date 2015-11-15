package com.google.vrtoolkit.cardboard.samples.treasurehunt;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera;
import android.widget.ImageView;


/**
 * Created by Mayank on 11/14/2015.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private int test = 0;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private ImageView mFilteredImageView;
    private ImageView mFilteredImageViewRight;
    private static String TAG = CameraPreview.class.getName();
    public CameraPreview(Context context, Camera camera, ImageView filteredImageView, ImageView filteredImageViewRight) {
        super(context);
        mCamera = camera;
        mFilteredImageView = filteredImageView;
        mFilteredImageViewRight = filteredImageViewRight;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.

        try {
          //  holder.setFormat(PixelFormat.TRANSPARENT);
            mCamera.setPreviewDisplay(holder);
            //TODO @see associated with previewCallback
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the com.google.vrtoolkit.cardboard.samples.treasurehunt.CameraActivity preview in your activity.
        try {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
            e.printStackTrace();
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings

        try {
            mCamera.setPreviewDisplay(mHolder);
            //TODO if view is not fixed @see https://stackoverflow.com/questions/14412618/android-onpreviewframe-is-not-called
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        // System.arraycopy(data, 0, mData, 0, data.length);

        int size = data.length;
        Camera.Size previewSize = camera.getParameters().getPreviewSize();

        ColorblindnessTestFilter colorblindnessTestFilter = new ColorblindnessTestFilter(getContext(),mFilteredImageView,mFilteredImageViewRight, camera);
        colorblindnessTestFilter.execute(data);
        /*YuvImage yuvimage=new YuvImage(data, ImageFormat.NV21, previewSize.width, previewSize.height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 80, baos);
        //
        // get JPEG
        yuvimage.compressToJpeg(new Rect(0, 0, 128, 96), 80, baos);
        byte[] jdata = baos.toByteArray();
        int sizeOfData = jdata.length;*/


    }

}
