package day7;

import org.openjdk.jol.info.ClassLayout;

public class MyObjecttTest {
    static MyObject myObject=null;

    public static void main(String[] args) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myObject = new MyObject();
        System.out.println(ClassLayout.parseInstance(myObject).toPrintable());


        new MyObjecttTest().test();

        System.out.println("加锁后-----------");
        System.out.println(ClassLayout.parseInstance(myObject).toPrintable());

    }

    public void test(){
        synchronized (myObject){
            System.out.println("加锁中+++++++");
            System.out.println(ClassLayout.parseInstance(myObject).toPrintable());
        }
    }
}
