import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cx on 8/21/15.
 */
public class TestMain {

    private static final Logger logger = LoggerFactory.getLogger(TestMain.class);


    static TestMain testMain;

    public static void main(String[] args) {
        String className = "/api/user/bind/:userId";
        System.out.println("final cls Name : " + getClassName(className));
//        System.out.println(TestMain.class.getPackage().getName());
//        URL location = TestMain.class.getProtectionDomain().getCodeSource().getLocation();
//        System.out.println(location.getFile());
    }

    public static String getClassName(String line) {
//       /api/user/bind/:userId
        String className = line;
        //去掉id
        if (className.contains("/:")) {
            className = className.substring(0, className.lastIndexOf("/"));
        }
        int length = className.indexOf("/");

        if (length != -1) {
            String[] arr = className.split("/");
            className = "";
            for (String str : arr) {
                if(!StringUtil.isEmpty(str)){
                    className += str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toUpperCase());
                }
            }
        } else {
            className = className.replaceFirst(className.substring(0, 1), className.substring(0, 1).toUpperCase());
        }
        return className;
    }

}
