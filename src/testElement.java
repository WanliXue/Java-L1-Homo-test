import Jama.Matrix;
import Jama.SingularValueDecomposition;
import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;
import JamaFixedPoint.Array;

/**
 * Created by wanli on 12/10/15.
 */

import FixedPointMath.FPMath;
import JamaFixedPoint.Array;
import JamaFixedPoint.*;

//import com.berkley.homotopy.HomotopyFP;
//import com.berkley.homotopy.fpmath.FPMath;
//import com.berkley.homotopy.jamafp.Array;
//import com.berkley.homotopy.jamafp.Matrix;


public class testElement {

    public static void main(String[] args)
    {

        double[] testData={74,
                75,
                75,
                74,
                74,
                74,
                73,
                74,
                74,
                75,
                75,
                92,
                89,
                82,
                77,
                74,
                74,
                73,
                73,
                72,
                70,
                69,
                70,
                70,
                71,
                72,
                74,
                74,
                71,
                70,
                70,
                71,
                72,
                74,
                74,
                73,
                73,
                73,
                73,
                73,
                73,
                73,
                72,
                71,
                70,
                70,
                72,
                71,
                71,
                71,
                71,
                69,
                63,
                63,
                61,
                61,
                62,
                62};

        Jama.Matrix Dictionary = new Jama.Matrix(testData,1);
        Jama.Matrix Phi = new  Jama.Matrix(new double [1][1]); //fake initializer
        Jama.Matrix y =new  Jama.Matrix(testData,1);
        MLArray mlArrayDict;
        MLDouble mlDict;

        Jama.Matrix tData =new  Jama.Matrix(testData,1);
        tData = tData.transpose(); //make row to column
        int noProj = 5;

        double[][] dict;
//        Matrix A = new Matrix();
        try {
//            reade the file from the .mat
            MatFileReader matfilereader = new MatFileReader("HB_dic.mat");

//            cast MLArray to MLDouble
            mlDict = (MLDouble)matfilereader.getMLArray("PsiX");
            System.out.println("loading the dictionary" +mlDict.toString());
//            displayMatrix(mlDict);

//          store the dict to java double array
            dict= mlDict.getArray();
            Dictionary = new  Jama.Matrix(dict);
//            displayMatrix(Dictionary);

             Phi = getPhi(Dictionary,noProj);
//            displayMatrix(Phi);

//          exapmple output
//            System.out.println(dict[20][20]);


// ---------- Key Fucntion Testing Part---------
//            compress y = Phi*testData
             y = Phi.times(tData);
            displayMatrix(y);





        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


//-----------------RECONSTRUCT-------------------
//Array alpha = HomotopyFP.SolveHomotopy(A, y);
//  A = Phi*Psi= Phi*Dictionary
//  y is compressed data
//  alpha is the result by solving y = A * alpha = Phi*Psi*alpha;
//  so the raw value is x = Psi*alpha

        Jama.Matrix A = Phi.times(Dictionary);      //get A =(Phi * Psi(Dictionary))

        System.out.println("here we comes A");
        displayMatrix(A);

JamaFixedPoint.Matrix B = new JamaFixedPoint.Matrix(A.getArray());
       double[] ya = y.getColumnPackedCopy();
        System.out.println(ya.toString());
JamaFixedPoint.Array y1 = new JamaFixedPoint.Array(ya);

//        Array A2 = new Array(a1);
//JamaFixedPoint.Array y1 = new JamaFixedPoint.Array();

JamaFixedPoint.Array theta = HomotopyFP.SolveHomotopy(B,y1);

JamaFixedPoint.Matrix JFPdic = new JamaFixedPoint.Matrix(Dictionary.getArray());
JamaFixedPoint.Array xarray = JFPdic.times(theta);



        System.out.print("xarray = [");
//        JFPdic.print(0,1);
        xarray.printArray();
        System.out.println("];");
//        xarray.getArray();
//        System.out.print(theta.getArray());
        int[] hints = xarray.getArray();
//        System.out.println(reconX[10]);
           double[] Xrecon= printArray(hints);

        System.out.println(Xrecon.length);
        System.out.println(Xrecon[0]);
//---------------------------------------------
    }

//Print the array to check
 public static double[] printArray(int[] ar)
 {
     double[] result = new double[58];
     for (int i =0; i<=ar.length-1; i++)
     {
//         System.out.print(ar[i] + " ");
         double xr = FPMath.FPToFloat(ar[i]);
         System.out.print( xr );
         result[i]= xr;
     }
     System.out.println();

     return result;
 }



    //    @dicM Dictionary Matrix
//    @noProj No. of Projection (the column size of Phi)
//    @out Matrix Phi
    public static  Jama.Matrix getPhi( Jama.Matrix dicM, int noProj)
    {
//        initialize a Phi of zeros
        Jama.Matrix Phi = new  Jama.Matrix(noProj,noProj);



        SingularValueDecomposition svd = new SingularValueDecomposition(dicM);
        Jama.Matrix U = svd.getU();
        Jama.Matrix V= svd.getV();
        Jama.Matrix S = svd.getS();

        Phi = changeSign(U, dicM, noProj);



        System.out.println("Phi is  "+ Phi.getRowDimension()+" "+Phi.getColumnDimension());

        return Phi;
    }

    //change the sign of first column of a matrix
    public static  Jama.Matrix changeSign( Jama.Matrix Phi,  Jama.Matrix dicM, int noProj)

    {


        int length = dicM.getRowDimension();
        int[] r ;
        r= new int[noProj];

        for (int i=0; i < noProj;i++)
        {
            r[i]=i;
        }


        Phi = Phi.getMatrix(0,length-1,r); //selct the 1-30 column
//        displayMatrix(Phi);

        Phi = Phi.transpose(); //tranpose the colum to row
//        displayMatrix(Phi);


        double[][] array =Phi.getArray();


        Phi = new  Jama.Matrix(array);

        return Phi;
    }
    //    print the value of a matrix
    public static void displayMatrix( Jama.Matrix m)
    {
        System.out.println("dimension of "+m.toString() +" "+m.getRowDimension()+" "+m.getColumnDimension());

        double[][] array =m.getArray();
        for(int i = 0; i< array.length; i++)
        {
            for (int j=0; j< array[i].length; j++)
            {
                System.out.print( " "+ array[i][j]);
            }
            System.out.println();
        }

        System.out.println();
    }

}
