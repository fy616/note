package 实验4.experiment4_2;

/**
 * CD类：创建光盘对象，存储光盘信息
 */
public class CD {
    // 光盘的私有成员变量
    private String cdName;   // 光盘名称
    private String cdType;   // 光盘类型（音乐/数据/视频）

    // 构造方法：创建CD对象时初始化信息
    public CD(String cdName, String cdType) {
        this.cdName = cdName;
        this.cdType = cdType;
    }

    // 获取光盘名称
    public String getCdName() {
        return cdName;
    }

    // 获取光盘类型
    public String getCdType() {
        return cdType;
    }
}
