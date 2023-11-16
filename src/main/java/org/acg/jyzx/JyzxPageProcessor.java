package org.acg.jyzx;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import org.acg.Paper;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

public class JyzxPageProcessor implements PageProcessor {
    private final Site site = Site.me().setDomain("www.chsi.com.cn");

    @Override
    public void process(Page page) {
        FileWriter fileWriter = new FileWriter("D:\\temp\\jyzx.txt");

        List<Selectable> liNodes = page.getHtml().$("ul.news-list").$("li").nodes();
        liNodes.forEach(item -> {
            Paper paper = new Paper();

            String title = item.$("div.news-title").$("a", "text").toString();
            if (StrUtil.containsAny(title, "大学", "学信")) {
                return;
            }
            paper.setTitle(title);

            String date = item.$("div.news-time", "text").toString();
            if (Convert.toInt(date.substring(0, date.indexOf("-"))) < 2017) {
                return;
            }
            paper.setDate(date);

            String url = item.$("div.news-title").$("a", "href").toString();
            String link = "/jyzx/" + date.substring(0, date.indexOf("-")) + "/" + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".")) + ".html";
            paper.setLink(link);

            fileWriter.append(JSONUtil.toJsonStr(paper) + ",");

            ThreadUtil.execAsync(() -> {
                JyzxPaperProcessor.buildPaper("https://" + site.getDomain() + url);
            });
        });
    }

    @Override
    public Site getSite() {
        return site;
    }
}
