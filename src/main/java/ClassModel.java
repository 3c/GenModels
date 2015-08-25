import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by CX on 2015/8/26.
 */
public class ClassModel implements Serializable {

    public String className = "";
    public ArrayList<ClassParamsModel> classParamsList = new ArrayList<>();

    public ClassModel(String _className, ArrayList<ClassParamsModel> _classParamsList) {
        className = _className;
        classParamsList = _classParamsList;
    }

    @Override
    public String toString() {
        return "ClassModel{" +
                "className='" + className + '\'' +
                ", classParamsList=" + classParamsList +
                '}';
    }
}
