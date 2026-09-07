package 实验4.experiment4_3;

public class FamilyPerson {
    public static String surname;
    private String name;
    public static void setSurname(String s) {
        surname = s;
    }
    public FamilyPerson() {

    }
    //有参
    public FamilyPerson(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
