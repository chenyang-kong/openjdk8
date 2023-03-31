package day8;



public class UnFairSync {
    private static Object object= new Object();
    public static void main(String[] args) {
        Thread[] threads = new Thread[10];
        for(int i=0;i<threads.length;i++){
            threads[i]= new Thread(()->{
                synchronized (object){
                    System.out.println(Thread.currentThread().getName()+"get lock");
                }
            });
        }
        synchronized (object){
            for(int i=0;i<threads.length;i++){
                threads[i].start();
                System.out.println(threads[i].getName()+"start lock");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
