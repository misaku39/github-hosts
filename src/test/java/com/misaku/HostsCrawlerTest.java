package com.misaku;

import com.misaku.config.Resources;
import com.misaku.crawler.ICrawler;
import com.misaku.domain.HostsConfig;
import com.misaku.domain.TargetURL;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author misaku
 * @since 2021/3/5 21:16
 */
public class HostsCrawlerTest {


    @Test
    public void crawlerTest() {
    }

    @Test
    public void resourcesTest() throws IOException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException, InstantiationException {
        // 1.加载主配置文件
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
