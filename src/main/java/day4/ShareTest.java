package day4;

import java.util.concurrent.TimeUnit;

/**
 * 测试共享变量
 */
public class ShareTest {
    static int a=0;

    public static void main(String[] args) throws InterruptedException {
        new Thread(()->{
            System.out.println("t1线程运行");
            while(true){
                System.out.println("aaaaa");//添加这个打印，下面if监测到a的变化？？？好奇怪
                if(a==1){//这里监测不到a值发生了变化
                    System.out.println("a值发生了变化，a="+a);
                    //退出循环
                    break;
                }
            }
        }).start();

      Thread t2=  new Thread(() -> {
          System.out.println("t2线程运行");
            for(int i=0;i<1000000;i++){}
            System.out.println("a值进行改变");
            a=1;
        });

        t2.start();
        t2.join();
        System.out.println("主线程能否获取变化后的a值？ a="+a);//可以获得
    }
}
