package com.misaku.config;

import com.misaku.domain.HostsConfig;
import com.misaku.domain.TargetURL;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 读取主配置文件以及网站配置文件 原Host文件
 * @author misaku
 * @since 2021/2/22 14:39
 */
public class Resources {

    /**
     * 主配置实体类
     */
    public static HostsConfig hostsConfig;

    static {
        try {
            loadHostsConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param name 主配置文件 需要带后缀
     */
    public static void loadHostsConfig(String name) throws IOException {
        if (name == null || "".equals(name.trim())) {
            name = ConfigConstants.MAIN_CONFIG_NAME;
        }

        Properties configProperties = loadProperties(name);

        String hostsPath = configProperties.getProperty(ConfigConstants.MAIN_HOSTS_PATH);
        String generateToPath = configProperties.getProperty(ConfigConstants.MAIN_GENERATE_PATH);
        List<String> toolsIPClassNames = getPropertiesValueList(configProperties, ConfigConstants.MAIN_TOOLS_IP_CLASS_NAME_PREFIX);
        List<String> configNames = getPropertiesValueList(configProperties, ConfigConstants.MAIN_CONFIG_NAME_PREFIX);
        String localHostsDir = configProperties.getProperty(ConfigConstants.LOCAL_HOSTS_DIR);

        hostsConfig = new HostsConfig(hostsPath, generateToPath, toolsIPClassNames, configNames, localHostsDir);
        System.out.println("加载主配置文件成功!");
    }

    public static void loadHostsConfig() throws IOException {
        loadHostsConfig(null);
    }


    /**
     * 选择加载需要的ping的hosts配置
     */
    public static List<TargetURL> selectConfigsLoad() throws IOException {
        System.out.println("============================================================");
        System.out.println("选择要加载的配置,输入序号 使用逗号进行分隔 如1,10,5");
        System.out.println("============================================================");


        List<String> configNames = hostsConfig.getConfigNames();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < configNames.size(); i++) {
            stringBuilder.append( i + " :   " + configNames.get(i) + " \n");
        }
        System.out.println(stringBuilder);

        System.out.print("选择要加载的配置的序号: ");

        Scanner scanner = new Scanner(System.in);
        String selectNos = scanner.next();
        System.out.println();


        String[] nameIndexes = selectNos.split(",");

        List<TargetURL> lists = new ArrayList<>();
        // 开始加载配置文件 加上后缀 suffix .properties
        for (String nameIndex : nameIndexes) {
            String name = configNames.get(Integer.parseInt(nameIndex.trim()));
            Properties properties = loadProperties(name + ConfigConstants.PROP_SUFFIX);
            List<String> urls = getPropertiesValueList(properties, name);
            lists.add(new TargetURL(name, urls));
        }

        hostsConfig.setTargetConfig(lists);

        return lists;
    }

    /**
     * 让用户选择查询工具
     */
    public static List<String> selectIPTools() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        List<String> toolsIPClassNames = hostsConfig.getToolsIPClassNames();
        System.out.println("============================================================");
        System.out.println("请选择要使用的网站工具: 建议一次输入一个");
        System.out.println("============================================================");

        for (int i = 0; i < toolsIPClassNames.size(); i++) {
            String toolsIPClassName = toolsIPClassNames.get(i);
            Class<?> clazz = Class.forName(toolsIPClassName);
            Field field = clazz.getDeclaredField(ConfigConstants.IP);
            field.setAccessible(true);
            String ip = (String) field.get(null);
            System.out.println(i + " :   " + ip);
        }

        System.out.print("选择要使用的网站工具的序号: ");

        Scanner scanner = new Scanner(System.in);
        String selectNos = scanner.next();
        System.out.println();


        String[] nameIndexes = selectNos.split(",");

        List<String> useIPToolClassNames = new ArrayList<>();

        // 将用户选择的工具 放入集合中
        for (String nameIndex : nameIndexes) {
            if (!useIPToolClassNames.contains(nameIndex)) {
                useIPToolClassNames.add(toolsIPClassNames.get(Integer.parseInt(nameIndex)));
            }
        }


        return useIPToolClassNames;

    }


    /**
     * 根据文件名 去classpath下去加载配置
     * @param name 文件名带后缀
     * @return Properties
     */
    private static Properties loadProperties(String name) throws IOException {
        Properties configProperties = new Properties();
        InputStream inputStream = Resources.class.getClassLoader().getResourceAsStream(name);
        configProperties.load(inputStream);
        return configProperties;
    }


    /**
     * 获取属性前缀+序号所有的对应的value集合
     * @param properties 配置文件Properties
     * @param keyPrefix 属性前缀
     * @return value集合
     */
    private static List<String> getPropertiesValueList(Properties properties ,String keyPrefix) throws IOException {
        ArrayList<String> valueList = new ArrayList<>();

        for (int i = 0; properties.getProperty(keyPrefix + i) != null; i++) {
            if (!valueList.contains(keyPrefix + i)) {
                valueList.add(properties.getProperty(keyPrefix + i));
            }
        }
        return valueList;
    }


    /**
     * 获取旧的hosts配置文件 读取为List<String>
     */
    public static LinkedList<String> getSourceHosts() throws IOException {
        LinkedList<String> collect = new LinkedList<>();

        // 这里先使用测试环境
        String hostsPath = hostsConfig.getHostsPath();
        if (hostsPath != null && !"".equals(hostsPath.trim())) {
            FileReader fileReader = new FileReader(hostsPath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            bufferedReader.lines().forEach(collect::add);
            bufferedReader.close();
            fileReader.close();
        }

        return collect;
    }

}
