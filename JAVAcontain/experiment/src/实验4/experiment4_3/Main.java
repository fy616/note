package 实验4.experiment4_3;

public class Main {
    static void main(String[] args) {
        FamilyPerson.setSurname("li");
        FamilyPerson father = new FamilyPerson("ms");
        FamilyPerson son = new FamilyPerson("m");
        FamilyPerson sontow = new FamilyPerson("s");
        System.out.println(father.getName());
        System.out.println(FamilyPerson.surname);
        System.out.println(son.getName());
        System.out.println(FamilyPerson.surname);
        System.out.println(sontow.getName());
        System.out.println(FamilyPerson.surname);



    }
}
