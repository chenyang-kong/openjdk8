package day8;

import jdk.nashorn.internal.ir.CallNode;

import java.util.ArrayList;
import java.util.List;

public class WaitAndNotify {
    private final static Object OBJECT=new Object();
    private static  boolean hasProduct=false;
    static List list=new ArrayList<>();
    public static void main(String[] args) {
        Thread producer=  new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (OBJECT){
                    if(hasProduct){
                        try {
                            OBJECT.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return;
                    }else{
                        producer();
                        OBJECT.notify();
                    }
                }
            }
            //生产者生产
            public void producer(){
                list.add(new Object());
                hasProduct=true;
                System.out.println("p--->"+list.get(0));
            }
        });
        producer.start();

        Thread consumer= new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (OBJECT){
                    if(hasProduct){
                        consumer();
                        OBJECT.notify();
                    }else{
                        try {
                            OBJECT.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            public void consumer(){
                list.remove(0);
                System.out.println("消费了");
                hasProduct=false;
            }
        });
        consumer.start();
    }
}
