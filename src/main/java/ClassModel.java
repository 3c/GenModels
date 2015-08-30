import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by CX on 2015/8/26.
 */
public class ClassModel implements Serializable {


    /**
     * 返回的数据为对象
     */
    public static final String RESP_TYPE_OBJECT = "obj";

    /**
     * 返回的数据为数组
     */
    public static final String RESP_TYPE_ARRAY = "array";


    public static final String REQ_TYPE_GET = "get";

    public static final String REQ_TYPE_POST = "post";


    /**
     * 类名,请求的action 是一起的
     */
    public String className = "";

    /**
     * 类型。是对象 还是 数组
     */
    public String respType = RESP_TYPE_OBJECT;

    /**
     * 请求类型。get or post
     */
    public String requestType = REQ_TYPE_POST;

    /**
     * 注释
     */
    public String comments = "";

    /**
     * 请求的字段列表
     */
    public ArrayList<ClassParamsModel> classParamsList = new ArrayList<>();

    public ClassModel(String _className, ArrayList<ClassParamsModel> _classParamsList) {
        className = _className;
        classParamsList = _classParamsList;
    }

    @Override
    public String toString() {
        return "ClassModel{" +
                "className='" + className + '\'' +
                ", respType='" + respType + '\'' +
                ", requestType='" + requestType + '\'' +
                ", comments='" + comments + '\'' +
                ", classParamsList=" + classParamsList +
                '}';
    }
}
