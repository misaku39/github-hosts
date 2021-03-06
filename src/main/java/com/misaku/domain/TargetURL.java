package com.misaku.domain;

import java.io.Serializable;
import java.util.*;

/**
 * 一个文件网站配置文件映射一个实体类
 * @author misaku
 * @since 2021/2/22 20:23
 */
public class TargetURL  implements Serializable {

    private static final long serialVersionUID = 6870137603894712283L;

    /**
     * 对应的配置文件名 (不包含后缀)
     */
    private String configFileName;

    /**
     * 文件中所有要ping取的url集合
     */
    private List<String> urls;

    /**
     * ping后的  url->IP结果集
     */
    private Map<String, List<String>> results = new LinkedHashMap<>();

    /**
     * ping失败的URl集合
     */
    private List<String> failUrlList = new ArrayList<>();


    /**
     * 将url对应的ipList放入result中 这个是爬取时使用的方法
     */
    public void addToResult(String url, List<String> ipList) {
        // 从失败列表中去除
        if (failUrlList.contains(url) && ipList.size() > 0) {
            failUrlList.remove(url);
        }

        // 使用第二个工具存入IP时 因为第一个已经存入了 那么就是ipList需要去重并集 要整合的list大于0才有意义
        // 包含此key时说明已经存入了 我们设置的不会为null的 是长度为0的list
        if ( (results.containsKey(url)&& results.get(url).size() > 0 ) && ipList.size() > 0 ) {
            // ip去重
            List<String> oldList = results.get(url);
            // 先去重 这里去原来的和新的都可以
            ipList.removeAll(oldList);
            // 再合并
            oldList.addAll(ipList);

            return;
        }

        results.put(url, ipList);
    }


    /**
     * 这个是供处理失败列表用的 这时先不从失败列表中移除
     */
    public void addFailOneToResult(String url, String ip) {

        if (results.containsKey(url)) {
            List<String> oldList = results.get(url);
            if (!oldList.contains(ip)) {
                oldList.add(ip);
            }

            return;
        }

        ArrayList<String> ipList = new ArrayList<>();
        ipList.add(ip);
        results.put(url, ipList);
    }

    public void addToFaiUrllList(List<String> failUrlList) {
        // 符合添加操作的要求
        // 1.成功列表存在的不添加进去(也就是results中的list长度不为0的)
        // 2.失败列表存在的不添加 避免重复添加
        // 移除的要求
        // 1.现在成功的要移除   这个操作在上面添加中
        for (String failUrl : failUrlList) {
            if ((!this.failUrlList.contains(failUrl)) && (results.get(failUrl).size() != 0)) {
                this.failUrlList.add(failUrl);
            }
        }

    }





    public TargetURL() {
    }

    public TargetURL(String configFileName, List<String> urls) {
        this.configFileName = configFileName;
        this.urls = urls;
    }

    @Override
    public String toString() {
        return "TargetURL{" +
                "configFileName='" + configFileName + '\'' +
                ", urls=" + urls +
                '}';
    }

    public String getConfigFileName() {
        return configFileName;
    }

    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public Map<String, List<String>> getResults() {
        return results;
    }

    public void setResults(Map<String, List<String>> results) {
        this.results = results;
    }

    public List<String> getFailUrlList() {
        return failUrlList;
    }

    public void setFailUrlList(List<String> failUrlList) {
        this.failUrlList = failUrlList;
    }

    public void addToFailUrlList(String url) {
        failUrlList.add(url);
    }

    public void removeFormFailUrlList(String url) {
        failUrlList.remove(url);
    }

    public boolean isFailUrl(String url) {
        return failUrlList.contains(url);
    }

}
