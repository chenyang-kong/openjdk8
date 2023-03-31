package jucDesign;

public class MyDemphore {

    private int count;

    public MyDemphore(int count) {
        this.count = count;
    }

    public void acquire() throws InterruptedException {
        while (true){
            synchronized (this){
                if(count>0){
                    this.count--;
                    break;
                }
                this.wait();
            }
        }
    }

    public void release(){
        synchronized (this){
            this.count++;
            this.notifyAll();
        }
    }
}
