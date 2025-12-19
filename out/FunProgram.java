import java.lang.Math;
public class FunProgram {
    public static void main(String[] args) {
        double aInput = 3;
        double result = 0;
        result = Sub(Add(Add(Add(aInput, (double)6), 10.1), (double)5), 3.6);
        Sub(Add(Add(Add(aInput, (double)6), 10.1), (double)5), 3.6);
        System.out.println(result);
        // end of main
    }
    public static double Add(double a, double b) 
    {
        double x = (a + b);
        return x;
    }
    
    public static double Sub(double a, double b) 
    {
        double x = (a - b);
        return x;
    }
    
}
