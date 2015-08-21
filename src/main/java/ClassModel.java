/**
 * Created by CX on 2015/6/30.
 */
public class ClassModel {
    /**
     * 字段名称
     */
    public String name;
    /**
     * 字段类型
     */
    public String type;
    /**
     * 说明
     */
    public String instruction;
    /**
     * 备注
     */
    public String comments;

    @Override
    public String toString() {
        return "ClassModel{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", instruction='" + instruction + '\'' +
                ", comments='" + comments + '\'' +
                '}';
    }
}
