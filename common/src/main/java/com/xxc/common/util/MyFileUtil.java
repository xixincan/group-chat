package com.xxc.common.util;

import cn.hutool.log.StaticLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xixincan
 * 2020-03-16
 *
 * @version 1.0.0
 */
public class MyFileUtil {

    private static final String B_UNIT = "B";
    private static final String KB_UNIT = "KB";
    private static final String MB_UNIT = "MB";
    private static final String GB_UNIT = "GB";
    private static final DecimalFormat FORMAT = new DecimalFormat("#.0");
    private static final Map<String, String> FILE_TYPES = new HashMap<>();
    static {
        // BOM（Byte Order Mark）文件头字节
        FILE_TYPES.put("494433", "mp3");
        FILE_TYPES.put("524946", "wav");
        FILE_TYPES.put("ffd8ff", "jpg");
        FILE_TYPES.put("FFD8FF", "jpg");
        FILE_TYPES.put("89504E", "png");
        FILE_TYPES.put("89504e", "png");
        FILE_TYPES.put("474946", "gif");
    }

    /**
     * 描述：通过含BOM（Byte Order Mark）的文件头的
     * 前 3个字节判断文件类型
     */
    public static String getFileType(String filePath) {
        return FILE_TYPES.get(getFileHeader3(filePath));
    }

    /**
     * 描述：获取文件头前3个字节
     */
    private static String getFileHeader3(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || file.length() < 4) {
            return "null";
        }

        String value = null;
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[3];
            int read = fileInputStream.read(bytes, 0, bytes.length);
            value = bytesToHexString(bytes);
        } catch (Exception exp) {
            StaticLog.error(exp);
        }
        return value;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte item : src) {
            int hex = item & 0xFF;
            String hexString = Integer.toHexString(hex);
            if (hexString.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hexString);
        }
        return stringBuilder.toString();
    }

    /**
     * 描述： 获取格式化的文件大小
     * 格式为带单位保留一位小数
     */
    public static String getFormatSize(double size) {
        String fileSizeString;
        if (size < 1024) {
            fileSizeString = FORMAT.format(size) + B_UNIT;
        } else if (size < 1048576) {
            fileSizeString = FORMAT.format(size / 1024) + KB_UNIT;
        } else if (size < 1073741824) {
            fileSizeString = FORMAT.format(size / 1048576) + MB_UNIT;
        } else {
            fileSizeString = FORMAT.format(size / 1073741824) + GB_UNIT;
        }
        return fileSizeString;
    }

    public static String getFormatSize(long size) {
        return getFormatSize((double) size);
    }

    /**
     * 描述：高效率的将文件转换成字节数组
     */
    public static byte[] toByteArray(String filePath) throws IOException {
        try (FileChannel fileChannel = new RandomAccessFile(filePath, "r").getChannel()) {
            MappedByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0,
                    fileChannel.size()).load();
            System.out.println(byteBuffer.isLoaded());
            byte[] result = new byte[(int) fileChannel.size()];
            if (byteBuffer.remaining() > 0) {
                byteBuffer.get(result, 0, byteBuffer.remaining());
            }
            return result;
        } catch (IOException ioe) {
            StaticLog.error(ioe);
            throw ioe;
        }
    }
}
