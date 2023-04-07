package com.lc.images;

import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.MultiResolutionImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cheng.liu
 * @version 1.0
 * @description: TODO
 * @date 2023/1/31 11:41
 */
public class ServiceAppTestMain {

    public static void main(String[] args) {
        taskArrangement();
    }

    private static void taskArrangement() {
        try {
            System.out.println("停止3秒后截取第一张屏幕画面");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("开始截取第一张屏幕画面");
        String beforeScreen = screenshot();
        try {
            System.out.println("停止3秒后截取第二张屏幕画面");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String currentScreen = screenshot();
        String imagePath = contrastImage(beforeScreen, currentScreen);
//        String imagePath = contrastImage(String.join(File.separator, getBasePath(), "test1.png"),
//                String.join(File.separator, getBasePath(), "test2.png"));
        if (null == imagePath || imagePath.trim().isEmpty()) {
            return;
        }
        long start = System.currentTimeMillis();
        String ocr = ImageOcrUtils.ocr(imagePath, "eng");
//        String ocr = com.robot.control.tests.v2.ImageOcrUtils.ocr(String.join(File.separator, getBasePath(), "1675747922902_cutOut.png"), "chi_sim+eng");
        System.out.println("ocr 耗时:" + (System.currentTimeMillis() - start));
        System.out.println("ocr 内容:" + ocr);
    }

    /**
     * 获取压缩图像的标准
     * @param beforeScreen 图像1
     * @param currentScreen 图像2
     * @return 压缩的分辨率 [宽，高]
     */
    private static Integer[] getMinImage(String beforeScreen, String currentScreen) {
        Integer[] beforeScreenResolution = ImageContrastUtils.getResolution(beforeScreen);
        Integer[] currentScreenResolution = ImageContrastUtils.getResolution(currentScreen);
        int wBefore = beforeScreenResolution[0];
        int hBefore = beforeScreenResolution[1];
        int wCurrent = currentScreenResolution[0];
        int hCurrent = currentScreenResolution[1];
        int w = wBefore;
        if (wBefore - wCurrent > 0) {
            w = wCurrent;
        }
        int h = hBefore;
        if (hBefore - hCurrent > 0) {
            h = hCurrent;
        }
        return new Integer[]{w, h};
    }

    /**
     * 图像对比
     * @param beforeScreen 图像1
     * @param currentScreen 图像2
     * @return 差异部分的图像临时路径
     */
    private static String contrastImage(String beforeScreen, String currentScreen) {
//        Mat beforeScreenMat = ImageContrastUtils.grayImage(beforeScreen);
//        Map<String, Double> beforeScreenMap = ImageContrastUtils.getPixelValue(beforeScreenMat);
//        Mat currentScreenMat = ImageContrastUtils.grayImage(currentScreen);
//        Map<String, Double> currentScreenMap = ImageContrastUtils.getPixelValue(currentScreenMat);

//        HashMap<String, double[]> beforeScreenMap = ImageContrastUtils.getPixelValue(beforeScreen);
//        HashMap<String, double[]> currentScreenMap = ImageContrastUtils.getPixelValue(currentScreen);
        Integer[] minWH = getMinImage(beforeScreen, currentScreen);
        Mat beforeScreenResize = ImageContrastUtils.resizeImage(beforeScreen, Double.valueOf(minWH[0]), Double.valueOf(minWH[1]));
        Mat currentScreenResize = ImageContrastUtils.resizeImage(currentScreen, Double.valueOf(minWH[0]), Double.valueOf(minWH[1]));
        HashMap<String, double[]> beforeScreenMap = ImageContrastUtils.getPixelValue(beforeScreenResize);
        HashMap<String, double[]> currentScreenMap = ImageContrastUtils.getPixelValue(currentScreenResize);

        //对比像素差异
        if (beforeScreenMap.size() != currentScreenMap.size()) {
            System.out.println("对比阶段:两张图片分辨率不一致");
        }

        System.out.println("全尺寸像素总个数" + beforeScreenMap.size());
        System.out.println("==============测试数据 开始=======================");
        List<Pixel> beforeScreenPixels = beforeScreenMap.entrySet().stream().map(m -> {
            Pixel pixel = new Pixel(m.getKey());
            pixel.setValues(m.getValue());
            return pixel;
        }).collect(Collectors.toList());
        List<Pixel> currentScreenPixels = currentScreenMap.entrySet().stream().map(m -> {
            Pixel pixel = new Pixel(m.getKey());
            pixel.setValues(m.getValue());
            return pixel;
        }).collect(Collectors.toList());

        Integer beforeMinCol = beforeScreenPixels.stream().min(Comparator.comparing(Pixel::getCol)).get().col;
        Integer beforeMinRow = beforeScreenPixels.stream().min(Comparator.comparing(Pixel::getRow)).get().row;
        Integer beforeMaxCol = beforeScreenPixels.stream().max(Comparator.comparing(Pixel::getCol)).get().col;
        Integer beforeMaxRow = beforeScreenPixels.stream().max(Comparator.comparing(Pixel::getRow)).get().row;

        Integer currentMinCol = currentScreenPixels.stream().min(Comparator.comparing(Pixel::getCol)).get().col;
        Integer currentMinRow = currentScreenPixels.stream().min(Comparator.comparing(Pixel::getRow)).get().row;
        Integer currentMaxCol = currentScreenPixels.stream().max(Comparator.comparing(Pixel::getCol)).get().col;
        Integer currentMaxRow = currentScreenPixels.stream().max(Comparator.comparing(Pixel::getRow)).get().row;
        System.out.println("全尺寸最小值:" + beforeMinRow + "," + beforeMinCol + " === " + currentMinRow + "," + currentMinCol);
        System.out.println("全尺寸最大值:" + beforeMaxRow + "," + beforeMaxCol + " === " + currentMaxRow + "," + currentMaxCol);
        System.out.println("==============测试数据 结束=======================");
        //对比像素，找出不同的像素值
        List<Pixel> varyPixels = new ArrayList<>();
        beforeScreenMap.forEach((key, values) -> {
            double[] currentValues = currentScreenMap.get(key);
            if (currentValues.length - values.length == 0) {
                for (int i = 0; i < currentValues.length; i++) {
                    if (currentValues[i] - values[i] != 0) {
                        varyPixels.add(new Pixel(key));
                        break;
                    }
                }
            } else {
                varyPixels.add(new Pixel(key));
            }
        });
        // 使用 varyPixels 绘制变化区域
        System.out.println("有变化的像素个数:" + varyPixels.size());
        if (varyPixels.isEmpty()) {
            System.out.println("无变化");
            return null;
        }
        Integer minCol = varyPixels.stream().min(Comparator.comparing(Pixel::getCol)).get().col;
        Integer minRow = varyPixels.stream().min(Comparator.comparing(Pixel::getRow)).get().row;
        Integer maxCol = varyPixels.stream().max(Comparator.comparing(Pixel::getCol)).get().col;
        Integer maxRow = varyPixels.stream().max(Comparator.comparing(Pixel::getRow)).get().row;
        System.out.println("变化的区域 start point:" + minRow + "," + minCol + " end point:" + maxRow + "," + maxCol);
        String beforeCutOutPath = ImageContrastUtils.cutOut(beforeScreenResize, beforeScreen, minRow, minCol, maxRow, maxCol);
        System.out.println("beforeScreen cutOutPath:" + beforeCutOutPath);
        String currentCutOutPath = ImageContrastUtils.cutOut(currentScreenResize, currentScreen, minRow, minCol, maxRow, maxCol);
        System.out.println("currentScreen cutOutPath:" + currentCutOutPath);
        return currentCutOutPath;
    }

    /**
     * rgb抽象模型
     */
    public static class Pixel {
        private Integer row;
        private Integer col;
        private double[] values;

        public Pixel(String pixel) {
            int index = pixel.indexOf(",");
            this.row = Integer.valueOf(pixel.substring(0, index));
            this.col = Integer.valueOf(pixel.substring(index + 1));
        }

        public Pixel(Integer row, Integer col, double[] values) {
            this.row = row;
            this.col = col;
            this.values = values;
        }

        public Integer getRow() {
            return row;
        }

        public void setRow(Integer row) {
            this.row = row;
        }

        public Integer getCol() {
            return col;
        }

        public void setCol(Integer col) {
            this.col = col;
        }

        public double[] getValues() {
            return values;
        }

        public void setValues(double[] values) {
            this.values = values;
        }
    }

    /**
     *  获取opencv操作的目录，不存在会创建
     * @return opencv操作根目录
     */
    private static String getBasePath() {
        String path = String.join(File.separator, System.getProperty("user.dir"), "opencv");
        File file = new File(path);
        if (!file.exists()) file.mkdirs();
        return file.getPath();
    }

    /**
     * 截取当前屏幕
     */
    private static String screenshot() {
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        assert robot != null;
        MultiResolutionImage mrImage = robot.createMultiResolutionScreenCapture(new Rectangle(dimension));
        Image image = mrImage.getResolutionVariants()
                .stream()
                .reduce((first, second) -> second)
                .orElseThrow();
        //图片写到磁盘
        String path = String.join(File.separator, getBasePath(), System.currentTimeMillis() + ".png");
        File file = new File(path);
        try {
            ImageIO.write((RenderedImage) image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getPath();
    }

}
