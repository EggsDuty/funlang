public class FunProgram {
    public static void main(String[] args) {
        int a = 10;
        double b = 2.5;
        String msg = "Labas!";
        int a2 = 9;
        System.out.println(Math.min(a, a2));
        System.out.println(Math.sqrt(a2));
        System.out.println(msg);
        System.out.println(b);
        System.out.println(((a * b) / 2));
        if ((a < 1)) 
        {
            System.out.println("a lower than 1");
        }
        else if ((a < 10)) 
        {
            System.out.println("a lower than 10");
        }
        else 
        {
            System.out.println("a bigger than or equal to 10");
        }
        for (int i = 1; i <= 3; i += 1) 
        {
            System.out.println(i);
        }
        int counter = 0;
        while ((counter <= 2)) 
        {
            counter = (counter + 1);
            System.out.println("While loop");
        }
        System.out.println(Add(a, b));
        // end of main
    }
    public static double Add(int a, double b) 
    {
        double x = (a + b);
        return x;
    }
    
}
