package day3;

public class HookThread {
    public static void main(String[] args) {
        //创建钩子线程
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("执行钩子线程");
        }));
    }
}
