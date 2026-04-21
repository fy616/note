package 面向对象案例综合;

public class JD implements  Switch{
    private String name;
    private boolean status;
    public void pres() {
        status=!status;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //有参构造器
    public JD(String name , boolean status) {
        this.name = name;
        this.status =  status;
    }
    //无参构造器
    public JD() {
    }

}
