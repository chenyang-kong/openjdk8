package day6;

/**
 * 对象头
 */
public class MarkWord implements Cloneable{
    //锁标记
    private String lockFlag="01";//无锁或偏向锁
    private String biaseLock="1";//默认偏向锁
    private String epoch;
    private String age;
    private volatile long threadId=-1;
    private volatile String status=null;
    //指向轻量级锁的指针
    private volatile LockRecord lockRecord;

    //指向重量级锁的指针
    private ObjectMonitor PrtMonitor=null;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }



    public String getLockFlag() {
        return lockFlag;
    }

    public void setLockFlag(String lockFlag) {
        this.lockFlag = lockFlag;
    }

    public ObjectMonitor getPrtMonitor() {
        return PrtMonitor;
    }

    public void setPrtMonitor(ObjectMonitor prtMonitor) {
        PrtMonitor = prtMonitor;
    }

    public LockRecord getLockRecord() {
        return lockRecord;
    }

    public void setLockRecord(LockRecord lockRecord) {
        this.lockRecord = lockRecord;
    }

    public String getBiaseLock() {
        return biaseLock;
    }

    public void setBiaseLock(String biaseLock) {
        this.biaseLock = biaseLock;
    }

    public String getEpoch() {
        return epoch;
    }

    public void setEpoch(String epoch) {
        this.epoch = epoch;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
