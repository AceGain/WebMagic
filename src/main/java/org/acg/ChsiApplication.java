package org.acg;

import org.acg.jysp.JyspPageProcessor;
import org.acg.jyzx.JyzxPageProcessor;
import org.acg.zwyw.ZwywPageProcessor;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

public class ChsiApplication {
    public static void main(String[] args) {
        for (int i = 0; i < 25; i++) {
            int start = i * 60;
            Spider.create(new JyzxPageProcessor()).addUrl("https://www.chsi.com.cn/jyzx/?start=" + start).thread(60)
                    .addPipeline(new ConsolePipeline()).run();
        }

        for (int i = 0; i < 2; i++) {
            int start = i * 60;
            Spider.create(new JyspPageProcessor()).addUrl("https://www.chsi.com.cn/jyzx/jysp/?start=" + start).thread(60)
                    .addPipeline(new ConsolePipeline()).run();
        }

        for (int i = 1; i < 16; i++) {
            if (i > 1) {
                Spider.create(new ZwywPageProcessor()).addUrl("http://zw.china.com.cn/node_8012270_" + i + ".html").thread(30)
                        .addPipeline(new ConsolePipeline()).run();
            } else {
                Spider.create(new ZwywPageProcessor()).addUrl("http://zw.china.com.cn/node_8012270.html").thread(30)
                        .addPipeline(new ConsolePipeline()).run();
            }
        }
    }
}
