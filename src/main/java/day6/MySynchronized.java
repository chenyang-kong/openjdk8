package day6;

import jdk.nashorn.internal.ir.CallNode;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class MySynchronized {
    private boolean UseBiasedLocking=true;
    static MyLock myLock=new MyLock();
    static BiasedLocking biasedLocking= new BiasedLocking();
    static ThreadLocal<LockRecord> threadLocal= new ThreadLocal(){
        @Override
        protected LockRecord initialValue() {
            MarkWord markWord = myLock.getMarkWord();
            MarkWord owner=null;
            MarkWord markWordClone=null;
//            try {
//                 markWordClone = (MarkWord)markWord.clone();
//            } catch (CloneNotSupportedException e) {
//                e.printStackTrace();
//            }
            LockRecord lockRecord = new LockRecord(markWordClone, owner);
            return lockRecord;
        }
    };

    /**
     * 加锁入口
     */
    public  void monitorEnter(){
        /**
         * 无锁---》偏向锁---》轻量级锁---》重量级锁
         */
        //是否开启偏向锁
        if(UseBiasedLocking){
            fastEnter();
        }
        //走轻量级锁:先膨胀--》重量级锁
        else{
            slowEnter();
       }
    }

    //偏向锁入口
    private void fastEnter() {
        if(UseBiasedLocking){
            boolean isOk = biasedLocking.revokeAndRebias(myLock);
            if(isOk){
                return;
            }
        }
        //走轻量级
        slowEnter();
    }

    /**
     * 释放锁出口
     */
    public  void monitorExit(){
        MarkWord markWord = myLock.getMarkWord();
        String lockFlag = markWord.getLockFlag();
        String biaseLock = markWord.getBiaseLock();
        long threadId = markWord.getThreadId();
        long id = Thread.currentThread().getId();
        //谁拿到锁谁释放
        if(biaseLock!=null&&"1".equals(biaseLock)&&(lockFlag!=null&&"01".equals(lockFlag))){
           if(threadId!=id){
               throw  new RuntimeException("非法释放");
           }
           return;
        }else{
            //轻量级锁和重量级锁的释放
            slowExit(markWord);
        }
    }

    private void slowExit( MarkWord markWord){
        fastExit(markWord);
    }

    private void fastExit( MarkWord markWord){
        //将MarkWord还原：markWord替换（CAS替换），lockFlag改为01
        //当前线程栈中的锁记录
        LockRecord lockRecord = threadLocal.get();
        //MarkWord中的锁记录
        LockRecord lockRecord1 = markWord.getLockRecord();
        //当前线程栈中的markWord，还原到对象头中
        MarkWord head = lockRecord.getMarkWord();
        if(head!=null){
            Unsafe unsafe = MyUnsafe.getUnsafe();
            Field markWord1 = null;
            try {
                markWord1 = myLock.getClass().getDeclaredField("markWord");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            long offset = unsafe.objectFieldOffset(markWord1);
            Object objectVolatile = unsafe.getObjectVolatile(myLock, offset);
            boolean isOk = unsafe.compareAndSwapObject(myLock, offset, objectVolatile, head);
            if(isOk){
                //cas修改成
                lockRecord.setMarkWord(null);
                lockRecord.setOwner(null);
                markWord.setLockFlag("01");
                return;
            }else{
                //cas修改失败
                try {
                    inflateExit();//膨胀
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 轻量级锁解锁失败，进行锁膨胀
     * @throws NoSuchFieldException
     */
    private void inflateExit() throws NoSuchFieldException {
        //锁膨胀
        ObjectMonitor objectMonitor=inflate();
        //重量级锁释放
        objectMonitor.exit(myLock);
    }



    //轻量级锁入口
    private void slowEnter(){

        MarkWord markWord = myLock.getMarkWord();
        //1.判断是否无锁或者偏向锁
        if(markWord.getLockFlag()!=null && "01".equals(markWord.getLockFlag())){
            markWord.setThreadId(-1);
            markWord.setBiaseLock(null);
            //cas变更lockrecord指针
            Unsafe unsafe = MyUnsafe.getUnsafe();
            try {
                Field lockRecord = markWord.getClass().getDeclaredField("lockRecord");
                long offset = unsafe.objectFieldOffset(lockRecord);
                Object currentLockRecord = unsafe.getObjectVolatile(markWord, offset);
                //获取当前线程的LockRecord
                LockRecord lockRecord1 = threadLocal.get();
                boolean isOk= unsafe.compareAndSwapObject(markWord,offset,currentLockRecord,lockRecord1);
                if(isOk){
                    MarkWord markWordClone = null;
                    try {
                        markWordClone = (MarkWord)markWord.clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    lockRecord1.setMarkWord(markWordClone);
                    markWord.setLockFlag("00");
                    lockRecord1.setOwner(markWord);
                    return;
                }
                //膨胀
                inflateEnter();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        //此时已经是轻量级锁状态
        else if(markWord.getLockFlag()!=null&&"00".equals(markWord.getLockFlag())){
            markWord.setThreadId(-1);
            markWord.setBiaseLock(null);
            //获取当前线程的 lockRecord
            LockRecord lockRecord =threadLocal.get();
            //获取markWord中的lockRecord
            LockRecord lockRecord1 = markWord.getLockRecord();
            //判断当前线程是否拥有锁
            if(lockRecord!=null&&lockRecord1!=null&&lockRecord1==lockRecord){
                return;
            }
            //膨胀
            try {
                inflateEnter();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

        }



    }

    /***
     * 锁膨胀入口
     */
    private void inflateEnter() throws NoSuchFieldException {
        //锁膨胀
        ObjectMonitor objectMonitor=inflate();
        objectMonitor.enter(myLock);
    }

    /***
     * 锁膨胀过程
     * @return
     */
    private ObjectMonitor inflate(){
        for(;;){
            MarkWord markWord = myLock.getMarkWord();
            ObjectMonitor prtMonitor = markWord.getPrtMonitor();
            //1.判断是否已经膨胀成功
            if(prtMonitor!=null){
                return prtMonitor;
            }
            //2.判断是否正在膨胀
            String status = markWord.getStatus();
            if(status!=null&&"inflating".equals(status)){
                continue;
            }

            //3.当前是轻量级锁
            LockRecord lockRecord = markWord.getLockRecord();
            String lockFlag = markWord.getLockFlag();
            if(lockFlag!=null&&"00".equals(lockFlag)&&lockRecord!=null){
                //cas更改markWord状态
                Unsafe unsafe = MyUnsafe.getUnsafe();
                Field status1 = null;
                try {
                    status1 = markWord.getClass().getDeclaredField("status");
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                long offset = unsafe.objectFieldOffset(status1);
                Object objectVolatile = unsafe.getObjectVolatile(markWord, offset);
                boolean isOk= unsafe.compareAndSwapObject(markWord, offset, objectVolatile, "infating");
                //更新失败
                if(!isOk){
                    continue ;
                }
                //更新成功
                ObjectMonitor objectMonitor = new ObjectMonitor();
                markWord.setPrtMonitor(objectMonitor);
                markWord.setLockFlag("10");
                markWord.setLockRecord(null);
                return  objectMonitor;
            }

        }
    }

}
