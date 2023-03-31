package day2;


public class testMain {
    public static void main(String[] args) {
       aClass a= new aClass(new lamdaTest(){
           @Override
           public void testMonth() {
               System.out.println("正常方法aaaaaa");
           }
        } );
        aClass b= new aClass(()->{
            System.out.println("lamda方式");

        });
        a.method();
        b.method();
    }

    static class aClass{
        lamdaTest lamdaTest;
        public aClass() {}

        public aClass(day2.lamdaTest lamdaTest) {
            this.lamdaTest = lamdaTest;
        }

        public void method(){
            if(lamdaTest!=null){
                lamdaTest.testMonth();
            }

        }
    }
}
