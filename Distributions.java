import java.util.Random;
public class Distributions{
	public static void main(String[] args){
		Random r = new Random();
		double mean  = 50;
		double std = 15;
		for(int i=1;i<=100000;i++){
			double x = r.nextGaussian()*std+mean;
			double y = pdf(x, mean, std);
			// double x = r.nextGaussian();
			// double y = pdf_std(x);
			System.out.println(x+" "+y);
		}
	}
	public static double pdf_std(double x) {
        return Math.exp(-x*x / 2) / Math.sqrt(2 * Math.PI);
    }	
    public static double pdf(double x, double mean, double std) {
       return Math.exp(-(x-mean)*(x-mean) / (2*std*std)) / Math.sqrt(2 *std*std* Math.PI);
    }    
}