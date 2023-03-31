package day6;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 重量级锁
 */
public class ObjectMonitor {
    private int recursions=0; //记录重入次数
    private volatile Thread owner; //标识拥有该锁的线程
    private LinkedBlockingDeque WaitSet;//等待队列，执行wait后，线程插入到该队列，双向链表
    private LinkedBlockingQueue cxq=new LinkedBlockingQueue() ;//多个线程争抢锁，会进入这个队列,单向链表
    private LinkedBlockingQueue EntryList;//这里存放竞争锁失败的线程,单向链表

    //重量级锁入口
    public void enter(MyLock myLock) throws NoSuchFieldException {
        //1.cas修改owner为当前线程
        Thread currentThread = cmpAndChgOwner(myLock);
        if(currentThread==null){
            return;
        }
        //2.如果之前owner为当前线程，锁重入recursions加1
        if(currentThread==Thread.currentThread()){
            recursions++;
            return;
        }
        //3.从轻量级锁膨胀来的
        LockRecord lockRecord = MySynchronized.threadLocal.get();
        MarkWord head = lockRecord.getMarkWord();
        if(head!=null){
            recursions=1;
            owner=Thread.currentThread();
            return;
        }
        //4.预备入队挂起
        enterI( myLock);
    }

    /**
     * cas修改owner字段
     * @param myLock
     * @return
     */
    public Thread cmpAndChgOwner(MyLock myLock){
        Unsafe unsafe = MyUnsafe.getUnsafe();
        ObjectMonitor prtMonitor = myLock.getMarkWord().getPrtMonitor();
        Field owner = null;
        try {
            owner = prtMonitor.getClass().getDeclaredField("owner");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        long offset = unsafe.objectFieldOffset(owner);
        Thread currentThread = Thread.currentThread();
        boolean isOk = unsafe.compareAndSwapObject(prtMonitor, offset, null, currentThread);

        //修改成功返回null
        if(isOk){
            return null;
        }
        //修改失败返回当前线程
        Thread owner1 = prtMonitor.getOwner();
        return owner1;
    }


    private void enterI(MyLock myLock){
        //自旋10次获取锁
        for(int i=0;i<10;i++){
            if(tryLock(myLock)>0) {
                return;
            }
        }

        try {
            //模拟延迟
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(int i=0;i<10;i++){//尝试10次获取锁
            if(tryLock(myLock)>0) {
                return;
            }
        }
        //到此，获取所失败，开始入队
        ObjectWaiter objectWaiter = new ObjectWaiter(Thread.currentThread());
        for(;;){
            try {
                cxq.put(objectWaiter);
                break;
            } catch (Exception e) {
                if(tryLock(myLock)>0) {
                    return;
                }
            }
        }

        //真正阻塞
        for(;;){
            if(tryLock(myLock)>0) {
                break;
            }
            Unsafe unsafe = MyUnsafe.getUnsafe();
            unsafe.park(false, 0L);//线程挂起，卡死，等待唤醒
            //唤醒后抢锁
            if(tryLock(myLock)>0) {
                break;
            }
        }


    }
    //获取锁
    private int tryLock(MyLock myLock){
        for(;;){
            //如果有线程占用重量级锁锁，直接退出
            if(owner!=null){
                return 0;
            }

            Thread thread = cmpAndChgOwner(myLock);
            //cas成功
            if(thread==null){
                return 1;
            }
            //cas失败
            return -1;
        }
    }

    //重量级锁释放
    public void exit(MyLock myLock){
        Thread thread = Thread.currentThread();
        if(owner!=thread){
            LockRecord lockRecord = MySynchronized.threadLocal.get();
            MarkWord head = lockRecord.getMarkWord();
            if(head!=null){//从轻量级锁过来
                owner=thread;
                recursions=0;
            }else{
                throw new RuntimeException("不是锁的拥有者，无权释放该锁");
            }
        }
        if(recursions!=0){
            recursions--;
            return;
        }

        //开始选择唤醒模式
        //触发屏障
        MyUnsafe.getUnsafe();

        //从队列里获取一个线程，准备唤醒
        ObjectWaiter objectWaiter = (ObjectWaiter) cxq.poll();
        if(objectWaiter!=null){
            exitEpilog(myLock,objectWaiter);
        }
    }
    private void exitEpilog(MyLock myLock,ObjectWaiter objectWaiter ){
        //丢弃锁，将owner置为null
       myLock.getMarkWord().getLockRecord().setOwner(null);
        Unsafe unsafe = MyUnsafe.getUnsafe();
        //唤醒线程
        Thread thread = objectWaiter.getThread();
        unsafe.unpark(thread);
        myLock.getMarkWord().setLockRecord(null);
        objectWaiter=null;

    }

    public int getRecursions() {
        return recursions;
    }

    public void setRecursions(int recursions) {
        this.recursions = recursions;
    }

    public Thread getOwner() {
        return owner;
    }

    public void setOwner(Thread owner) {
        this.owner = owner;
    }

    public LinkedBlockingDeque getWaitSet() {
        return WaitSet;
    }

    public void setWaitSet(LinkedBlockingDeque waitSet) {
        WaitSet = waitSet;
    }

    public LinkedBlockingQueue getCxq() {
        return cxq;
    }

    public void setCxq(LinkedBlockingQueue cxq) {
        this.cxq = cxq;
    }

    public LinkedBlockingQueue getEntryList() {
        return EntryList;
    }

    public void setEntryList(LinkedBlockingQueue entryList) {
        EntryList = entryList;
    }
}
