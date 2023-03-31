package day6;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class MyUnsafe {
    public static Unsafe getUnsafe(){
        Field field=null;
        try {
            field = Unsafe.class.getDeclaredField("theUnasfe");
            field.setAccessible(true);
            return (Unsafe)field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
