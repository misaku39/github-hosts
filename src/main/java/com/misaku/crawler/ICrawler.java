package com.misaku.crawler;

import com.misaku.config.ConfigConstants;
import com.misaku.config.Resources;
import com.misaku.domain.TargetURL;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 所有具体的爬虫都要实现该接口
 * @author misaku
 * @since 2021/2/22 14:55
 */
public interface ICrawler {


    /**
     * 根据网站对象 进行ping取IP 并封入targetURL.results中
     * @param targetURL 网站URLs对象
     */
    void pingTargetURL(TargetURL targetURL) throws IOException;


    /**
     * 将TargetURL中爬取的Result输入到hosts文件中
     * @param targetURL 爬取后的网站URLs对象
     */
    void exportHostsToFile(TargetURL targetURL);


    void getTargetHostsList(List<TargetURL> targetURLList);

    String getIPToolName();


    //void getTargetHostsByTools(TargetURL targetURL, );


    /**
     * 指定一个工具抓取 一整个网站
     * @param targetURL 要抓取的网站
     * @param className 一定是该接口的实现类
     */
    public static void getTargetHostsByClassName(TargetURL targetURL, String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException {

        Class<?> clazz = Class.forName(className);
        if (ICrawler.class.isAssignableFrom(clazz) && (!clazz.isInterface())) {
            ICrawler crawler = (ICrawler) clazz.newInstance();


            System.out.println("------------------------------------------------------------");
            System.out.println("现在使用    "+ crawler.getIPToolName() +"   工具开始抓取");
            crawler.pingTargetURL(targetURL);
            System.out.println("------------------------------------------------------------");

        }
    }

    public static void getTargetHostsByClassNames(List<TargetURL> targetURLList, List<String> classNames) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        //外循环网站  内循环工具  一次外循环一个网站完成
        for (TargetURL targetURL : targetURLList) {
            System.out.println("============================================================");
            System.out.println("现在开始对    "+ targetURL.getConfigFileName() +"   文件开始抓取");
            for (String className : classNames) {
                getTargetHostsByClassName(targetURL, className);
            }
            System.out.println("============================================================");
        }


    }




    static class ProcessHostsFile{
        /**
         * 就是是不是以前使用该项目修改过配置文件
         * 判断原始的配置文件是否含有
         *              #${name}_begin
         *              #${name}_end
         *              默认是成对出现 否则直接在末尾追加
         *
         */
        private static boolean hasUsedBefore(String configName, LinkedList<String> sourceHosts) throws FileNotFoundException {
            boolean begin = false, end = false;

            // 必须是开始在前 结束在后
            for (String sourceHost : sourceHosts) {
                begin = sourceHost.startsWith("#" +configName + "_begin");
                if(begin) {
                    int beginIndex = sourceHost.indexOf(sourceHost);
                    for (int i = beginIndex + 1; i < sourceHosts.size(); i++) {
                        end = sourceHost.startsWith("#" +configName + "_end");
                        if (end) {
                            break;
                        }
                    }
                    break;
                }
            }
            return begin && end;
        }


        private static boolean hasUsedBefore(TargetURL targetURL, LinkedList<String> sourceHosts) throws FileNotFoundException {
            return hasUsedBefore(targetURL.getConfigFileName(), sourceHosts);
        }


        /**
         * 根据以前是否使用过该项目处理hosts来 删减原hosts中旧的URL
         *      使用过: 删除begin-end中间的部分
         *      没有使用过: 删除包括的url
         * @param sourceHosts 原hosts的集合
         * @param hasUsedBefore 以前是否使用该项目处理过hosts文件
         */
        private static void processHostsList(TargetURL targetUrl, LinkedList<String> sourceHosts, boolean hasUsedBefore) {
            String configFileName = targetUrl.getConfigFileName();

            StringBuilder stringBuilder = new StringBuilder();

            // 之前用过
            if (hasUsedBefore) {
                int beginIndex = sourceHosts.indexOf("#" + configFileName + "_begin");
                int endIndex = sourceHosts.indexOf("#" + configFileName + "_end");

                List<String> failUrlList = targetUrl.getFailUrlList();

                // 从尾遍历删除 这里考虑到ping失败url不用删除 所以直接传递TargetURL 而不是configFileName
                //              并且TargetURL要加一个失败列表属性
                for (int i = endIndex - 1; i > beginIndex; i--) {
                    String url = sourceHosts.get(i).trim();
                    // 如果失败队列中有该行 那么还是使用上次的 从失败队列中移除加入到成功队列并切割字符串 ip加入成功队列
                    // 这里我们不能url用contains(failUrl) 因为 主域名和子域名的关系 比如 a.misaku.com 和 misaku.com
                    // 我们这里直接切割 前15位是IP+补充的空格
                    if (!url.startsWith("#")) {
                        // 截取URL和IP是因为之前使用过 这是我们约定的 15ip-4空格-url
                        String oldUrl = (url.indexOf("#") == -1) ? url.substring(15).trim():url.substring(15, url.indexOf("#")).trim();
                        String oldIP = url.substring(0, 15+1).trim();

                        if (failUrlList.contains(oldUrl)) {
                            targetUrl.addFailOneToResult(oldUrl, oldIP);
                        }
                    }
                    sourceHosts.remove(i);
                }

                // 因为之前用过 所以我们直接将该网站新的网址插入到begin-end符号之间
                processNewHost(targetUrl, stringBuilder);
                sourceHosts.add(sourceHosts.indexOf("#" + configFileName + "_begin") + 1, stringBuilder.toString());
            }else {
                stringBuilder.append(ConfigConstants.LINESEPARATOR).append("#" + configFileName + "_begin");
                processNewHost(targetUrl, stringBuilder);
                stringBuilder.append(ConfigConstants.LINESEPARATOR).append("#" + configFileName + "_end");
                sourceHosts.addLast(stringBuilder.toString());
            }





        }

        /**
         * 将TargetUrl中的result进行组合
         */
        private static void processNewHost(TargetURL targetUrl, StringBuilder stringBuilder) {

            Map<String, List<String>> results = targetUrl.getResults();
            Set<String> keySet = results.keySet();



            stringBuilder.append(ConfigConstants.LINESEPARATOR).append("#" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            for (String url : keySet) {

                List<String> ipList = results.get(url);

                for (String ip : ipList) {
                    stringBuilder.append(ConfigConstants.LINESEPARATOR).append(ip);
                    if (ip.length() < 15) {
                        for (int i = 0; i < 15 - ip.length(); i++) {
                            stringBuilder.append(ConfigConstants.WHITESPACE);
                        }
                    }
                    stringBuilder.append(ConfigConstants.TABLE).append(url);
                }

                stringBuilder.append(ConfigConstants.LINESEPARATOR);
            }


            //return stringBuilder;
        }


        /**
         * 将配置写出到文件中
         */
        public static void writeHostsToFile(List<TargetURL> targetURLList) throws IOException {
            LinkedList<String> sourceHosts = Resources.getSourceHosts();
            for (TargetURL targetURL : targetURLList) {
                processHostsList(targetURL, sourceHosts, hasUsedBefore(targetURL, sourceHosts));
            }

            for (String sourceHost : sourceHosts) {
                System.out.print(sourceHost);
                System.out.println();
            }

            String generateToPath = Resources.hostsConfig.getGenerateToPath();
            if (".".equals(generateToPath) || "".equals(generateToPath.trim())) {
                generateToPath = "hosts";
            }else{
                if (generateToPath.endsWith("/") || generateToPath.endsWith("\\")) {
                    generateToPath += "hosts";
                }
                generateToPath += "\\hosts";
            }
            File hostsFile = new File(generateToPath);
            FileWriter writer = new FileWriter(hostsFile, true);
            PrintWriter printWriter = new PrintWriter(writer, true);

            for (String sourceHost : sourceHosts) {
                printWriter.println(sourceHost);
            }
            printWriter.close();
            writer.close();
        }
    }

}
