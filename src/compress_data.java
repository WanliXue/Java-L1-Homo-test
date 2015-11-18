import Jama.Matrix;
import Jama.SingularValueDecomposition;
//import com.berkley.homotopy.HomotopyFP;
//import com.berkley.homotopy.jamafp.*;
import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;
import Jama.*;


//import com.berkley.homotopy.HomotopyFP;
//import com.berkley.homotopy.fpmath.FPMath;
//import com.berkley.homotopy.jamafp.Array;
//import com.berkley.homotopy.jamafp.Matrix;
//

/**
 * class used to compress data x=>y
 * Created by wanli on 12/10/15.
 */
public class compress_data {

    public static void main(String[] args)
    {
        MLArray mlArrayDict;
        MLDouble mlDict;
        double [][] dict;
        Matrix Dictionary;
        int noProj = 30;

        try {
//            reade the file from the .mat
            MatFileReader matfilereader = new MatFileReader("HB_dic.mat");

//            cast MLArray to MLDouble
            mlDict = (MLDouble) matfilereader.getMLArray("PsiX");
            System.out.println("loading the dictionary" + mlDict.toString());
            dict = mlDict.getArray();
            Dictionary = new Matrix(dict); //store the dictionary
//            displayMatrix(Dictionary);

            Matrix Phi = getPhi(Dictionary, noProj);

            Matrix rawData = readRawdata(); //dimension 58xn
            Matrix compressData = Phi.times(rawData);


            displayMatrix(compressData);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static Matrix readRawdata()
    {
        double rawData[][]={};
        try {
            MatFileReader mtfr = new MatFileReader("HB_rawData.mat");
            MLDouble temp = (MLDouble) mtfr.getMLArray("xData");
            System.out.println(temp.toString());

            rawData = temp.getArray();
            System.out.println(rawData[0][20]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Matrix (rawData);
    }

    //    print the value of a matrix
    public static void displayMatrix(Matrix m)
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

    public static void displayMatrix(MLDouble m)
    {
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

    //    @dicM Dictionary Matrix
//    @noProj No. of Projection (the column size of Phi)
//    @out Matrix Phi
    public static Matrix getPhi(Matrix dicM, int noProj)
    {
//        initialize a Phi of zeros
        Matrix Phi = new Matrix(noProj,noProj);


//        print first noProj lines as Phi
//        LUDecomposition LU = new LUDecomposition(dicM);
//        Matrix U = LU.getU();

        SingularValueDecomposition svd = new SingularValueDecomposition(dicM);
        Matrix U = svd.getU();
        Matrix V= svd.getV();
        Matrix S = svd.getS();

//        System.out.println("dimension of U is "+U.getRowDimension()+" "+U.getColumnDimension());
//        System.out.println(U.get(3,0));
//        displayMatrix(U);
        Phi = changeSign(U, dicM, noProj);
//        displayMatrix(U);
//        displayMatrix(V);
//        displayMatrix(S);

//        displayMatrix(Phi);



        System.out.println("Phi is  "+ Phi.getRowDimension()+" "+Phi.getColumnDimension());

        return Phi;
    }

    //change the sign of first column of a matrix
    public static Matrix changeSign(Matrix Phi, Matrix dicM, int noProj)

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
        displayMatrix(Phi);


        double[][] array =Phi.getArray();
//        for(int i = 0; i< 1; i++) //only the first row
//        {
//            for (int j=0; j< array[i].length; j++)
//            {
//
//                array[i][j]= -array[i][j]; //change sign
//                System.out.print( " "+ array[i][j]);
//            }
//            System.out.println();
//        }

        Phi = new Matrix(array);

//        Phi = Phi.inverse(); // dont have to inverse back
//        displayMatrix(Phi);

        return Phi;
    }
}
