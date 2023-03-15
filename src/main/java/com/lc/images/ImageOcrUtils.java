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
//            tesseract.setLanguage("eng");
//            tesseract.setHocr(true);
            text = tesseract.doOCR(bufferedImage);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return text;
    }
}
