package day10;

import java.util.concurrent.SynchronousQueue;

public class SynchronousQueueTest {
    public static void main(String[] args) {
        SynchronousQueue<Object> queue = new SynchronousQueue<>();

        //生产者
        Thread p=new Thread(()->{
            try {
                queue.put(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        p.start();

       //消费者
      Thread c=  new Thread(()->{
          try {
              Object obj = queue.take();
              System.out.println("获取值："+obj);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      });
      c.start();
    }
}
