package jucDesign;

import java.util.concurrent.Semaphore;

/*
同时最多n个线程执行临界区
 */
public class MySemphoreTest {
    public static void main(String[] args) {
        MySemphoreTest mySemphoreTest = new MySemphoreTest();
        //Semaphore semaphore = new Semaphore(8);
        MyDemphore semaphore = new MyDemphore(8);
        for(int i=0;i<100;i++){
            int finalI = i;
            new Thread(()->{
                try {
                    semaphore.acquire();
                    System.out.println(finalI+"号客人进来吃饭");
                    mySemphoreTest.eat(finalI);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    semaphore.release();//唤醒线程
                }

            }).start();
        }
    }

    public void eat(int i) throws InterruptedException {
        Thread.sleep(100);
        System.out.println(i+"号客人吃完，请下个客人");
    }
}
