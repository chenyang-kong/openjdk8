package day10;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MySimpliThreadPool {
    private int corePoolSize;
    //定义一个队列，用于存放任务
    private static final LinkedList<Runnable> TASKQUEUE=new LinkedList<>();

    private static final List<WorkerThread> THREADQUEUE=new ArrayList<>();

    private static final int DEFAULT_SIZE=5;

    private DiscardPolicy discardPolicy;

    private int maxmumPoolSize;

    private static final int DEFAULT_MAX_THREAD_SIZE=40;

    //线程池是否已经关闭
    private boolean isClose=false;

    private static DiscardPolicy DEFAULT_POLICY=()->{
        throw new RuntimeException("任务超出预期！");
    };

    //默认构造
    public MySimpliThreadPool() {
        this(DEFAULT_SIZE,DEFAULT_MAX_THREAD_SIZE,DEFAULT_POLICY);
    }
    //有参构造
    public MySimpliThreadPool(int corePoolSize,int maxmumPoolSize,DiscardPolicy discardPolicy) {
        this.corePoolSize = corePoolSize;
        this.discardPolicy=discardPolicy;
        this.maxmumPoolSize=maxmumPoolSize;
    }

    public static void main(String[] args) {
        MySimpliThreadPool mySimpliThreadPool = new MySimpliThreadPool(10,60,MySimpliThreadPool.DEFAULT_POLICY);
        mySimpliThreadPool.init();
        for(int i=0;i<50;i++){
            int finalI = i;
            mySimpliThreadPool.submit(()->{
                try {
                    Thread.sleep(1000);
                    System.out.println("========当前线程"+Thread.currentThread().getName()+"处理任务"+ finalI);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     *初始化线程池
     */
    private void init(){
        for(int i=0;i<5;i++){
            creatThread();
        }
    }
    //创建任务
    private void creatThread(){
        WorkerThread workerThread = new WorkerThread();
        workerThread.start();
        THREADQUEUE.add(workerThread);
    }
    //提交任务
    private void submit(Runnable task){
        if(isClose){
            throw new RuntimeException("线程池已经关闭,任务不可再提交");
        }
        synchronized (TASKQUEUE){
            if(THREADQUEUE.size()>maxmumPoolSize){
                System.out.println("当前任务队里中的任务数："+THREADQUEUE.size());
                this.discardPolicy.discard();
            }

            TASKQUEUE.addLast(task);
            TASKQUEUE.notifyAll();
        }
    }

    private void shutdown(){
        //先检查任务队列，队列为空才继续检查线程队列
        while (!TASKQUEUE.isEmpty()){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        synchronized (THREADQUEUE){
            int size = THREADQUEUE.size();
            while (size>0){
                for(WorkerThread workerThread:THREADQUEUE){
                    //判断阻塞状态
                    if(workerThread.state==WorkerThreadState.BLOCK){
                        workerThread.interrupt();
                        workerThread.close();
                        size--;
                    }

                    else if(workerThread.state==WorkerThreadState.RUNNING){
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        System.out.println("线程池已经关闭");
    }

    //线程池状态
    private enum WorkerThreadState{
        FERR,BLOCK,RUNNING,DEAD
    }

    //工作线程
    public class WorkerThread extends Thread {
        private volatile WorkerThreadState state=WorkerThreadState.FERR;
        @Override
        public void run() {
            OUTER:
            while(state!=WorkerThreadState.DEAD){
                Runnable task;
                synchronized (TASKQUEUE){
                    while (TASKQUEUE.isEmpty()){
                        try {
                            this.state=WorkerThreadState.DEAD;
                            TASKQUEUE.wait();
                        } catch (InterruptedException e) {
                            //e.printStackTrace();
                            break OUTER;
                        }
                    }
                    //从任务队列中取一个任务
                    task = TASKQUEUE.remove();

                }
                //执行任务
                if(task!=null){
                    this.state=WorkerThreadState.RUNNING;
                    task.run();
                    this.state=WorkerThreadState.FERR;
                }
            }
        }

        private void close(){
            this.state=WorkerThreadState.DEAD;
        }
    }

    //拒绝策略
    public  interface  DiscardPolicy{
        void discard();
    }
}
