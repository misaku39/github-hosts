package com.misaku.config;

/**
 * 配置文件中的  Key
 *
 * @author misaku
 * @since 2021/2/23 16:07
 */
public class ConfigConstants {

    /**
     * 主配置文件名称
     */
    public static final String MAIN_CONFIG_NAME = "hosts-config.properties";

    /**
     * 原hosts文件的路径   Key
     */
    public static String MAIN_HOSTS_PATH = "hosts.file.path";

    /**
     * 生成文件路径   Key
     */
    public static String MAIN_GENERATE_PATH = "generate.file.path";

    /**
     * 查询工具类    Key
     */
    public static String MAIN_TOOLS_IP_CLASS_NAME_PREFIX = "ping.tools.class";

    /**
     * 配置文件名前缀
     */
    public static String MAIN_CONFIG_NAME_PREFIX = "target.config";


    public static final String PROP_SUFFIX = ".properties";

    public static final String LOCAL_HOSTS_DIR = "local.hosts.dir";

    public static final String DEFAULT_LOCAL_HOSTS_DIR = "C:\\Windows\\System32\\drivers\\etc";



    //====================================================== 上面是配置文件中的常量 下面是类中的常量


    public static final String IP = "IP";


    // 一个空格
    public static final String WHITESPACE = " ";

    // 这里 t制表符四个空格
    public static final String TABLE = "    ";


    public static final String LINESEPARATOR = java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));


}
