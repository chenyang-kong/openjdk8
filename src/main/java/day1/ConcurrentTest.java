package day1;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 验证多线程一定比单线程快吗？
 * 多线程并不一定比单线程快，需要考虑数据量，如果数据是10亿，多线程就更快了
 */
public class ConcurrentTest {
    static int COUNT=10000000;
    public static void main(String[] args) {
        //并发
        concurrency();
        //串行
        serial();
       // Thread
    }

    private static  void concurrency(){
        long startTime = System.currentTimeMillis();
        AtomicInteger a= new AtomicInteger();
        Thread thread= new Thread(()->{

           for(int i=0;i<COUNT;i++){
               a.addAndGet(3);
           }
        });
        thread.start();
        int b=0;
        for(int j=0;j<COUNT;j++){
            b--;
        }
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("cost time="+(System.currentTimeMillis()-startTime));
        System.out.println("a="+a);
    }


    private static  void serial(){
        long startTime = System.currentTimeMillis();

            int a=0;
            for(int i=0;i<COUNT;i++){
                a+=3;
            }
        int b=0;
        for(int j=0;j<COUNT;j++){
            b--;
        }
        System.out.println("cost time2="+(System.currentTimeMillis()-startTime));

    }
}
