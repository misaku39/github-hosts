package com.misaku.crawler;

import com.misaku.domain.TargetURL;
import com.misaku.util.HttpClientUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 直接访问: https://www.ipaddress.com/search/ 的话:
 *  该网站并不是需要使用cookie进行访问 而是要带上浏览器头进行访问  返回的是301重定向 要获取响应头Location中的目的地址
 *  这里我们使用的是Jsoup中的Http 可以直接处理重定向
 * @author misaku
 * @since 2021/2/22 14:58
 */
public class IPAddressCrawler implements ICrawler {

    /**
     * 使用的搜索网址
     */
    public static final String IP = "https://www.ipaddress.com/search/";


    private static final String PARAM_NAME_HOST = "host";

    private  Map<String, String> params = new HashMap<>();


    @Override
    public void pingTargetURL(TargetURL targetURL) throws IOException {

        List<String> urls = targetURL.getUrls();
        List<String> failList = new ArrayList<>();

        for (String url : urls) {
            List<String> ipList = getIPListByURL(url);
            // ping取失败的url
            if (ipList.size() == 0) {
                failList.add(url);
            }

            targetURL.addToResult(url, ipList);

        }

        // 进行说明
        System.out.printf("使用: %s 工具,ping取%d个网址. 成功%d个 失败%d个 %s",
                            IP, urls.size(), urls.size()-failList.size(), failList.size(), failList.size() == 0?"":"失败的URL如下:");
        System.out.println();
        for (String url : failList) {
            System.out.println(url);
        }
        // 对失败的URl尽心处理 1.直接放入结果集 2.再次ping  这里先使用1
        // 先不和上面迭代整合 因为后续可能会添加代码
        //for (String url : failList) {
            //
        //}

        // 请求发送失败或请求的URL没有找到 将url加入失败队列 之前成功的url不能加入失败队列
        // 这里去重复是因为多个不同的工具
        // 满足 长度为0 之前的失败列表没有 并且 成功列表也就是结果集的list长度不为0
        //      失败列表没有: 防止重复添加
        targetURL.addToFaiUrllList(failList);


    }

    @Override
    public void exportHostsToFile(TargetURL targetURL) {
        System.out.println("============================================================");
    }


    /**
     * 这个才是具体的发送网络请求的爬虫代码
     * @param url 要ping的网址
     * @return 没有ping取到 不会返回null的 只会返回长度为0的list
     */
    private List<String> getIPListByURL(String url) {
        List<String> ipList = new ArrayList<>();

        try {
            // 这里我们并不对返回的状态码进行判断了 请求失败不管是否异常都只会返回一个空的list
            Connection connect = Jsoup.connect(IP);
            HashMap<String, String> data = new HashMap<>();
            data.put(PARAM_NAME_HOST, url);
            connect.data(data);
            Document document = connect.post();

            // 对结果进行提取
            // a[href^="https://www.ipaddress.com/ipv4/"]
            Elements select = document.select("a[href^='https://www.ipaddress.com/ipv4/']");

            for (Element element : select) {
                if (!ipList.contains(element.text())) ipList.add(element.text());
            }
        }catch (Exception e) {
            System.out.printf("对  %s  请求异常", url);
            System.out.println();
        }


        return ipList;
    }




    @Override
    public void getTargetHostsList(List<TargetURL> targetURLList) {

    }

    @Override
    public String getIPToolName() {
        return IP;
    }

}
