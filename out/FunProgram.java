import java.lang.Math;
public class FunProgram {
    public static void main(String[] args) {
        int aInput = 3;
        double result = 0;
        result = Add((int)Add((int)aInput, (int)6), (int)10);
        Add((int)Add((int)aInput, (int)6), (int)10);
        System.out.println(result);
        // end of main
    }
    public static double Add(int a, double b) 
    {
        double x = (a + b);
        return x;
    }
    
}
