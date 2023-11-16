package org.acg.zwyw;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import org.acg.Paper;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

public class ZwywPageProcessor implements PageProcessor {
    private final Site site = Site.me().setDomain("http://zw.china.com.cn/");

    @Override
    public void process(Page page) {
        FileWriter fileWriter = new FileWriter("D:\\temp\\zwyw.txt");

        List<Selectable> liNodes = page.getHtml().$("div.wrap").$("div.lp").$("ul").$("li").nodes();
        liNodes.forEach(item -> {
            Paper paper = new Paper();

            String title = item.$("a", "text").toString();
            paper.setTitle(title);

            String date = item.$("span", "text").toString();
            paper.setDate(date);

            String url = item.$("a", "href").toString();
            String link = "/zwyw/" + date.substring(0, date.indexOf("-")) + "/" + url.substring(url.lastIndexOf("/") + 9, url.lastIndexOf(".")) + ".html";
            paper.setLink(link);

            fileWriter.append(JSONUtil.toJsonStr(paper) + ",");

            ThreadUtil.execAsync(() -> {
                ZwywPaperProcessor.buildPaper(url);
            });
        });
    }

    @Override
    public Site getSite() {
        return PageProcessor.super.getSite();
    }
}
