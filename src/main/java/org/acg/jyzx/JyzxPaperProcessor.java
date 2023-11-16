package org.acg.jyzx;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

public class JyzxPaperProcessor implements PageProcessor {

    private final Site site = Site.me().setDomain("www.chsi.com.cn");

    @Override
    public void process(Page page) {
        String url = page.getUrl().get();
        String id = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));

        String title = page.getHtml().$("div.title-box").$("h2", "text").get();
        String date = page.getHtml().$("div.title-box").$("span.news-time", "text").get();
        String from = page.getHtml().$("div.title-box").$("span.news-from", "text").get();

        List<String> imgList = page.getHtml().$("div.content-l").$("img", "src").all();

        String content = page.getHtml().$("div.content-l").toString()
                .replace("&nbsp; \n" + " <div id=\"dz\"></div>", "")
                .replace("<script src=\"//t1.chei.com.cn/common/js/dianzan.js\"></script>", "")
                .replace("<script>", "")
                .replace("showDianZan(\"dz\", " + id + ", \"#2EAFBB\");", "")
                .replace("</script>", "")
                .replace("content-l", "paper_content");

        for (String img : imgList) {
            if (StrUtil.contains(content, img)) {
                String imgNew = "https://www.chsi.com.cn" + img;
                content = content.replace(img, imgNew);
            }
        }


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
        fileLines.add(content);
        fileLines.add("</div>");

        String baseSrc = "D:\\temp\\jyzx\\";
        String subSrc = url.replace("https://www.chsi.com.cn/jyzx/", "").substring(0, 4);
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
        Spider.create(new JyzxPaperProcessor()).addUrl(url)
                .addPipeline(new ConsolePipeline()).run();
    }

//    public static void main(String[] args) {
//        Spider.create(new JyzxPaperProcessor()).addUrl("https://www.chsi.com.cn/jyzx/201701/20170118/1579778417.html")
//                .addPipeline(new ConsolePipeline()).run();
//    }
}
