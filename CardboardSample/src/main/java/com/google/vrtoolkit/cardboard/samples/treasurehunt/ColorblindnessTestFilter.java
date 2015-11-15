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
 * 1st param is byte[] i.e raw data from camera.
 * Created by Mayank on 11/14/2015.
 */
public class ColorblindnessTestFilter extends AsyncTask<byte[], Integer, byte[]> {
    private ImageView imageView;
    private ImageView imageViewRight;
    private Camera camera;
    private Context context;

    public ColorblindnessTestFilter(Context context, ImageView imageView, ImageView imageViewRight, Camera camera) {
        this.context = context;
        this.imageView = imageView;
        this.camera = camera;
        this.imageViewRight = imageViewRight;
    }

    @Override
    protected byte[] doInBackground(byte[]... params) {
        byte[] data = params[0];
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
 /*       Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
          ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;*/

/*
        int picw = previewSize.width;
        int pich = previewSize.height;
        int[] pix = new int[picw * pich];
        bm.getPixels(pix, 0, picw, 0, 0, picw, pich);

        int R, G, B,Y;

        for (int y = 0; y < pich; y++){
            for (int x = 0; x < picw; x++)
            {
                int index = y * picw + x;
                R = (pix[index] >> 16) & 0xff;
                G = (pix[index] >> 8) & 0xff;
                B = pix[index] & 0xff;
                Log.i("x:" + x + "y:" + y,"R :"+ R );
                //R,G.B - Red, Green, Blue
                //to restore the values after RGB modification, use
                //next statement
                pix[index] = 0xff000000 | (R << 16) | (G << 8) | B;
            }}
*/

        //Bitmap

        //RGB mods

        //Reconstruct to byte array

        //apply yuv image to that bytearray
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, previewSize.width, previewSize.height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, 480, 340), 80, baos);

        byte[] jdata = baos.toByteArray();
//        int sizeOfData = jdata.length;

        return jdata;
    }

    @Override
    protected void onPostExecute(byte[] data) {
        Bitmap bmSrc = BitmapFactory.decodeByteArray(data, 0, data.length);
        Bitmap bm = bmSrc.copy(Bitmap.Config.ARGB_8888,true);
        Bitmap bm2 = bmSrc.copy(Bitmap.Config.ARGB_8888,true);
        // Process pixels
        int pixel,R,G,B,A,r,g,b,a;
        for(int i=0;i<bm.getWidth();i++)
            for(int j=0;j<bm.getHeight();j++){
                pixel = bm.getPixel(i, j);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                A = Color.alpha(pixel);
                try {
                    r = (int)(0.5667*R + 0.43333*G);
                    g = (int)(0.55833*R + 0.44167*G);
                    b =(int)(0.24167*G+0.75833*B);
                    a = A;
                    int color = Color.argb(100, r, g, b);
                    bm2.setPixel(i, j, color);
                } catch(Exception ex){
                    ex.printStackTrace();
                }
            }
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //  imageView.setMinimumHeight(dm.heightPixels);
        //  imageView.setMinimumWidth(dm.widthPixels);
        imageView.setImageBitmap(bm2);
        imageViewRight.setImageBitmap(bm2);

    }

    private WindowManager getWindowManager() {
        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }
}