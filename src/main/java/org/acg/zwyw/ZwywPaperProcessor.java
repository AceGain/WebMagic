package org.acg.zwyw;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

public class ZwywPaperProcessor implements PageProcessor {

    private final Site site = Site.me().setDomain("http://zw.china.com.cn/");

    @Override
    public void process(Page page) {
        String url = page.getUrl().get();
        String id = url.substring(url.lastIndexOf("/") + 9, url.lastIndexOf("."));

        String title = page.getHtml().$("div.content").$("h1", "text").get();
        String date = page.getHtml().$("div.inform").$("p", "text").nodes().get(0).get();
        String from = page.getHtml().$("div.inform").$("p", "text").nodes().get(1).get();
        List<Selectable> content = page.getHtml().$("div.article").$("p").nodes();
        String editor = page.getHtml().$("div.article").$("div.editor", "text").get();

        List<String> fileLines = CollectionUtil.newArrayList();
        fileLines.add("---");
        fileLines.add("title: " + title);
        fileLines.add("layout: doc");
        fileLines.add("navbar: true");
        fileLines.add("sidebar: false");
        fileLines.add("aside: true");
        fileLines.add("footer: true");
        fileLines.add("prev: false");
        fileLines.add("next: false");
        fileLines.add("---");

        fileLines.add("<div class=\"paper_container\">");
        fileLines.add("<div class=\"paper_title\">");
        fileLines.add(title);
        fileLines.add("</div>");
        fileLines.add("<div class=\"paper_title_sub\">");
        fileLines.add(date);
        fileLines.add("<el-divider direction=\"vertical\" />");
        fileLines.add(from);
        fileLines.add("</div>");
        fileLines.add("<div class=\"paper_content\">");
        content.forEach(item -> {
            String temp = item.$("p", "text").get();
            if (StrUtil.isEmpty(temp)) {
                return;
            }
            if (StrUtil.contains(temp, "<style")) {
                return;
            }
            fileLines.add("<p>");
            fileLines.add(temp);
            fileLines.add("</p>");
        });
        fileLines.add("</div>");
        fileLines.add("<div  class=\"paper_editor\">");
        fileLines.add(editor);
        fileLines.add("</div>");
        fileLines.add("</div>");

        String baseSrc = "D:\\temp\\zwyw\\";
        String subSrc = url.replace("http://zw.china.com.cn/", "").substring(0, 4);
        String fileName = id + ".md";
        String filePath = baseSrc + subSrc + "\\" + fileName;
        FileWriter fileWriter = new FileWriter(filePath);
        fileWriter.appendLines(fileLines);
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void buildPaper(String url) {
        Spider.create(new ZwywPaperProcessor()).addUrl(url)
                .addPipeline(new ConsolePipeline()).run();
    }

//    public static void main(String[] args) {
//        Spider.create(new ZwywPaperProcessor()).addUrl("http://zw.china.com.cn/2023-10/16/content_116748369.shtml")
//                .addPipeline(new ConsolePipeline()).run();
//    }
}
