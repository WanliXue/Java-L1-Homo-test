package com.example.facedetectionexample;
 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;



public class FaceDetectionExample extends Activity {
    private static final int TAKE_PICTURE_CODE = 100;
    private static final int MAX_FACES = 5;
    
     
    private Bitmap cameraBitmap = null;
    private boolean isReconigzeFace = true;
    
    private WeakReference<Context> contextReference;
    private static L1Classifier activityClassifier = L1Classifier.getInstance();

     
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
    //Context context = getApplicationContext();
    contextReference = new WeakReference<Context>(getApplicationContext());
     
    ((Button)findViewById(R.id.take_picture)).setOnClickListener(btnClick);
    
    activityClassifier.init(contextReference);
}
 
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
     
            if(TAKE_PICTURE_CODE == requestCode){
                    processCameraImage(data);
            }
    }
 
private void openCamera(){
    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
    
    startActivityForResult(intent, TAKE_PICTURE_CODE);
}
 
private void processCameraImage(Intent intent){
    setContentView(R.layout.detectlayout);
     
    ((Button)findViewById(R.id.detect_face)).setOnClickListener(btnClick);
     
    ImageView imageView = (ImageView)findViewById(R.id.image_view);
     
    cameraBitmap = (Bitmap)intent.getExtras().get("data");
    imageView.setImageBitmap(cameraBitmap);
}


private Bitmap toGrayscale(Bitmap bmpOriginal)
{        
    int width, height;
    height = bmpOriginal.getHeight();
    width = bmpOriginal.getWidth();    

    Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
    Canvas c = new Canvas(bmpGrayscale);
    Paint paint = new Paint();
    ColorMatrix cm = new ColorMatrix();
    cm.setSaturation(0);
    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
    paint.setColorFilter(f);
    c.drawBitmap(bmpOriginal, 0, 0, paint);
    
    bmpOriginal.recycle();
    
    return bmpGrayscale;
}



private int reconigzeFace(Bitmap bmp) {
	int width = bmp.getWidth();
	int height = bmp.getHeight();
	
	int[] pixels = new int[width * height];
	
	bmp.getPixels(pixels, 0, width, 0, 0, width, height);
	
	for(int i = 0; i < width * height; i++) {
        int R = (pixels[i] >> 16) & 0xff;     //bitwise shifting
        int G = (pixels[i] >> 8) & 0xff;
        int B = pixels[i] & 0xff;
        
        pixels[ i]  = (R + G + B)/3; 
	}
	
	
	int result = activityClassifier.doClassifyActivity(contextReference, pixels);
	//int result = activityClassifier.doClassifyActivity(contextReference, tt);
	//Log.i("FaceDetector", "classification result: " + result);
	
	
	return result;
}

 
private void detectFaces(){
	int classifition_width = 56, classifition_height = 92;
	
    if(null != cameraBitmap){
            int width = cameraBitmap.getWidth();
            int height = cameraBitmap.getHeight();
             
            FaceDetector detector = new FaceDetector(width, height,FaceDetectionExample.MAX_FACES);
            Face[] faces = new Face[FaceDetectionExample.MAX_FACES];
             
            Bitmap bitmap565 = Bitmap.createBitmap(width, height, Config.RGB_565);
            Paint ditherPaint = new Paint();
            Paint drawPaint = new Paint();
             
            ditherPaint.setDither(true);
            drawPaint.setColor(Color.RED);
            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setStrokeWidth(2);
             
            Canvas canvas = new Canvas();
            canvas.setBitmap(bitmap565);
            canvas.drawBitmap(cameraBitmap, 0, 0, ditherPaint);
             
            int facesFound = detector.findFaces(bitmap565, faces);
            PointF midPoint = new PointF();
            float eyeDistance = 0.0f;
            float confidence = 0.0f;
             
            Log.i("FaceDetector", "Number of faces found: " + facesFound);
             
            if(facesFound > 0) {
                    for(int index=0; index<facesFound; ++index){
                            faces[index].getMidPoint(midPoint);
                            eyeDistance = faces[index].eyesDistance();
                            confidence = faces[index].confidence();
                             
                            Log.i("FaceDetector", 
                                            "Confidence: " + confidence + 
                                            ", Eye distance: " + eyeDistance + 
                                            ", Mid Point: (" + midPoint.x + ", " + midPoint.y + ")");
                             
                            canvas.drawRect((int)midPoint.x - eyeDistance , 
                                                            (int) (midPoint.y - eyeDistance * 1.6) , 
                                                            (int)midPoint.x + eyeDistance, 
                                                            (int) (midPoint.y + eyeDistance * 1.6), drawPaint);
                    
                            String filepath = Environment.getExternalStorageDirectory() + "/DCIM/face/a" 
                            		+  System.currentTimeMillis() +  ".png";
                            
                       	
	                        Bitmap croppedBmp = Bitmap.createBitmap(bitmap565, 
	                            		(int) (midPoint.x - eyeDistance + 2),
	                            		(int) (midPoint.y - eyeDistance * 1.6 + 2), 
	                                    (int) ( 2 *  eyeDistance - 4 ), 
	                                    (int) (2 *  eyeDistance * 1.6 - 4));
	                        
                            
                            if((croppedBmp.getWidth() > 10) && (croppedBmp.getHeight() > 10)) { 
                            	 try {   
	                            	FileOutputStream fos = new FileOutputStream(filepath);
	                            	
		                            croppedBmp  = Bitmap.createScaledBitmap(croppedBmp, 
		                            		classifition_width, classifition_height, true);
		                            croppedBmp = toGrayscale(croppedBmp);
	                            	
		                            if(isReconigzeFace) {
		                            	int id = reconigzeFace(croppedBmp);
		                            	TextView tv1 = (TextView) findViewById(R.id.classificatoin_result);
		                            	tv1.setText(tv1.getText().toString()  + " " + id);
	                            	}
	                            
	                            	croppedBmp.compress(CompressFormat.PNG, 100, fos);
		                            
		                            fos.flush();
		                            fos.close();
	                            } catch (FileNotFoundException e) {
	                                e.printStackTrace();
	                            } catch (IOException e) {
	                                    e.printStackTrace();
	                            }
                            }
                           
                            croppedBmp.recycle();
                            System.gc();
                    
                    }
            }

            ImageView imageView = (ImageView)findViewById(R.id.image_view);
             
            imageView.setImageBitmap(bitmap565);
    }
}
 
    private View.OnClickListener btnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    switch(v.getId()){
                            case R.id.take_picture:         openCamera();   break;
                            case R.id.detect_face:          detectFaces();  break;  
                    }
            }
    };
}