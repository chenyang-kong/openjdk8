package juc.unSafe_Study;

import juc.unSafe_Study.entity.Man;
import juc.unSafe_Study.entity.Person;
import sun.misc.Unsafe;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * UnSafe处理数据
 */
public class UnSafeDealData {
    public static void main(String[] args) {
        //创建UnSafe
        Unsafe unSafe = CreateUnSafe.createUnSafe();
        //unSafe_get_array(unSafe);
        //unSafe_deal_Object(unSafe);
        //unSafepark(unSafe);
        unSafeCas(unSafe);
    }

    /**UnSafe 获取数组数据*/
    public static void unSafe_get_array(Unsafe unsafe){
        String[] str=new String[]{"zhangsan","lisi","wanger"};
        //计算基数偏移量地址
        int baseOffset = unsafe.arrayBaseOffset(String[].class);
        //获取数组间隔偏移量
        int indexScale = unsafe.arrayIndexScale(String[].class);

        System.out.println("baseOffset:"+baseOffset);
        System.out.println("indexScale:"+indexScale);

        //获取数据
        for (int i=0;i<str.length;i++){
            long offset=baseOffset+indexScale*i;
            String data = (String) unsafe.getObject(str, offset);//string类型用getObject方法
            System.out.println("str"+i+"="+data);
        }


        long[] lon={1,2,3};
        //计算基数偏移量地址
        int baseOffset2 = unsafe.arrayBaseOffset(long[].class);
        //获取数组间隔偏移量
        int indexScale2 = unsafe.arrayIndexScale(long[].class);

        System.out.println("baseOffset2:"+baseOffset2);
        System.out.println("indexScale2:"+indexScale2);

    }

    /***
     * UnSafe处理对象数据
     * @param unsafe
     */
    public static void unSafe_deal_Object(Unsafe unsafe){
        try {
            //通过UnSafe 获取对象
            Man man = (Man) unsafe.allocateInstance(Man.class);
            System.out.println("man:"+man);

            Person person = new Person();
            person.setId(1);
            person.setMan(new Man(20,"zhangsan"));
            //获取id偏移量
            long idOffset = unsafe.objectFieldOffset(Person.class.getDeclaredField("id"));
            //获取man偏移量
            long manOffset = unsafe.objectFieldOffset(Person.class.getDeclaredField("man"));
            //获取静态字段defaultString偏移量
            long defaultStringOffset = unsafe.staticFieldOffset(Person.class.getDeclaredField("defaultString"));

            //获取字段对应值
            System.out.println("get id:"+unsafe.getInt(person,idOffset));
            System.out.println("get main:"+unsafe.getObject(person,manOffset));
            //这个注意要使用Person.class
            System.out.println("get defaultStringOffset:"+unsafe.getObject(Person.class,defaultStringOffset));

            //修改值
            unsafe.putInt(person, idOffset, 2);
            unsafe.putObject(person, manOffset, new Man(21,"lisi"));
            unsafe.putObject(person, defaultStringOffset, "new staticString");

            //获取字段对应值
            System.out.println("get new id:"+unsafe.getInt(person,idOffset));
            System.out.println("get new main:"+unsafe.getObject(person,manOffset));
            //这个注意使用Person.class的话是取到class的值，要先赋值才能用person的值，
            System.out.println("get new defaultStringOffset:"+unsafe.getObject(person,defaultStringOffset));

        } catch (InstantiationException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void unSafepark(Unsafe unsafe){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss");
        System.out.println(LocalDateTime.now().format(formatter)+" "+Thread.currentThread().getName()+"正在运行。。。");

        //阻塞5秒 true 系统时间 + 毫秒
        unsafe.park(true, System.currentTimeMillis()+ TimeUnit.SECONDS.toMillis(5));
        System.out.println(LocalDateTime.now().format(formatter)+" "+Thread.currentThread().getName()+"阻塞后回复运行1。。。");

        //阻塞3秒 false 纳秒
        unsafe.park(false, TimeUnit.SECONDS.toNanos(3));
        System.out.println(LocalDateTime.now().format(formatter)+" "+Thread.currentThread().getName()+"阻塞后回复运行2。。。");

        //将会一直阻塞，无法自动醒来
        unsafe.park(false, 0);
    }

    private volatile int a;
    public static void unSafeCas(Unsafe unsafe){
        UnSafeDealData unSafeDealData = new UnSafeDealData();
        new Thread(()->{
            for(int i=1;i<=10;i++){
                unSafeDealData.increment(i,unsafe);
                System.out.println(unSafeDealData.a);
            }
        }).start();

        new Thread(()->{
            for(int i=11;i<=20;i++){
                unSafeDealData.increment(i,unsafe);
                System.out.println(unSafeDealData.a);
            }
        }).start();
    }
    private void increment(int x,Unsafe unsafe){
        long offset=0;
        do {
            try {
                offset = unsafe.objectFieldOffset(UnSafeDealData.class.getDeclaredField("a"));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }while (!unsafe.compareAndSwapInt(this,offset,x-1,x));
    }
}
