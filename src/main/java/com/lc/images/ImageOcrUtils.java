package com.lc.images;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author cheng.liu
 * @version 1.0
 * @description: TODO
 * @date 2023/1/31 11:43
 */
public class ImageOcrUtils {
    // 语言包从 https://github.com/tesseract-ocr/tessdata 下载放到 项目的tessdata 目录下
    // 语言包说明 https://tesseract-ocr.github.io/tessdoc/Data-Files.html
    private static ITesseract tesseract;

    static {
        tesseract = new Tesseract();
        tesseract.setDatapath(getTessData());
    }

    private static String getTessData() {
        String path = String.join(File.separator, System.getProperty("user.dir"), "tessdata");
        File file = new File(path);
        if (!file.exists()) file.mkdirs();
        return file.getPath();
    }

    public static String ocr(String imagePath, String language) {
        File file = new File(imagePath);
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String text = "";
        try {
            tesseract.setLanguage(language);
            // http://www.sk-spell.sk.cx/tesseract-ocr-parameters-in-302-version
            // 将结果转为html输出
//          tesseract.setVariable("tessedit_create_hocr","1");
            // 要识别的字符白名单，不包含的字符不会识别-在中文下无效
//          tesseract.setVariable("tessedit_char_whitelist","0123456789-.");
            // 不识别字符黑名单
//          tesseract.setVariable("tessedit_char_blacklist","计算机系统");
            text = tesseract.doOCR(bufferedImage);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return text;
    }
}
