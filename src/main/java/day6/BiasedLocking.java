package day6;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class BiasedLocking {

    public boolean revokeAndRebias(MyLock myLock){
        MarkWord markWord = myLock.getMarkWord();
        long threadId = markWord.getThreadId();
        //获取偏向锁标记，锁的标记
        String lockFlag = markWord.getLockFlag();
        String biaseLock = markWord.getBiaseLock();
        Field threadId1 = null;
        try {
            threadId1 = markWord.getClass().getDeclaredField("threadId");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        Unsafe unsafe = MyUnsafe.getUnsafe();
        long offset = unsafe.objectFieldOffset(threadId1);
        long longVolatile = unsafe.getLongVolatile(markWord, offset);
        long id= Thread.currentThread().getId();
        //判断可偏向，但是还没有偏向
        if(longVolatile==-1&&(biaseLock!=null&&biaseLock.equals("1"))&&(lockFlag!=null&&lockFlag.equals("01"))){
            //执行cas操作，将自己的线程id写入markWord
            boolean isOk = unsafe.compareAndSwapLong(markWord, offset, longVolatile, id);
            if(isOk){
                return true;
            }

        }
        //可偏向，并已经偏向某个线程
        else if (longVolatile!=-1&&(biaseLock!=null&&biaseLock.equals("1"))&&(lockFlag!=null&&lockFlag.equals("01"))){
            //判断偏向的线程是否是当前线程
            if(longVolatile==id){
                return  true;
            }
            //撤销偏向锁
            revokeBiased(myLock);
           // return false;
        }
        return false;
    }

    /**
     * 撤销偏向锁
     * @return
     */
   public boolean revokeBiased(MyLock myLock){
       //1.判断线程是否已经离开同步代码块
       //2.线程是否存活
        boolean isAlive=false;

       MarkWord markWord = myLock.getMarkWord();
       long threadId = markWord.getThreadId();
       //获取当前线程组中存活的线程数量
       ThreadGroup threadGroup= Thread.currentThread().getThreadGroup();
       int i = threadGroup.activeCount();
       //获取线程组中的线程
       Thread[] threads = new Thread[i];
       int enumerate = threadGroup.enumerate(threads);
       for(Thread thread:threads){
           //此时表示拥有这把锁的线程依然存活
           if(thread.getId()==threadId){
               isAlive=true;
               break;
           }
       }
       //线程存活，那么此时设置成无锁状态
       if(isAlive){
           markWord.setBiaseLock("0");
           markWord.setBiaseLock("01");
           markWord.setThreadId(-1);
            return true;
       }

       //走轻量级锁
       return false;

    }
}
