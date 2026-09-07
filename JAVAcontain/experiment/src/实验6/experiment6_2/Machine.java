package 实验6.experiment6_2;

public class Machine{
    public void checkBag(Goods goods)  throws DangerException{
        if(goods.isDanger()){
            DangerException danger=new DangerException();
		//【代码1】 //抛出danger
            throw danger;
        }
    }
}