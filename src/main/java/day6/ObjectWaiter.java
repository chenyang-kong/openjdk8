package day6;

public class ObjectWaiter {
    private Thread thread;

    public ObjectWaiter(Thread thread) {
        this.thread = thread;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
}
