package day1;

import java.util.Map;
import java.util.Set;

/**
 * main线程详解
 */
public class mainTst {
    public static void main(String[] args) {
        //main方法启动时。启动了哪些线程
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        Set<Thread> threads = allStackTraces.keySet();
        for(Thread thread:threads){
            System.out.println(">>>>线程名："+thread.getName());
        }

        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
