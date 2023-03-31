package day4;

import java.util.ArrayList;

/**
 * 高深面试题
 */
public class ShareTest2 {
    static int a=0;
    private static  int count=200;//100,200,300,400,500,600,700,100000
    public static void main(String[] args) {

        new Thread(() -> {
            System.out.println("1执行");
            ArrayList<Object> list = new ArrayList<Object>();
            long l = System.nanoTime();
            for(int x=1;x<=count;x++){
                list.add(x);
            }

            while (true){
                if(list.contains(a)){
                    System.out.println(System.currentTimeMillis()+":::消费者线程打印a:"+a);
                    break;
                }
            }
        }).start();

       Thread t2= new Thread(() -> {
            System.out.println("2执行");
            //睡2秒
            time(2000000000);
            for(int j=1;j<=count;j++){
                a=j;
            }
           System.out.println(System.currentTimeMillis()+":::t2线程改变a值，a="+a);
        });
       t2.start();

//       while (a<=601){
//           System.out.println("主线程获取a值，a="+a);
//
//       }


    }
    //自定义睡眠方法
    private static void time(long sleeptime){
        long start = System.nanoTime();
        long end;
        do{
            end=System.nanoTime();
        }while (start+sleeptime>=end);

    }
}
