package Set;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

public class test {
    public static void main(String[] args) {
        // ========== Set集合的特点：无序、不重复 ==========
        System.out.println("===== 1. 基本操作演示 =====");
        Set<String> set = new HashSet<>();

        // 添加元素（返回boolean，如果元素已存在则返回false）
        boolean result1 = set.add("json");
        System.out.println("添加'json': " + result1);  // true

        boolean result2 = set.add("ken");
        System.out.println("添加'ken': " + result2);   // true

        boolean result3 = set.add("ke");
        System.out.println("添加'ke': " + result3);    // true

        // 尝试添加重复元素
        boolean result4 = set.add("json");
        System.out.println("再次添加'json': " + result4);  // false（因为已存在）

        System.out.println("集合内容: " + set);
        System.out.println("集合大小: " + set.size());  // 3

        // ========== 2. 遍历Set的三种方式 ==========
        System.out.println("\n===== 2. 遍历Set =====");

        // 方式1：增强for循环（推荐）
        System.out.print("增强for循环: ");
        for (String item : set) {
            System.out.print(item + " ");
        }
        System.out.println();

        // 方式2：迭代器
        System.out.print("迭代器遍历: ");
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            System.out.print(iterator.next() + " ");
        }
        System.out.println();

        // 方式3：forEach方法（Java 8+）
        System.out.print("forEach方法: ");
        set.forEach(item -> System.out.print(item + " "));
        System.out.println();

        // ========== 3. 删除操作 ==========
        System.out.println("\n===== 3. 删除操作 =====");
        boolean removed = set.remove("ke");
        System.out.println("删除'ke': " + removed);  // true
        System.out.println("删除后集合: " + set);

        // 清空集合
        // set.clear();

        // ========== 4. 查询操作 ==========
        System.out.println("\n===== 4. 查询操作 =====");
        System.out.println("是否包含'json': " + set.contains("json"));  // true
        System.out.println("是否为空: " + set.isEmpty());               // false

        // ========== 5. 自定义对象的去重演示 ==========
        System.out.println("\n===== 5. 自定义对象去重 =====");
        Set<Student> studentSet = new HashSet<>();
        studentSet.add(new Student("张三", 20));
        studentSet.add(new Student("李四", 22));
        studentSet.add(new Student("张三", 20));  // 如果Student正确重写了equals和hashCode，这会被认为是重复的

        System.out.println("学生集合大小: " + studentSet.size());
        System.out.println("学生集合内容: " + studentSet);
        System.out.println("注意：要实现去重，需要在Student类中重写equals()和hashCode()方法");
        for(Student student : studentSet)
            System.out.println(student.toString());
    }
}

// 自定义学生类，演示如何正确实现去重
class Student {
    String name;
    int age;

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // 重写equals方法：姓名和年龄相同就认为是同一个学生
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student student = (Student) obj;
        return age == student.age && name.equals(student.name);
    }

    // 重写hashCode方法：必须与equals保持一致
    @Override
    public int hashCode() {
        return name.hashCode() * 31 + age;
    }

    @Override
    public String toString() {
        return "Student{name='" + name + "', age=" + age + "}";
    }
}
