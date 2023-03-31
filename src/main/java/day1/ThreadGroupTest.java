package day1;

public class ThreadGroupTest {
    public static void main(String[] args) {
        ThreadGroup threadGroup = new ThreadGroup("g1");
        Thread thread = new Thread(threadGroup, new Runnable() {
            @Override
            public void run() {
                ThreadGroup threadGroup1 = Thread.currentThread().getThreadGroup();
                System.out.println("当前线程"+Thread.currentThread().getName()+",所属线程组："+threadGroup1.getName());
                while (true){

                }
            }
        }, "t1");
        Thread thread2 = new Thread(threadGroup, new Runnable() {
            @Override
            public void run() {
                ThreadGroup threadGroup1 = Thread.currentThread().getThreadGroup();
                System.out.println("当前线程"+Thread.currentThread().getName()+",所属线程组："+threadGroup1.getName());
                while (true){

                }
            }
        }, "t2");
        thread.start();
        thread2.start();

        ThreadGroup mg= Thread.currentThread().getThreadGroup();
        System.out.println("主线程所属组："+mg.getName());
        Thread[] threads = new Thread[mg.activeCount()];
        int enumerate = mg.enumerate(threads);
        for(Thread t:threads){
            System.out.println("线程组中的线程"+t.getName());
            if(t.getName().equals("t2")){
                System.out.println("停止线程"+t.getName());

            }
        }

    }
}
