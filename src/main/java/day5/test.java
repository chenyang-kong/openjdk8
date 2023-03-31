package day5;

/**
 * 美团面试题
 */
public class test {
    private static int a=0,b=0;
    private static int x=0,y=0;
    public static void main(String[] args) {
        int i=0;
        for(;;){
            i++;
            x=0;y=0;a=0;b=0;
            Thread t1= new Thread(() -> {
                a=1;
                x=b;
            });

            Thread t2= new Thread(() -> {
                b=1;
                y=a;
            });
            t1.start();
            t2.start();

            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("第"+i+"次"+"a="+a+";b="+b+";x="+x+";y="+y);
        }
    }
}
