package 实验3.experiment3_4;

public class test {
    public static void main(String[] args) {
            int[][] a =new int[11][11];
            //打印杨辉三角
        for(int i=0;i<=10;i++){
            for(int j=0;j<=i;j++){
                if(j==0||j==i){
                    a[i][j]=1;
                }
                else{
                    a[i][j]=a[i-1][j-1]+a[i-1][j];
                }
                System.out.print(a[i][j]+" ");
            }
            System.out.println();
        }
    }
}
