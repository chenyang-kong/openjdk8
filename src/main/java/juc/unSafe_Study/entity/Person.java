package juc.unSafe_Study.entity;

public class Person {
    private int id;
    private Man man;
    private static String defaultString="dafaultString.....";

    public Person() {
    }

    public Person(int id, Man man) {
        this.id = id;
        this.man = man;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Man getMan() {
        return man;
    }

    public void setMan(Man man) {
        this.man = man;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", man=" + man +
                '}';
    }
}
