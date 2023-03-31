package day2;

import java.io.IOException;

/**
 *  第三方代码
 */
public class testSaveThread {
    public static void main(String[] args) {
        new Thread(new SaveThread() {
            @Override
            public void protectMethod() {
                System.out.println("save Method");
                //意图删除我文件
                //此处的代码受保护，不可以删掉文件
//                try {
//                    Runtime.getRuntime().exec("cmd.exe /c del d:\\text.txt /q");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }).start();
        //测试 此处的代码不受保护，可以删掉文件
        try {
            Runtime.getRuntime().exec("cmd.exe /c del d:\\text.txt /q");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
