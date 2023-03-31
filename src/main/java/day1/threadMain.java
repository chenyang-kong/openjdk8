package day1;

public class threadMain {
    public static void main(String[] args) {
        //方式1
        thread1 thread1 = new thread1();
        thread1.start();
        //方式2
        Thread thread = new Thread(new thread2());
        thread.setDaemon(true);
        thread.start();

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("111");
            }
        });


    }
}
