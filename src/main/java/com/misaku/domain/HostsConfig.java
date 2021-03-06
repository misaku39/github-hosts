package com.misaku.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 主配置文件对应的实体类
 * @author misaku
 * @since 2021/2/22 14:51
 */
public class HostsConfig implements Serializable {

    private static final long serialVersionUID = -4059900931364117002L;


    /**
     * 原hosts文件的位置
     */
    private String hostsPath;


    /**
     * 将抓取的hosts数据生成的文件的位置
     */
    private String generateToPath;

    /**
     * 使用的工具 也就是IP查询网站
     */
    private List<String> toolsIPClassNames;

    /**
     * 当前使用的工具类名
     */
    private List<String> useIPToolClassNames;


    /**
     * 要ping的子配置文件的名称集合
     */
    private List<String> configNames;

    /**
     * 要ping的网站的合集
     */
    private List<TargetURL> targetConfig;


    /**
     * 电脑hosts目录
     */
    private String localHostsDir;





    public HostsConfig() {
    }

    public HostsConfig(String hostsPath, String generateToPath, List<String> toolsIPClassNames, List<String> configNames) {
        this.hostsPath = hostsPath;
        this.generateToPath = generateToPath;
        this.toolsIPClassNames = toolsIPClassNames;
        this.configNames = configNames;
    }



    public HostsConfig(String hostsPath, String generateToPath, List<String> toolsIPClassNames, List<String> configNames, String localHostsDir) {
        this.hostsPath = hostsPath;
        this.generateToPath = generateToPath;
        this.toolsIPClassNames = toolsIPClassNames;
        this.configNames = configNames;
        this.localHostsDir = localHostsDir;
    }


    public HostsConfig(String hostsPath, String generateToPath, List<String> useIPToolClassNames, List<String> useIPToolClassName, List<String> configNames) {
        this.hostsPath = hostsPath;
        this.generateToPath = generateToPath;
        this.toolsIPClassNames = useIPToolClassNames;
        this.useIPToolClassNames = useIPToolClassName;
        this.configNames = configNames;
    }




    public HostsConfig(String hostsPath, String generateToPath, List<String> toolsIPClassNames, List<String> useIPToolClassNames, List<String> configNames, List<TargetURL> targetConfig) {
        this.hostsPath = hostsPath;
        this.generateToPath = generateToPath;
        this.toolsIPClassNames = toolsIPClassNames;
        this.useIPToolClassNames = useIPToolClassNames;
        this.configNames = configNames;
        this.targetConfig = targetConfig;
    }

    public List<String> getToolsIPClassNames() {
        return toolsIPClassNames;
    }

    public void setToolsIPClassNames(List<String> toolsIPClassNames) {
        this.toolsIPClassNames = toolsIPClassNames;
    }

    public String getGenerateToPath() {
        return generateToPath;
    }

    public void setGenerateToPath(String generateToPath) {
        this.generateToPath = generateToPath;
    }

    public String getHostsPath() {
        return hostsPath;
    }

    public void setHostsPath(String hostsPath) {
        this.hostsPath = hostsPath;
    }

    public List<String> getConfigNames() {
        return configNames;
    }

    public void setConfigNames(List<String> configNames) {
        this.configNames = configNames;
    }

    public List<TargetURL> getTargetConfig() {
        return targetConfig;
    }

    public void setTargetConfig(List<TargetURL> targetConfig) {
        this.targetConfig = targetConfig;
    }

    public List<String> getUseIPToolClassNames() {
        return useIPToolClassNames;
    }

    public void setUseIPToolClassNames(List<String> useIPToolClassNames) {
        this.useIPToolClassNames = useIPToolClassNames;
    }

    public String getLocalHostsDir() {
        return localHostsDir;
    }

    public void setLocalHostsDir(String localHostsDir) {
        this.localHostsDir = localHostsDir;
    }

    @Override
    public String toString() {
        return "HostsConfig{" +
                "hostsPath='" + hostsPath + '\'' +
                "\n, generateToPath='" + generateToPath + '\'' +
                "\n, toolsIPClassNames=" + toolsIPClassNames +
                "\n, useIPToolClassNames=" + useIPToolClassNames +
                "\n, configNames=" + configNames +
                "\n, targetConfig=" + targetConfig +
                '}';
    }
}
