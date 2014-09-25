package phfi.fetal.fetal_doppler;

import android.util.Log;

public class GaussianFilter {
	private final int gaussian_length_var=1056;
	private final int sum_of_coefficients = 1;
	public int tempNum = 0;
	public long sumOfCoefficients=1;
    double [] gaussianArray = new double[gaussian_length_var]; 
    double [] gaussianArraynew = new double[2080]; 
    double [] gaussianArrayFinal = new double[2047];
    short [] newArray = new short[gaussian_length_var];
    double [] gaussianFinalArray = new double[gaussian_length_var-32];
    double []gaussianCoefficients = {
            0.000918658762900967,0.00167271160390584,0.00291808157365892,
            0.00487734299048978,0.00781049714452678,0.0119834969351006,0.0176156215402622,	
            0.0248097212142047,0.0334776764680217,0.0432810952908657,0.0536106025286151,0.0636227804472263,  	   
            0.0723409340722900,0.0788070535507320,0.0822537258771996, 0.0822537258771996,0.0788070535507320,	
            0.0723409340722900,0.0636227804472263,0.0536106025286151, 0.0432810952908657,0.0334776764680216,
            0.0248097212142048,	0.0176156215402622,	0.0119834969351006,	0.00781049714452678,0.00487734299048978,
            0.00291808157365892,0.00167271160390584,0.000918658762900967

    		                         };
    //constructor to create the array with 33 elements
    public double [] GaussianFilterMethod(double [] copyArray)
    { 
        for(int i=0,j=1055;i<16&&j>=(1056-16);i++,j--)
        {        	
    	    gaussianArray[i]=0;
    	    gaussianArray[j]=0;
        }  
        for(int i=16;i<1040;i++)
        {
        	gaussianArray[i]=copyArray[i-16];
        }    
        int sum = 0;
        for(int k=0;k<1024;k++)
        {
        	for(int m=0;m<30;m++)
        	{
        		sum =sum+(int) (gaussianArray[m+k]*gaussianCoefficients[m]);
        	}
        	gaussianFinalArray[k]=(short) sum;
        }
		return gaussianFinalArray;
        
    }

}
