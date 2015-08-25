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
    public final static String SOURCE_FILE_NAME = "source.txt";

    /**
     * 生成的文件路径
     */
    public final static String RESULT_FILE_PATH = "/src/main/java/models/";

    /**
     * 生成文件的包名。统一的？
     */
    public final static String RESULT_PACKAGE = "com.goumin.tuan.entity";


    public static final String REQ_FILE_NAME="req_data.json";

    /**
     * 请求类的数据集合
     */
    public static ArrayList<ClassModel> reqDataSource = new ArrayList<>();


    public static final String RESP_FILE_NAME="resp_data.json";

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
    public static ArrayList<String> listWithArray = new ArrayList<>();

    public static void main(String[] args) {
        logger.info("start");
        genFile();
    }


    public static void genFile() {
//        String result = readFile();
//        System.out.println(result);
//        writeJsonFile(REQ_FILE_NAME, reqDataSource);
//        writeJsonFile(RESP_FILE_NAME, respDataSource);
        readJsonFile();
        genReq(); //生成req
//        genResp();//生成resp


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

        System.out.println("reqDataSource size " + reqDataSource.size());
        for (ClassModel classModel : reqDataSource) {
            writeReqModel(classModel.className, classModel.classParamsList);
        }
    }

    /**
     * 生成resp
     */
    public static void genResp() {
        System.out.println("respDataSource size " + respDataSource.size());
        for (ClassModel classModel : respDataSource) {
            writeRespModel(classModel.className, classModel.classParamsList);
        }
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

            String className = null;//类名.从h3取得
            ArrayList<ClassParamsModel> reqModelList = new ArrayList<>();//req字段类
            ArrayList<ClassParamsModel> respModelList = new ArrayList<>();//resp字段类

            boolean isReqParams = false; //请求类的标识
            boolean isRespParams = false;//解析类的标识

            while (line != null) {

                //基础逻辑。h3 是关键位置。标志每一个method的开始。旗下有三种框，请求的，响应结果，和返回字段

                //取类名
                if (line.startsWith("h3")) {

                    //h3 标志 resp结束
                    isRespParams = false;

                    methodCount++;
                    //当发现一次h3。就是赋值的时候。那最后一次怎么办？
                    if (className != null && !"".equals(className)) {
                        ClassModel classModel = new ClassModel(className, reqModelList);
                        reqDataSource.add(classModel);
                        reqModelList = new ArrayList<>();
                    }

                    if (className != null && !"".equals(className)) {
                        ClassModel classModel = new ClassModel(className, respModelList);
                        respDataSource.add(classModel);
                        respModelList = new ArrayList<>();
                    }

                    className = line;
//                    System.out.println("className---> "+className);
                }

                //read req
                if (isReqParams) {
                    ClassParamsModel classModel = getClassModel(line);
                    System.out.println("classModel null? " + (classModel == null));
                    if (classModel != null) {
                        reqModelList.add(classModel);
                    }
                }

                //read resp
                if (isRespParams) {
                    ClassParamsModel classModel = getClassModel(line);
                    System.out.println("classModel null? " + (classModel == null));
                    if (classModel != null) {
                        respModelList.add(classModel);
                    }
                }

                if (line.contains("发送请求信息参数")) {
                    //这里标识可以取req数据了
                    isReqParams = true;
                } else if (line.contains("响应状态信息参数")) {
                    //停止读取req用的字段
                    isReqParams = false;
                } else if (line.contains("响应正文data信息参数")) {
                    //开始读取resp
                    isRespParams = true;
                    if (line.contains("array")) {
                        listWithArray.add(className);
                    }

                }
                line = br.readLine();
            }
            if (reqModelList != null) {
                //最后一次，如果记录到了。就要搞进去
                ClassModel classModel = new ClassModel(className, reqModelList);
                reqDataSource.add(classModel);
            }

            if (respModelList != null) {
                //最后一次，如果记录到了。就要搞进去
                ClassModel classModel = new ClassModel(className, respModelList);
                respDataSource.add(classModel);
            }
            System.out.println("method count " + methodCount);

//            everything = sb.toString();
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return everything;
    }

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static String getCurrentTime() {
        long time = System.currentTimeMillis();
        String date = sdf.format(new Date(time));
        return date;
    }

    /**
     * 生成req类
     *
     * @param _className
     * @param _list
     */
    public static void writeReqModel(String _className, ArrayList<ClassParamsModel> _list) {

        String classComments = _className;

        boolean isArrayResp = false;

        if (listWithArray.contains(_className)) {
            isArrayResp = true;
        }

        _className = getClassName(_className);

        String packageAndImport = "package " + RESULT_PACKAGE + ";\n\n" +
                "import com.goumin.lib.net.AbsGMRequest;\n" +
                "import org.json.JSONArray;\n" +
                "import org.json.JSONException;\n" +
                "import java.util.ArrayList;\n" +
                "import com.gm.common.utils.CollectionUtil;\n" +
                "import org.json.JSONObject;\n\n";

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
            } else if (type.equals("decimal")) {
                type = "float";
            } else if (type.equals("array")) {

                //数组格式的


                jsonStr += "            JSONArray " + classModel.name + "JsonArray=new JSONArray();\n" +
                        "            if(CollectionUtil.isListMoreOne(" + classModel.name + ")){\n" +
                        "                for(String tag:" + classModel.name + "){\n" +
                        "                    " + classModel.name + "JsonArray.put(tag);\n" +
                        "                }\n" +
                        "            }\n";


                type = "ArrayList<String>"; //列表的暂时还没办法抓取,统一为String
            } else if (type.equals("object")) {
                type = "Object";
            }

            reqParams += "    public " + type + " " + classModel.name + ";\n";


            jsonStr += "            jsonObject.put(\"" + classModel.name + "\", " + classModel.name + "); //" + classModel.instruction + "\n";


        }

        String allJsonStr;
        if ("".equals(jsonStr)) {
            //json 字段是空的
            allJsonStr = "    @Override\n" +
                    "    public JSONObject getJsonObject() {\n" +
                    "        return null;\n" +
                    "    }";
        } else {
            allJsonStr = "    @Override\n" +
                    "    public JSONObject getJsonObject() {\n" +
                    "        JSONObject jsonObject = new JSONObject();\n" +
                    "        try {\n" +
                    jsonStr + "\n" +
                    "        } catch (JSONException e) {\n" +
                    "            e.printStackTrace();\n" +
                    "        }\n" +
                    "\n" +
                    "        return jsonObject;\n" +
                    "    }";
        }
        String action = getReqAction(classComments);

        if (classComments.contains("{id}")) {
            action += "/\"+id";
        } else {
            action += "\"";
        }

        //返回的resp类。要判断是不是array
        String respClass = _className + "Resp";
        if (isArrayResp) {
            respClass += "[]";
        }

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
                "}";

//        String finalResultContent = packageAndImport + className + keys + "\n\n" + reqParams + "\n\n" + endStr;

        String finalResultContent = packageAndImport + className + "\n" + reqParams + "\n\n" + endStr;

        saveStr2File(finalResultContent, reqClassName + ".java");


    }


    /**
     * 生成resp类
     *
     * @param _className
     * @param _list
     */
    public static void writeRespModel(String _className, ArrayList<ClassParamsModel> _list) {

        String classComments = _className;

        _className = getClassName(_className);

        String packageAndImport = "package " + RESULT_PACKAGE + ";\n\n" +
                "import java.io.Serializable;\n" +
                "import java.util.ArrayList;\n\n";

        String respClassName = _className + "Resp";

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
            String finalClassName = classModel.name;
            if (type.equals("string")) {
                type = "String";
                finalClassName += " = \"\"";
            } else if (type.equals("decimal")) {
                type = "float";
            } else if (type.equals("array")) {
                type = "ArrayList<String>"; //列表的暂时还没办法抓取,统一为String
                finalClassName += " = new ArrayList<>()";
            } else if (type.equals("text")) {
                type = "ArrayList<String>";
                finalClassName += " = new ArrayList<>()";
            } else if (type.equals("object")) {
                type = "Object";
            }

            respParams += "    public " + type + " " + finalClassName + ";  // " + classModel.instruction + "\n";
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
            File fileSave=new File(path);
            if(!fileSave.exists()){
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
//        h3. [201]用户注册接口( /signup)
        String className = "";
        className = line.substring(line.indexOf("/") + 1, line.length());
        if (className.endsWith(")")) {
            className = className.substring(0, className.length() - 1);
        }

        //去掉id
        if (className.endsWith("{id}")) {
            className = className.substring(0, className.length() - 5);
        }

        int length = className.indexOf("-");

        if (length != -1) {
            String[] arr = className.split("-");
            className = "";
            for (String str : arr) {
                className += str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toUpperCase());
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
        String className = "";
        className = line.substring(line.indexOf("/") + 1, line.length());
        if (className.endsWith(")")) {
            className = className.substring(0, className.length() - 1);
        }

        //去掉id
        if (className.endsWith("{id}")) {
            className = className.substring(0, className.length() - 5);
        }
        return className;
    }

    /**
     * @param line
     * @return 取得参数的类
     */
    public static ClassParamsModel getClassModel(String line) {

        if (line == null) {
            return null;
        }
//        System.out.println("class model line " + line);
        String[] arr = line.split("\\|");
        if (arr.length < 3) {
            //至少两个字段，name  和 type.加上第一个空格的，所以是3个
            return null;
        }
        //过滤掉参数名称
        if (arr[1].trim().equals("_.参数名称")) {
            return null;
        }
        ClassParamsModel classModel = new ClassParamsModel();
        classModel.name = arr[1].trim();
        classModel.type = arr[2].trim();
        //下面两个字段不是一直都有
        if (arr.length > 3) {
            classModel.instruction = arr[3].trim();
        }
        if (arr.length > 4) {
            classModel.comments = arr[4].trim();
        }
        return classModel;
    }

}

