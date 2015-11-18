package com.example.facedetectionexample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.berkley.homotopy.HomotopyFP;
import com.berkley.homotopy.fpmath.FPMath;
import com.berkley.homotopy.jamafp.Array;
import com.berkley.homotopy.jamafp.Matrix;


public class L1Classifier {

	private static final L1Classifier instance = new L1Classifier();
	
	/** Asset filename where the A matrix is stored. */
	private static final String A_MATRIX = "AP.txt";
	
	/** Asset filename where the B matrix is stored. */
	private static final String B_MATRIX = "B.txt";
	
	/** Asset filename where the projection matrix is stored. */
	private static final String PROJECTION_MATRIX = "P.txt";
	
	private Matrix A;

    private Matrix ACOE;
	
	/** Label identity matrix */
	private Matrix B;
	
	/** Projection matrix used for dimensionality reduction */
	private Matrix P;

	private L1Classifier() {
	
	}

	public static L1Classifier getInstance() {
		return instance;
	}
	
	
	public int doClassifyActivity(
			WeakReference<Context> contextReference,
			int[] pixels) {
		if(A == null || B == null) {
			init(contextReference);
		}
		String s1 = new String();
		
		assert(A.getRowDimension() == B.getRowDimension());
		
		//Array y = new Array(new double[pixels.length]);
		Array y = new Array(pixels); 
	/*	Log.i("FaceDetector", "lengths: " + pixels.length);
		for(int i = 0; i < pixels.length; i++) {
			y.setIndex(i, pixels[i]);
		}
		*/
	/*	Log.i("FaceDetector", "y length: " + y.getLength() );
		*/
		File sensorDataLogFile = new File(Environment.getExternalStorageDirectory().toString()  + "/DCIM/face/b" 
        		+  System.currentTimeMillis() + ".csv");
		BufferedWriter sensorDataLogFileWriter = null;
		
		if(sensorDataLogFileWriter == null) {
			try {
				sensorDataLogFileWriter = new BufferedWriter(new FileWriter(sensorDataLogFile, true));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < y.getLength(); i++) {
			builder.append(y.getElement(i));
			builder.append('\n');
		}
		
		try {
			sensorDataLogFileWriter.write(builder.toString());
			sensorDataLogFileWriter.flush();
			sensorDataLogFileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//*/
		//Log.i("FaceDetector", s1);

//        Log.i("FaceDetector", "before y: " );
//        s1 = "";
//        for ( int i = 0 ; i < y.getLength()/10 ; i++){
//            s1 += y.getElement(i) + ", ";
//        }
//        Log.i("FaceDetector", s1 );


		
		//y = P.times(y);

        int[][] a = P.getArray();
        int[] b = new int[a.length];
        for(int i = 0 ; i < a.length; i++){
            b[i] = 0;
            for(int j = 0; j < a[0].length; j++){
                b[i] += a[i][j] * y.getElement(j);
            }
        }
        y = new Array(b);



		
		/*Log.i("FaceDetector", "before P: " );
		s1 = "";
		for(int i = 0; i < y.getLength(); i++) {
			s1 += FPMath.FPToFloat(y.getElement(i)) + " ,";
		}
		Log.i("FaceDetector", s1);
		*/
		//norm(y);
		
/*		Log.i("FaceDetector", "after y: " );
		s1 = "";
		for(int i = 0; i < y.getLength(); i++) {
			s1 += FPMath.FPToFloat(y.getElement(i)) + " ,";
		}
		Log.i("FaceDetector", s1);
	*/	
		// Solve for alpha using homotopy for the equation: y = alpha * A.
//        Log.i("FaceDetector", "A: " );
//        s1 = "";
//        for ( int i = 0 ; i < A.getColumnDimension()/10 ; i++){
//            for ( int j = 0 ; j < A.getRowDimension()/10 ; j++){
//                s1 += A.get(j,i)  + ", ";
//            }
//
//        }
//        Log.i("FaceDetector", s1 );
//
//        Log.i("FaceDetector", "after y: " );
//        s1 = "";
//        for ( int i = 0 ; i < y.getLength()/10 ; i++){
//            s1 += y.getElement(i)  + ", ";
//        }
//        Log.i("FaceDetector", s1 );
//
//        Log.i("FaceDetector", "P: " );
//        s1 = "";
//        for ( int i = 0 ; i < 2 ; i++){
//            for ( int j = 0 ; j < 2; j++){
//                s1 += P.get(j,i)  + ", ";
//            }
//
//        }
//        Log.i("FaceDetector", s1 );

		Array alpha = HomotopyFP.SolveHomotopy(A, y);
		


		int numClasses = B.getRowDimension();
		double[] residuals = new double[numClasses];
		double minResidualValue = Double.MAX_VALUE;
		int minResidualIndex = 0;
		
		Log.i("FaceDetector", "alpha: " );
		s1 = "";
		for(int i = 0; i < alpha.getLength(); i++) {
//            if (FPMath.FPToFloat(alpha.getElement(i)) != 0){
			    s1 += FPMath.FPToFloat(alpha.getElement(i)) + " ,";
//            }
		}
		
		Log.i("FaceDetector", s1);
		
		Log.i("FaceDetector", "alpha: " );
		s1 = "";
		for(int i = 0; i < alpha.getLength(); i++) {
//            if (FPMath.FPToFloat(alpha.getElement(i)) != 0){
			    s1 += (alpha.getElement(i)) + " ,";
//            }
		}
		
		Log.i("FaceDetector", s1);

        double[] alphatemp = new double[ alpha.getLength() ] ;
        for (int i = 0 ; i < alphatemp.length ; i++){
            alphatemp[i] = (double) (alpha.getElement(i)) ;
        }
        Array alphaInput = new Array (alphatemp ) ;
        
        
        Log.i("FaceDetector", "alphaInput: " );
		s1 = "";
		for(int i = 0; i < alphaInput.getLength(); i++) {
//            if (FPMath.FPToFloat(alpha.getElement(i)) != 0){
			    s1 += (alphaInput.getElement(i)) + " ,";
//            }
		}
		
		Log.i("FaceDetector", s1);
		
		
		

        double[] ytemp = new double[ y.getLength() ] ;
        for (int i = 0 ; i < ytemp.length ; i++){
            ytemp[i] = (double) y.getElement(i) ;
        }
        Array yInput = new Array (ytemp ) ;
		
		for(int i = 0; i < numClasses; i++) {
			// Calculate the residual value.
			Array dotProduct = Array.dotMult(new Array(B.getArrayCopy()[i]), alpha);
			
			Log.i("FaceDetector", "dotProduct: " );
			s1 = "";
			for(int j = 0; j < dotProduct.getLength(); j++) {
//	            if (FPMath.FPToFloat(alpha.getElement(i)) != 0){
				    s1 += (dotProduct.getElement(j)) + " ,";
//	            }
			}
			
			Log.i("FaceDetector", s1);
			
			Array reconstruction = yInput.subtractArray(ACOE.times(dotProduct));

			for(int bcd : reconstruction.getArray()) {
//				residuals[i] += Math.pow(bcdToDouble(bcd), 2);
                residuals[i] += Math.pow(FPMath.FPToFloat(bcd), 2);
			}
			residuals[i] = Math.sqrt(residuals[i]);

			// And update the minimum residual value and index.
			if(residuals[i] < minResidualValue) {
				minResidualValue = residuals[i];
				minResidualIndex = i;
			}
		}
		
		Log.i("FaceDetector", "residual: " );
		s1 = "";
		for(int i = 0; i < numClasses; i++) {
			s1 += residuals[i] + " ,";
		}
		
		Log.i("FaceDetector", s1);
		
		
		
		int activityType = minResidualIndex;
	/*	if(minResidualIndex == 0) {
			activityType = ActivityType.WALKING;
		} else if(minResidualIndex == 1) {
			activityType = ActivityType.SITTING;
		} else if(minResidualIndex == 2) {
			activityType = ActivityType.LAYING;
		}
		*/
		return activityType;
	}
	
	public void init(final WeakReference<Context> contextReference) {
		if(A == null || B == null) {
			A = initMatrixFromAsset(contextReference, A_MATRIX);
            ACOE = initMatrixFromAssetDouble(contextReference, A_MATRIX);
			B = initMatrixFromAssetDouble(contextReference, B_MATRIX);
			P = initMatrixFromAsset(contextReference, PROJECTION_MATRIX);
		}
	}
	
	/**
	 * 
	 * @param contextReference the current application context.
	 * @param assetFileName
	 * @return
	 */
	private Matrix initMatrixFromAsset(final WeakReference<Context> contextReference, String assetFileName) {
//		double[][] values = null;
        int[][] values = null;
		Matrix ret = null;
		
		try {
			AssetManager assetManager = contextReference.get().getAssets();
			InputStream in = assetManager.open(assetFileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			
			int lineIndex = 0;
			String line;
			
			values = allocIntegerArray(contextReference, assetFileName);
			while((line = reader.readLine()) != null) {
				String[] a = line.split("\\s+");
				
				for(int i = 0; i < a.length; i++) {
					values[i][lineIndex] = Integer.valueOf(a[i]);
				}
				
				a = null;
				lineIndex++;
			}
			
			ret = new Matrix(values).transpose();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.gc();
		
		return ret;
	}

    private Matrix initMatrixFromAssetDouble(final WeakReference<Context> contextReference, String assetFileName) {
		double[][] values = null;
        Matrix ret = null;
        try {
            AssetManager assetManager = contextReference.get().getAssets();
            InputStream in = assetManager.open(assetFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            int lineIndex = 0;
            String line;

            values = allocDoubleArray(contextReference, assetFileName);
            while((line = reader.readLine()) != null) {
                String[] a = line.split("\\s+");

                for(int i = 0; i < a.length; i++) {
                    values[i][lineIndex] = Double.valueOf(a[i]);
                }

                a = null;
                lineIndex++;
            }

            ret = new Matrix(values).transpose();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.gc();

        return ret;
    }


    private int[][] allocIntegerArray(final WeakReference<Context> contextReference, String assetFileName) throws IOException {
        AssetManager assetManager = contextReference.get().getAssets();
        InputStream in = assetManager.open(assetFileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        int numCols = 0;
        int lineIndex = 0;
        String line;

        while((line = reader.readLine()) != null) {
            String[] a = line.split("\\s+");

            if(lineIndex++ == 0) {
                numCols = a.length;
            }
            a = null;
        }

        System.gc();

        return new int[numCols][lineIndex];
    }

	private double[][] allocDoubleArray(final WeakReference<Context> contextReference, String assetFileName) throws IOException {	
		AssetManager assetManager = contextReference.get().getAssets();
		InputStream in = assetManager.open(assetFileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		int numCols = 0;
		int lineIndex = 0;
		String line;
		
		while((line = reader.readLine()) != null) {
			String[] a = line.split("\\s+");
			
			if(lineIndex++ == 0) {
				numCols = a.length;
			}	
			a = null;
		}
		
		System.gc();
		
		return new double[numCols][lineIndex];
	}

	/**
	 * Normalize columns of the given matrix to a length of 1.
	 * 
	 * @param A the matrix to normalize.
	 */
	@SuppressWarnings("unused")
	private void normc(Matrix A) {
		for(int i = 0; i < A.getColumnDimension(); i++) {
			// Calculate the column total.
			double colTotal = 0;
			for(int j = 0; j < A.getRowDimension(); j++) {
				colTotal += Math.pow(bcdToDouble(A.get(j, i)), 2);
			}
			colTotal = Math.sqrt(colTotal);

			// And normalize each value in the column.
			for(int j = 0; j < A.getRowDimension(); j++) {
				int val = FPMath.FPDiv(A.get(j, i), FPMath.FloatToFP(colTotal));
				A.set(j, i, val);
			}
		}
	}
	
	/**
	 * Normalize an array to a length of 1.
	 * 
	 * @param array the array to normalize.
	 */
	private void norm(Array array) {
		int length = array.getLength();
		double total = 0;
		
		for(int i : array.getArray()) {
			total += Math.pow(bcdToDouble(i), 2);
		}
		/*for(int i : array.getArray()) {
			total += Math.pow(i, 2);
		}*/
		total = Math.sqrt(total); 
		
		Log.i("FaceDetector", "total: " + total);

		for(int i = 0; i < length; i++) {
			int val = FPMath.FPDiv(array.getElement(i), FPMath.FloatToFP(total));
			//int val = FPMath.FloatToFP((float) array.getElement(i)/total);
			array.setIndex(i, val);
		}
	}
	
	/**
	 * Convert a binary coded decimal (BCD) to a double.
	 * 
	 * @param bcd the BCD to convert.
	 * @return the equivalent decimal value of the given BCD as a double.
	 */
	private static double bcdToDouble(int bcd) {
		double retval;

		if(bcd == 0) {
			retval = 0;
		} else {
			retval = (bcd >> 16) + ((bcd & 0xFFFF) / ((double) 0xFFFF));
		}

		return retval;
	}
}
