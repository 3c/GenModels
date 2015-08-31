import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by CX on 2015/6/30.
 * it's my final fantasy
 * I will be fired , if this works
 * haa haa~~~
 * TODO 3.解析数据响应类和结果说明类  5.考虑是否带有KEY 6.数组形式的特殊处理
 */
public class FinalFantasy {


    private static final Logger logger = LoggerFactory.getLogger(FinalFantasy.class);


    /**
     * 要被弄的文件路径
     */
    public final static String SOURCE_FILE_PATH = "/src/main/java/";


    /**
     * 要被干的文件名
     */
    public final static String SOURCE_FILE_NAME = "new_source.txt";

    /**
     * 生成的文件路径
     */
    public final static String RESULT_FILE_PATH = "/src/main/java/models/";

    /**
     * 生成文件的包名。统一的？
     */
    public final static String RESULT_PACKAGE = "com.s2t.app.yoga.entity";


    public static final String REQ_FILE_NAME = "req_data.json";

    /**
     * 请求类的数据集合
     */
    public static ArrayList<ClassModel> reqDataSource = new ArrayList<>();


    public static final String RESP_FILE_NAME = "resp_data.json";

    /**
     * 解析类的数据集合
     */
    public static ArrayList<ClassModel> respDataSource = new ArrayList<>();

    /**
     * json出来的数据
     */
    public static ArrayList<ClassParamsModel> dataJsonList = new ArrayList<>();


    /**
     * 记录array的model类
     */
//    public static ArrayList<String> listWithArray = new ArrayList<>();
    public static void main(String[] args) {
        logger.info("start");
        genFile();
    }


    public static void genFile() {
        String result = readFile();
//        writeJsonFile(REQ_FILE_NAME, reqDataSource);
//        writeJsonFile(RESP_FILE_NAME, respDataSource);
//        readJsonFile();
        genReq(); //生成req
        genResp();//生成resp


    }

    public static void readJsonFile() {

        try {
            Gson gson = new Gson();
            String str = readStrFile(getProjectPath() + SOURCE_FILE_PATH + REQ_FILE_NAME);
            reqDataSource = gson.fromJson(str, new TypeToken<ArrayList<ClassModel>>() {
            }.getType());
            str = readStrFile(getProjectPath() + SOURCE_FILE_PATH + RESP_FILE_NAME);
            respDataSource = gson.fromJson(str, new TypeToken<ArrayList<ClassModel>>() {
            }.getType());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readStrFile(String fileName) throws IOException {
        String res = "";
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            String e = "";
            StringBuffer buffer = new StringBuffer();

            while ((e = br.readLine()) != null) {
                buffer.append(e);
            }

            res = buffer.toString();
        } catch (Exception var8) {
            var8.printStackTrace();
        } finally {
            br.close();
        }
        return res;
    }


    public static void writeJsonFile(String jsonFileName, Object jsonFileContent) {
        Gson gson = new Gson();
        String result = gson.toJson(jsonFileContent);
        saveStr2File(result, jsonFileName);
    }

    /**
     * 生成req
     */
    public static void genReq() {

        logger.info("reqDataSource size " + reqDataSource.size());
        for (ClassModel classModel : reqDataSource) {
            writeReqModel(classModel);
        }
    }

    /**
     * 生成resp
     */
    public static void genResp() {
        logger.info("respDataSource size " + respDataSource.size());
        for (ClassModel classModel : respDataSource) {
            writeRespModel(classModel);
        }
    }

    public static String getLineValue(String line) {
        if (StringUtil.isEmpty(line)) {
            return null;
        }
        String result = null;
        try {
            result = line.substring(line.indexOf(":") + 1, line.length()).trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @return 读取文件
     */
    public static String readFile() {

        String courseFile = getProjectPath();
        String everything = "";


        String path = courseFile + SOURCE_FILE_PATH + SOURCE_FILE_NAME;

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
//            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            int methodCount = 0;


            String requestType = ClassModel.REQ_TYPE_POST;
            String respType = ClassModel.RESP_TYPE_OBJECT;
            String classComments = "";

            String className = null;//类名.从h3取得
            ArrayList<ClassParamsModel> reqModelList = new ArrayList<>();//req字段类
            ArrayList<ClassParamsModel> respModelList = new ArrayList<>();//resp字段类

            boolean isReqParams = false; //请求类的标识
            boolean isRespParams = false;//解析类的标识

            while (line != null) {

                //基础逻辑。- 是关键位置。标志每一个method的开始。旗下有三种框，请求的，响应结果，和返回字段

                //取类名
                if (line.startsWith("-")) {

                    //h3 标志 resp结束
                    isRespParams = false;

                    methodCount++;

                    if (!StringUtil.isEmpty(className)) {
                        addReqClass(getClassModel(className, reqModelList, requestType, respType, classComments));
                        reqModelList = new ArrayList<>();
                    }

                    if (!StringUtil.isEmpty(className)) {
                        addRespClass(getClassModel(className, respModelList, requestType, respType, classComments));
                        respModelList = new ArrayList<>();
                    }


                    classComments = line + " ";
                }

                //read req
                if (isReqParams) {
                    ClassParamsModel classModel = getClassParamsModel(line);
                    if (classModel != null) {
                        reqModelList.add(classModel);
                    }
                }

                //read resp
                if (isRespParams) {
                    ClassParamsModel classModel = getClassParamsModel(line);
                    if (classModel != null) {
                        respModelList.add(classModel);
                    }
                }

                if (line.contains("请求链接")) {
                    className = getLineValue(line);
                    logger.info("className---> " + className);
                } else if (line.contains("请求方式")) {
                    requestType = getLineValue(line).toLowerCase();
                    if (StringUtil.isEmpty(requestType)) {
                        requestType = ClassModel.REQ_TYPE_POST;
                    }
                } else if (line.contains("请求说明")) {
                    String comments = getLineValue(line);
                    if (!StringUtil.isEmpty(comments)) {
                        classComments += comments;
                    }
                } else if (line.contains("请求参数")) {
                    //这里标识可以取req数据了
                    isReqParams = true;
                } else if (line.contains("返回结果")) {
                    //停止读取req用的字段
                    isReqParams = false;
                } else if (line.contains("返回数据")) {
                    //开始读取resp
                    isRespParams = true;
                    if (line.contains("array")) {
                        respType = ClassModel.RESP_TYPE_ARRAY;
                    } else {
                        respType = ClassModel.RESP_TYPE_OBJECT;
                    }

                }
                line = br.readLine();
            }

            if (reqModelList != null) {
                //最后一次，如果记录到了。就要搞进去
                addReqClass(getClassModel(className, reqModelList, requestType, respType, classComments));
            }

            if (respModelList != null) {
                //最后一次，如果记录到了。就要搞进去
                addRespClass(getClassModel(className, respModelList, requestType, respType, classComments));
            }
            logger.info("method count " + methodCount);

//            everything = sb.toString();
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return everything;
    }


    /**
     * 添加请求类
     *
     * @param _classModel
     */
    public static void addReqClass(ClassModel _classModel) {
        String reqType = _classModel.requestType;
        //内部类不需要请求
        if ("inner".equals(reqType)) {
            return;
        }
        reqDataSource.add(_classModel);
    }

    /**
     * 添加响应类
     *
     * @param _classModel
     */
    public static void addRespClass(ClassModel _classModel) {
        respDataSource.add(_classModel);
    }

    /**
     * 根据给定的字段，获取值
     *
     * @param className
     * @param respModelList
     * @param requestType
     * @param respType
     * @param classComments
     * @return
     */
    public static ClassModel getClassModel(String className, ArrayList<ClassParamsModel> respModelList, String requestType, String respType, String classComments) {
        ClassModel classModel = new ClassModel(className, respModelList);
        classModel.requestType = requestType;
        classModel.respType = respType;
        classModel.comments = classComments;
        return classModel;

    }

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static String getCurrentTime() {
        long time = System.currentTimeMillis();
        String date = sdf.format(new Date(time));
        return date;
    }

    /**
     * 生成req类
     */
    public static void writeReqModel(ClassModel _classModel) {

        String _className = _classModel.className; //类名
        ArrayList<ClassParamsModel> _list = _classModel.classParamsList;

        String classComments = "api : " + _className + "  " + _classModel.comments; //类的注释

        boolean isArrayResp = false;

        if (ClassModel.RESP_TYPE_ARRAY.equals(_classModel.respType)) {
            isArrayResp = true;
        }

        _className = getClassName(_className);
        logger.info("_className " + _className);

        String packageAndImport = "package " + RESULT_PACKAGE + ";\n\n" +
                "import android.content.Context;\n" +
                "import com.gm.common.utils.LogUtil;\n" +
                "import com.gm.common.utils.StringUtils;\n" +
                "import com.gm.lib.net.AbsGMRequest;\n" +
                "import com.gm.lib.net.GMNetRequest;\n" +
                "import com.gm.lib.net.GMApiHandler;\n" +
                "import com.gm.lib.net.ReqParams;\n\n";

        String reqClassName = _className + "Req";

        String className = "/**\n" +
                " * Created by auto.\n" +
                " * " + classComments + "\n" +
                " * date: " + getCurrentTime() + "\n" +
                " */\n" +
                "public class " + reqClassName + " extends AbsGMRequest {\n\n";

//        String keys = "";
        String reqParams = "";
        String jsonStr = "";

        for (ClassParamsModel classModel : _list) {

//            keys += "    private final static String KEY_" + classModel.name.toUpperCase() + " = \"" + classModel.name + "\";\n";
            String type = classModel.type;
            if (type.equals("string")) {
                type = "String";
            } else if (type.equals("int")) {
                type = "int";
            } else if (type.equals("array")) {
                //数组格式的
                type = "ArrayList<" + classModel.name + ">"; //列表的暂时还没办法抓取,统一为String
            } else {
                type = "Object";
            }

            reqParams += "    public " + type + " " + classModel.name + ";\n";


            jsonStr += "        reqParams.put(\"" + classModel.name + "\", " + classModel.name + "); //" + classModel.instruction + "\n";


        }

        String allJsonStr;
        if ("".equals(jsonStr) || ClassModel.REQ_TYPE_GET.equals(_classModel.requestType)) {
            //json 字段是空的 或者 请求是get
            allJsonStr = "    @Override\n" +
                    "    public ReqParams getRequestParams() {\n" +
                    "        return null;\n" +
                    "    }";
        } else {
            allJsonStr = "    @Override\n" +
                    "    public ReqParams getRequestParams() {\n" +
                    "        ReqParams reqParams = new ReqParams();\n" +
                    jsonStr +
                    "        return reqParams;\n" +
                    "    }";
        }

        String action = getReqAction(_classModel.className);

        if (_classModel.className.contains("/:")) {
            //id 的默认只有一个，取第一个
            action += "/\"+" + _classModel.classParamsList.get(0).name;
        } else if (_classModel.className.contains("?")) {
            for (int i = 0; i < _classModel.classParamsList.size(); i++) {
                String paramsName = _classModel.classParamsList.get(i).name;
                if (i == 0) {
                    action += "?" + paramsName + "=\"+" + paramsName;
                } else {
                    action += "&" + paramsName + "=\"+" + paramsName;
                }
            }
        } else {
            action += "\"";
        }

        //返回的resp类。要判断是不是array
        String respClass = _className + "Resp";
        if (isArrayResp) {
            respClass += "[]";
        }

        String httpType = "post";
        if (ClassModel.REQ_TYPE_GET.equals(_classModel.requestType)) {
            httpType = "get";
        }

        String httpMethod = "    /**\n" +
                "     * 网络请求 哦呼\n" +
                "     * @param _context      引用\n" +
                "     * @param _gmApiHandler 回调的参数\n" +
                "     */\n" +
                "    public void httpData(Context _context, GMApiHandler<" + respClass + "> _gmApiHandler) {\n" +
                "        GMNetRequest.getInstance()." + httpType + "(_context, this, _gmApiHandler);\n" +
                "    }";

        String endStr = "    @Override\n" +
                "    public Class getJsonCls() {\n" +
                "        return " + respClass + ".class;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public String getUrl() {\n" +
                "        return BaseUrl + \"/" + action + ";\n" +
                "    }\n" +
                "\n" +
                allJsonStr +
                "\n" +
                "\n" +
                httpMethod + "\n\n" +
                "}";

//        String finalResultContent = packageAndImport + className + keys + "\n\n" + reqParams + "\n\n" + endStr;

        String finalResultContent = packageAndImport + className + "\n" + reqParams + "\n\n" + endStr;

        saveStr2File(finalResultContent, reqClassName + ".java");


    }


    /**
     * 生成resp类
     */
    public static void writeRespModel(ClassModel _classModel) {

        logger.info(_classModel.toString());

        String _className = _classModel.className;
        ArrayList<ClassParamsModel> _list = _classModel.classParamsList;

        String classComments = "api : " + _className + "  " + _classModel.comments; //类的注释

        _className = getClassName(_className);

        String packageAndImport = "package " + RESULT_PACKAGE + ";\n\n" +
                "import java.io.Serializable;\n" +
                "import java.util.ArrayList;\n\n";
        String respClassName = _className + "Resp";

        if ("inner".equals(_classModel.requestType)) {
            respClassName = _className + "Model";
        }

        String className = "/**\n" +
                " * Created by auto.\n" +
                " * " + classComments + "\n" +
                " * date: " + getCurrentTime() + "\n" +
                " */\n" +
                "public class " + respClassName + " implements Serializable {\n\n";

        String respParams = "";
        String toString = ""; //log


        for (ClassParamsModel classModel : _list) {

            String type = classModel.type;
            String finalClassParamsName = classModel.name;
            if (type.equals("string")) {
                type = "String";
                finalClassParamsName += " = \"\"";
            } else if (type.equals("int")) {
                type = "int";
            } else if (type.contains(":")) {
                String[] arrs = type.split(":");
                String realType = arrs[0];
                String innerClassName = arrs[1];
                innerClassName = innerClassName.replaceFirst(innerClassName.substring(0, 1), innerClassName.substring(0, 1).toUpperCase());
                if (!"String".equals(innerClassName)) {
                    //如果不是string，就上Model
                    innerClassName += "Model";
                }
                if ("array".equals(realType)) {
                    type = "ArrayList<" + innerClassName + ">"; //列表的暂时还没办法抓取,统一为String
                    finalClassParamsName += " = new ArrayList<>()";
                } else {
                    type = innerClassName;
                    finalClassParamsName += " = new " + innerClassName + "()";
                }
            } else {
                type = "Object";
            }

            respParams += "    public " + type + " " + finalClassParamsName + ";  // " + classModel.instruction + "\n";
            toString += "                \"" + classModel.name + "='\" + " + classModel.name + " + '\\'' +\n";

        }

        String endStr = "    @Override\n" +
                "    public String toString() {\n" +
                "        return \"" + respClassName + "{\" +\n" +
                toString +
                "                '}';\n" +
                "    }" +
                "\n\n" +
                "}";

        String finalResultContent = packageAndImport + className + "\n" + respParams + "\n\n" + endStr;

        saveStr2File(finalResultContent, respClassName + ".java");


    }


    /**
     * @return 获取工程的路径
     */
    public static String getProjectPath() {
        File directory = new File("");//参数为空
        String courseFile = directory.getAbsolutePath();
        logger.info("projectPath--> {}", courseFile);
        return courseFile;
    }


    /**
     * 文件保存
     *
     * @param content
     */
    public static void saveStr2File(String content, String fileName) {

        FileOutputStream fos = null;
        try {

            String courseFile = getProjectPath();
            String path = courseFile + RESULT_FILE_PATH;
            File fileSave = new File(path);
            if (!fileSave.exists()) {
                fileSave.mkdirs();
            }
            File file = new File(path, fileName);
            fos = new FileOutputStream(file);
            fos.write(content.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * @param line
     * @return 取得类名
     */
    public static String getClassName(String line) {
//       /api/user/bind/:userId
        String className = line;

        //去掉?
        if (className.contains("?")) {
            className = className.substring(0, className.lastIndexOf("?"));
        }

        //去掉id
        if (className.contains("/:")) {
            className = className.substring(0, className.lastIndexOf("/"));
        }
        int length = className.indexOf("/");

        if (length != -1) {
            String[] arr = className.split("/");
            className = "";
            for (String str : arr) {
                if (!StringUtil.isEmpty(str)) {
                    className += str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toUpperCase());
                }
            }
        } else {
            className = className.replaceFirst(className.substring(0, 1), className.substring(0, 1).toUpperCase());
        }
        return className;
    }

    /**
     * @param line
     * @return 取得action
     */
    public static String getReqAction(String line) {
//        h3. [201]用户注册接口( /signup)
        String className = line;
        //去掉?
        if (className.contains("?")) {
            className = className.substring(0, className.lastIndexOf("?"));
        }
        //去掉id
        if (className.contains("/:")) {
            className = className.substring(0, className.lastIndexOf("/"));
        }
        return className;
    }

    /**
     * @param line
     * @return 取得参数的类
     */
    public static ClassParamsModel getClassParamsModel(String line) {
        if (StringUtil.isEmpty(line)) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("class model line:" + line + "\n");
        String[] arr = line.split("\\|");
        stringBuilder.append("arr.length " + arr.length + "\n");
        if (arr.length < 3) {
            //至少两个字段，name  和 type.加上第一个空格的，所以是3个
            return null;
        }
        //过滤掉参数名称
        String firstParams = arr[0].trim();
        stringBuilder.append("firstParams:" + firstParams + "\n");
        if ("名字".equals(firstParams) || "---".equals(firstParams)) {
            return null;
        }
        ClassParamsModel classModel = new ClassParamsModel();
        classModel.name = arr[0].trim();
        classModel.type = arr[1].trim();
        //下面两个字段不是一直都有
        if (arr.length > 2) {
            classModel.instruction = arr[2].trim();
        }
        if (arr.length > 3) {
            classModel.comments = arr[3].trim();
        }

        logger.info("message " + stringBuilder.toString());
        return classModel;
    }

}

