package day9;

import jdk.nashorn.internal.ir.CallNode;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadLocalTest {
   private ThreadLocal threadLocal= new ThreadLocal<Integer>();

    public static void main(String[] args) {
        ThreadLocalTest threadLocalTest = new ThreadLocalTest();
        ArrayList<Thread> threads = new ArrayList<>();
        for(int i=0;i<5;i++){
            int finalI = i;
            Thread t=  new Thread(()->{
                threadLocalTest.test1(finalI);
                threadLocalTest.test2();
            });
            t.start();
            threads.add(t);
        }

    }

    public void test1(int i){
        threadLocal.set(i);

    }
    public void test2(){
        System.out.println(Thread.currentThread().getName()+":"+threadLocal.get());
    }

}
