package juc.unSafe_Study;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 创建UnSafe
 */
public class CreateUnSafe {

    public static void main(String[] args) {
        createUnSafe();
    }
    public static Unsafe createUnSafe(){
        Unsafe unsafe=null;
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe=(Unsafe)field.get(null);//这个随便写，我写的null
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        System.out.println("unsafe:"+unsafe);
        return unsafe;
    }

}
