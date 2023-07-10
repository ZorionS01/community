package com.nowcoder.community;

import java.io.IOException;

/**
 * @Author Szw 2001
 * @Date 2023/7/9 9:33
 * @Slogn 致未来的你！
 */
public class WkTests {

    public static void main(String[] args) throws IOException, InterruptedException {
        String cmd = "D:/wkhtmltopdf/bin/wkhtmltoimage https://www.nowcoder.com d:/work/data/wk-images/1.png";
        Runtime.getRuntime().exec(cmd);
        Thread.sleep(5000);
    }
}
