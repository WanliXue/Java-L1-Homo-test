/**
 * Created by wanli on 8/10/15.
 */
import Jama.*;
import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;


public class test_driver {

    public static void main (String[] args)
    {

        MLArray mlArrayDict;
        MLDouble mlDict;

        Matrix Dictionary;  // marked as Psi sometimes


        double[][] dict;

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


        System.out.println("testData length is " + testData.length);

        Matrix tData =new Matrix(testData,1);
        tData = tData.transpose(); //make row to column

        //check data
//        displayMatrix(tData);

        int noProj = 5;

//        -----------testing part---------------
//        System.out.println("test");
//        double[][] array = {{1.,2.,3,},{4.,5.,6.},{7.,8.,10.}};
//
//
//        System.out.println(array[0][1]);
//        Matrix A = new Matrix(array);
//        Matrix b = Matrix.random(3,1);
//
//        System.out.println(b.getRowDimension() + "  " + b.getColumnDimension());
////        b.print(1,1);
//        Matrix x = A.solve(b);
//        Matrix Residual = A.times(x).minus(b);
//        double rnorm = Residual.normInf();
//        ------------------

        try {
//            reade the file from the .mat
            MatFileReader matfilereader = new MatFileReader("HB_dic.mat");

//            cast MLArray to MLDouble
            mlDict = (MLDouble)matfilereader.getMLArray("PsiX");
            System.out.println("loading the dictionary" +mlDict.toString());
//            displayMatrix(mlDict);

//          store the dict to java double array
           dict= mlDict.getArray();
           Dictionary = new Matrix(dict);
//            displayMatrix(Dictionary);

            Matrix Phi = getPhi(Dictionary,noProj);
//            displayMatrix(Phi);

//          exapmple output
//            System.out.println(dict[20][20]);


// ---------- Key Fucntion Testing Part---------
//            compress y = Phi*testData
            Matrix y = Phi.times(tData);
//            displayMatrix(y);

        double ave =calculate_average(Dictionary,Phi,  y);


    System.out.println("Real average is "+aveArray(testData));

// -------------------------------




        }
        catch (Exception e)
        {
            e.printStackTrace();
        }





    }


public static double aveArray(double[] array)
{
    double sum=0;

    for (int i = 0; i < array.length; i++)
    {
        sum = sum + array[i];
    }


    return sum/array.length;
}





//    calculate the average with the data send in
//    @Psi Dictionary Matrix 58x58
//    @Phi Projection Matrix 10x58
//    @testData The temp data need to calculate 10x1
    public static double calculate_average(Matrix Psi, Matrix Phi, Matrix y)
    {
        double average =0; //intial as 0
        int lenX = Phi.getColumnDimension(); //58  get the column length
        System.out.println("lenx is " + lenX);
        double scalar=1/(double)lenX;
        System.out.println("scalar is "+scalar);
        Matrix avgMulti = new Matrix(1, lenX, scalar);

//        System.out.println("here is avgMulti");
//        displayMatrix(avgMulti);

//---------------Pinv method---------------------
        Matrix tempPhi=Phi.transpose();
        Matrix pinvPhi= tempPhi.inverse();
        pinvPhi=pinvPhi.transpose();
        Matrix Z_pinv = avgMulti.times(pinvPhi);
//        Z_pinv = avgMultiplierVector * pinv(Phi);
        System.out.println("Z_pinv dimension is " + Z_pinv.getRowDimension() + "  " + Z_pinv.getColumnDimension());
        displayMatrix(y);

        Matrix derivedAverage_pinv=Z_pinv.times(y);
        average = derivedAverage_pinv.trace();
//   -------------------------------------------

//    ---------------- L1 Homotopy--------
//        com.berkley.homotopy.jamafp.Matrix l1Phi = new com.berkley.homotopy.jamafp.Matrix(Phi.getArray());
//        l1Phi=l1Phi.transpose();
//        double[] array = new double[lenX];
//        Arrays.fill(array, scalar);
//        com.berkley.homotopy.jamafp.Array l2avgMulti  = new Array(array);
//        Array alpha = HomotopyFP.SolveHomotopy(l1Phi, l2avgMulti);
//        System.out.println(alpha.toString());
//      ---------------------------------



        System.out.println("derived average is " + average);

        return average;
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


}
