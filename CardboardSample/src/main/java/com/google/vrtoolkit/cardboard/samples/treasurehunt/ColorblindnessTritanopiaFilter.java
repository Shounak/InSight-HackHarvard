package com.google.vrtoolkit.cardboard.samples.treasurehunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * Created by Mayank on 11/15/2015.
 */
public class ColorblindnessTritanopiaFilter extends AsyncTask<byte[], Integer, byte[]> {
    private ImageView imageView;
    private ImageView imageViewRight;
    private Camera camera;
    private Context context;


    public ColorblindnessTritanopiaFilter(Context context, ImageView imageView, ImageView imageViewRight, Camera camera) {

        this.context = context;
        this.imageView = imageView;
        this.camera = camera;
        this.imageViewRight = imageViewRight;
    }
    @Override
    protected byte[] doInBackground(byte[]... params) {
        byte[] data = params[0];
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, previewSize.width, previewSize.height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, 480, 340), 80, baos);
        byte[] jdata = baos.toByteArray();
        return jdata;
    }

    @Override
    protected void onPostExecute(byte[] data) {
        Bitmap bmSrc = BitmapFactory.decodeByteArray(data, 0, data.length);
        Bitmap bm = bmSrc.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bm2 = bmSrc.copy(Bitmap.Config.ARGB_8888, true);
        // Process pixels
        int pixel, R, G, B, A, r, g, b, a;
        for (int i = 0; i < bm.getWidth(); i++)
            for (int j = 0; j < bm.getHeight(); j++) {
                pixel = bm.getPixel(i, j);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                A = Color.alpha(pixel);
                try {
                    r = (int) (0.95*R + 0.05*G);
                    g = (int) (0.43333*G + 0.56667*B);
                    b = (int) (0.475*G+0.525*B);
                    a = A;
                    int color = Color.argb(100, r, g, b);
                    bm2.setPixel(i, j, color);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        imageView.setImageBitmap(bm2);
        imageViewRight.setImageBitmap(bm2);

    }

    private WindowManager getWindowManager() {
        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }
}
