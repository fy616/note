package 实验2.experiment2_3;

public class HandleLargeNumber {
    public static void main(String args[]) {
        int a[] = {0,9,9,7,9,4,5,6,7,2,3,4,5,6,4,7,8,9,8,7,6,9};
        int b[] = {0,0,5,9,1,6,4,5,6,2,3,4,5,7,2,1,3,4,0,3,2,9};
        int c[]= new int[a.length];
        int i = 0,result =0,k=0;
        for(i=0;i<a.length;i++) {
            if(a[i]!=0) {
                k=i;
                break;
            }
        }
        for(i=k;i<a.length;i++) {
            System.out.printf("%d",a[i]);
        }
        System.out.printf("\n加上:\n");
        for(i=0;i<b.length;i++) {
            if(b[i]!=0) {
                k=i;
                break;
            }
        }
        for(i=k;i<b.length;i++) {
            System.out.printf("%d",b[i]);
        }
        for(i=a.length-1;i>=0;i--) {
            result = a[i]+b[i];
            if(result>=10) {
                c[i] = result%10;
                a[i-1] = a[i-1]+1;
            }
            else
                c[i] = result;
        }
        System.out.printf("\n等于:\n");
        for(i=0;i<c.length;i++) {
            if(c[i]!=0) {
                k=i;
                break;
            }
        }
        for(i=k;i<c.length;i++) {
            System.out.printf("%d",c[i]);
        }
    }
}