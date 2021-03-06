package com.misaku;

import com.misaku.config.ConfigConstants;
import com.misaku.config.Resources;
import com.misaku.crawler.ICrawler;
import com.misaku.domain.HostsConfig;
import com.misaku.domain.TargetURL;
import com.misaku.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

/**
 * @author misaku
 * @since 2021/2/22 9:44
 */
public class MainApp {


    /**
     * 这里我们选择生成一个新的文件 我们自己复制黏贴到hosts文件中
     */
    public static void main(String[] args) throws Exception {
        resourcesTest();
        openHostsDir();
        //getJarPathTest();

    }

    /**
     * 打开系统hosts所在的目录
     */
    public static void openHostsDir() throws IOException, InterruptedException {
        String localHostsDir = Resources.hostsConfig.getLocalHostsDir();
        if (localHostsDir == null || "".equals(localHostsDir)) {
            localHostsDir = ConfigConstants.DEFAULT_LOCAL_HOSTS_DIR;
        }

        Runtime runtime = Runtime.getRuntime();
        runtime.exec("explorer " + localHostsDir);
    }



    // Main-Class: com.misaku.MainApp
    //  java -jar E:\新建文件夹\github-hosts-1.0-SNAPSHOT.jar
    //  java -jar github-hosts-1.0-SNAPSHOT.jar
    public static void getJarPathTest() throws UnsupportedEncodingException {

        //获取jar包所在的位置
        //System.out.println(getJarPath01());
        getJarPath01();
        System.out.println("====================");
        getJarPath02();


        System.out.println("====================");
        getJarPath03();
        System.out.println("====================");
        //System.out.println(getJarPath01());
        //System.out.println(getJarPath02());

        //StringUtils.trim();

    }



    // 这两个获取的路径最后都不是以 / 结尾的


    // 这种方式是使用 传的是 jar所在的绝对路径的  而且不能不打包进行测试 必须打包后才可以进行测试
    // 经过改进可以不用传绝对路径了 但还是要打包测试
    // 但这种方法 就算有中文路径也不会乱码
    public static String getJarPath02() {
        /**
         * 方法一：获取当前可执行jar包所在目录
         */
        String filePath = System.getProperty("java.class.path");
        System.out.println("System.getProperty(\"java.class.path\")     " + filePath);
        //===========================必须加上获取绝对路径

        System.out.println("getAbsolutePath     " + new File(filePath).getAbsolutePath());
        filePath = new File(filePath).getAbsolutePath();


        //=============================
        String pathSplit = System.getProperty("path.separator");//得到当前操作系统的分隔符，windows下是";",linux下是":"

        /**
         * 若没有其他依赖，则filePath的结果应当是该可运行jar包的绝对路径，
         * 此时我们只需要经过字符串解析，便可得到jar所在目录
         */
        if(filePath.contains(pathSplit)){
            filePath = filePath.substring(0,filePath.indexOf(pathSplit));
            System.out.println("contains(pathSplit)" + filePath);
        }

        if (filePath.endsWith(".jar")) {//截取路径中的jar包名,可执行jar包运行的结果里包含".jar"
            filePath = filePath.substring(0, filePath.lastIndexOf(File.separator));
            System.out.println("filePath.endsWith(\".jar\")" + filePath);
        }
        System.out.println("jar包所在目录："+filePath);

        return filePath;
    }


    // 这种方式 有中文路径会乱码 所以要转UTF-8
    public static String getJarPath01() throws UnsupportedEncodingException {

        String path = MainApp.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = URLDecoder.decode(path, "utf-8");// 转化为utf-8编码，支持中文
        System.out.println("path    " + path);
        //System.out.println(MainApp.class.getProtectionDomain().getCodeSource());
        //URL url = MainApp.class.getProtectionDomain().getCodeSource().getLocation();
        //System.out.println(url.toString());


        System.out.println("System.getProperty(\"os.name\") " + System.getProperty("os.name"));
        if (System.getProperty("os.name").contains("dows")) {
            path = path.substring(1, path.length());
        }
        System.out.println(path);

        System.out.println("path.contains(\"jar\")  "  + path.contains("jar") );
        if (path.contains("jar")) {
            path = path.substring(0, path.lastIndexOf("."));
            System.out.println(path);
            path = path.substring(0, path.lastIndexOf("/"));
            System.out.println(path);
            return path;
        }
        return path.replace("target/classes/", "");
    }

    // new File("").getAbsolutePath()在jar包状态下直接获取当前jar所在的目录
    // 获取的是不以 /结尾的路径
    public static void getJarPath03() {
        File file = new File("");
        System.out.println(file.getAbsolutePath());

        File file1 = new File(".");
        System.out.println(file1.getAbsolutePath());

    }

    public static void resourcesTest() throws IOException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException, InstantiationException {
        // 1.加载主配置文件
        //Resources.loadHostsConfig();
        HostsConfig hostsConfig = Resources.hostsConfig;
        System.out.println(hostsConfig);

        // 2.选择加载要ping取的网站配置文件 也就是网站配置文件
        List<TargetURL> targetURLS = Resources.selectConfigsLoad();
        System.out.println(targetURLS);

        // 3.选择要使用的工具
        List<String> classNames = Resources.selectIPTools();
        System.out.println(classNames);

        // 4.进行爬取 结果存放在TargetURL中的results中
        ICrawler.getTargetHostsByClassNames(targetURLS, classNames);

        // 5.进行整理写入到新的文件中
        ICrawler.ProcessHostsFile.writeHostsToFile(targetURLS);

    }



}
