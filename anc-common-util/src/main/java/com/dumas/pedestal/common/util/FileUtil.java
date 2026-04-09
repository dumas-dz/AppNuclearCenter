package com.dumas.pedestal.common.util;

import cn.hutool.core.lang.UUID;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件工具类
 *
 * @author andaren
 * @version V1.0
 * @since 2019-10-22 15:09
 */
public class FileUtil {
    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";
    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    public static String readFile(String path) {
        Path filePath = Paths.get(path);
        byte[] contentBytes = null;
        try {
            contentBytes = Files.readAllBytes(filePath);
            return new String(contentBytes, StandardCharsets.UTF_8);
        } catch (IOException ioe) {
           log.error("Failed to read file: {}", path, ioe);
           return null;
        }
    }

    public static void writeString(String path, String content) {
        Path filePath = Paths.get(path);
        byte[] contentBytes = content.getBytes();
        try {
            Files.write(filePath, contentBytes);
        } catch (IOException ioe) {
            log.error("Failed to write string to file: {}", path, ioe);
        }
    }

    public static void writeJson2File(String jsonData, String path) {
        Path filePath = Paths.get(path);
        try {
            Files.write(filePath, jsonData.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException ioe) {
            log.error("Failed to write JSON to file: {}", path, ioe);
        }
    }

    /**
     * 输出指定文件的byte数组
     *
     * @param filePath 文件路径
     * @param os       输出流
     * @return
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException {
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException(filePath);
            }
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e1) {
                    log.error("Failed to close output stream", e1);
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                    log.error("Failed to close file input stream", e1);
                }
            }
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件
     * @return
     */
    public static boolean deleteFile(String filePath) {
        boolean flag = false;
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 文件名称验证
     *
     * @param filename 文件名称
     * @return true 正常 false 非法
     */
    public static boolean isValidFilename(String filename) {
        return filename.matches(FILENAME_PATTERN);
    }

    /**
     * 将文件的字节数转换成文件的大小
     * @author krry
     * @param size
     * @return String
     */
    public static String format(long size){
        float fsize = size;
        String fileSizeString;
        if (fsize < 1024) {
            fileSizeString = String.format("%.2f", fsize) + "B"; //2f表示保留两位小数
        } else if (fsize < 1048576) {
            fileSizeString = String.format("%.2f", fsize/1024) + "KB";
        } else if (fsize < 1073741824) {
            fileSizeString = String.format("%.2f", fsize/1024/1024) + "MB";
        } else if (fsize < 1024 * 1024 * 1024) {
            fileSizeString = String.format("%.2f", fsize/1024/1024/1024) + "GB";
        } else {
            fileSizeString = "0B";
        }
        return fileSizeString;
    }

    public static String generate(String filePrefix) {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String newFileName = UUID.fastUUID().toString(true);
        return filePrefix + File.separator + today + File.separator + newFileName;
    }

    public static void main(String[] args) {
        System.out.println(generate("aaa"));
    }

}
