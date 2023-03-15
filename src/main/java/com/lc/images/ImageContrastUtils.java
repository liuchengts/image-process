package com.lc.images;

import org.opencv.core.*;

import java.util.HashMap;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.*;

/**
 * @author cheng.liu
 * @version 1.0
 * @description: TODO
 * @date 2023/1/31 11:43
 */
public class ImageContrastUtils {

    static {
        System.load(ClassLoader.getSystemResource("libs/opencv_java470.dll").getPath());
    }

    /**
     * 获取图像分辨率
     *
     * @param imagePath 图像路径
     * @return 宽度*高度
     */
    public static Integer[] getResolution(String imagePath) {
        Mat imageMat = imread(imagePath);
        if (imageMat.empty()) {
            System.out.println("图片不存在:" + imagePath);
        }
        //宽度，高度
        return new Integer[]{imageMat.cols(), imageMat.rows()};
    }

    /**
     * 裁剪图像
     *
     * @param sourceImage 要裁剪的源图像
     * @param outPath     预期裁剪后的图像路径（会在这个路径最后加上”cutOut“后缀）
     * @param minRow      像素最小行
     * @param minCol      像素最小列
     * @param maxRow      像素最大行
     * @param maxCol      像素最大列
     * @return 裁剪后的图像输出路径
     */
    public static String cutOut(Mat sourceImage, String outPath, Integer minRow, Integer minCol, Integer maxRow, Integer maxCol) {
        //剪切范围
        Rect rect = new Rect(new Point(minCol, minRow), new Point(maxCol, maxRow));
        //设置roi
        Mat imageROI = new Mat(sourceImage, rect);
        //剪切目标
        Mat cutImage = new Mat();
        imageROI.copyTo(cutImage);
        String path = getCreateImageName(outPath, "cutOut");
        imshow("cutOut", cutImage);
        imwrite(path, cutImage);
        return path;
    }

    private static String getPixelKey(Integer row, Integer col) {
        return String.join(",", String.valueOf(row), String.valueOf(col));
    }

    /**
     * 压缩图像分辨率
     *
     * @param imagePath 图像路径
     * @param width     压缩后的宽度，默认200
     * @param height    压缩后的高度，默认200
     * @return 压缩后的图像
     */
    public static Mat resizeImage(String imagePath, Double width, Double height) {
        Mat imageMat = imread(imagePath);
        if (imageMat.empty()) {
            System.out.println("图片不存在:" + imagePath);
        }
        if (width == null || width <= 0) {
            width = 200d;
        }
        if (height == null || height <= 0) {
            height = 200d;
        }
        Mat mat = new Mat();//缩放之后的图片
        resize(imageMat, mat, new Size(width, height), INTER_AREA);//缩小图片
//        resize(imageMat, mat, new Size(width, height), 0.5, 0.5, INTER_AREA);//缩小图片
        imshow("resize", mat);
        return mat;
    }

    /**
     * 获取图像的像素值
     *
     * @param imagePath 图像路径
     * @return 像素坐标 #getPixelKey 通道:值
     */
    public static HashMap<String, double[]> getPixelValue(String imagePath) {
        Mat imageMat = imread(imagePath);
        if (imageMat.empty()) {
            System.out.println("图片不存在:" + imagePath);
        }
        return getPixelValue(imageMat);
    }

    /**
     * 获取图像的像素值
     *
     * @param imageMat 图像
     */
    public static HashMap<String, double[]> getPixelValue(Mat imageMat) {
        HashMap<String, double[]> map = new HashMap<>();
        //图像行，高度
        int rows = imageMat.rows();
        //图像列，宽度
        int cols = imageMat.cols();
        //图像通道，维度
//        int channels = imageMat.channels();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                map.put(getPixelKey(i, j), imageMat.get(i, j));
            }
        }
        return map;
    }

    /**
     * 将图像置灰-保留一个通道
     *
     * @param imagePath 图像路径
     * @return opencv可操作实例
     */
    public static Mat grayImage(String imagePath) {
        Mat image = imread(imagePath);
        if (image.empty()) {
            System.out.println("图片不存在:" + imagePath);
        }
        Mat grayImage = new Mat(image.rows(), image.cols(), CvType.CV_8SC1);
        cvtColor(image, grayImage, COLOR_RGB2GRAY);
//        imwrite(getCreateImageName(imagePath, "gray"), grayImage);
        imshow("gray", grayImage);
        return grayImage;
    }

    private static String getCreateImageName(String imagePath, String prefix) {
        return imagePath.replace(".", "_" + prefix + ".");
    }
}
