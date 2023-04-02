package juc.unSafe_Study.entity;

public class Man {
    private int age;
    private String name;

    public Man(){
        System.out.println("constructor no args===");//通过UnSafe获取Man 会打印
    }
    public Man(int age,String name) {
        this.age = age;
        this.name=name;
        System.out.println("constructor has args===");//通过UnSafe获取Man 不会打印
    }
    static {
        System.out.println("stasic ====");//通过UnSafe获取Man 不会打印
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Man{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}
