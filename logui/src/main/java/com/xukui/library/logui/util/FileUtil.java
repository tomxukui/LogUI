package com.xukui.library.logui.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtil {

    /**
     * KB与Byte的倍数
     */
    public static final int KB = 1024;

    /**
     * MB与Byte的倍数
     */
    public static final int MB = 1048576;

    /**
     * GB与Byte的倍数
     */
    public static final int GB = 1073741824;

    /**
     * 获取文件大小
     *
     * @param file 文件
     * @return 文件大小
     */
    public static String getFileSize(File file) {
        long length = -1;

        if (file != null && file.isFile() && file.exists()) {
            length = file.length();
        }

        return length == -1 ? "" : byte2FitMemorySize(length);
    }

    /**
     * 字节数转合适内存大小
     * <p>保留3位小数</p>
     *
     * @param byteNum 字节数
     * @return 合适内存大小
     */
    public static String byte2FitMemorySize(long byteNum) {
        if (byteNum < 0) {
            return "";

        } else if (byteNum < KB) {
            return String.format("%.3fB", byteNum + 0.0005);

        } else if (byteNum < MB) {
            return String.format("%.3fKB", byteNum / KB + 0.0005);

        } else if (byteNum < GB) {
            return String.format("%.3fMB", byteNum / MB + 0.0005);

        } else {
            return String.format("%.3fGB", byteNum / GB + 0.0005);
        }
    }


    /**
     * 获取目录下所有文件
     *
     * @param dirPath 目录路径
     * @return 文件链表
     */
    public static List<File> listFilesInDir(String dirPath) {
        return listFilesInDir(getFileByPath(dirPath));
    }

    /**
     * 获取目录下所有文件
     *
     * @param dir 目录
     * @return 文件链表
     */
    public static List<File> listFilesInDir(File dir) {
        if (!isDir(dir)) {
            return null;
        }

        List<File> list = new ArrayList<>();
        File[] files = dir.listFiles();

        if (files != null && files.length != 0) {
            Collections.addAll(list, files);
        }

        return list;
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isFileExists(File file) {
        return file != null && file.exists();
    }

    /**
     * 判断是否是目录
     *
     * @param file 文件
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isDir(File file) {
        return isFileExists(file) && file.isDirectory();
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    public static File getFileByPath(String filePath) {
        if (filePath == null || filePath.trim().length() == 0) {
            return null;

        } else {
            return new File(filePath);
        }
    }

    /**
     * 删除目录下的所有文件
     *
     * @param dirPath 目录路径
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFilesInDir(String dirPath) {
        return deleteFilesInDir(getFileByPath(dirPath));
    }

    /**
     * 删除目录下的所有文件
     *
     * @param dir 目录
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFilesInDir(File dir) {
        if (dir == null) {
            return false;
        }

        // 目录不存在返回true
        if (!dir.exists()) {
            return true;
        }

        // 不是目录返回false
        if (!dir.isDirectory()) {
            return false;
        }

        // 现在文件存在且是文件夹
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!deleteFile(file)) {
                        return false;
                    }

                } else if (file.isDirectory()) {
                    if (!deleteDir(file)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFile(File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }

    /**
     * 删除目录
     *
     * @param dir 目录
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteDir(File dir) {
        if (dir == null) {
            return false;
        }

        // 目录不存在返回true
        if (!dir.exists()) {
            return true;
        }

        // 不是目录返回false
        if (!dir.isDirectory()) {
            return false;
        }

        // 现在文件存在且是文件夹
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!deleteFile(file)) {
                        return false;
                    }

                } else if (file.isDirectory()) {
                    if (!deleteDir(file)) {
                        return false;
                    }
                }
            }
        }

        return dir.delete();
    }

    /**
     * 指定编码按行读取文件到链表中
     *
     * @param file        文件
     * @param charsetName 编码格式
     * @return 文件行链表
     */
    public static List<String> readFile2List(File file, String charsetName) {
        return readFile2List(file, 0, 0x7FFFFFFF, charsetName);
    }

    /**
     * 指定编码按行读取文件到链表中
     *
     * @param file        文件
     * @param st          需要读取的开始行数
     * @param end         需要读取的结束行数
     * @param charsetName 编码格式
     * @return 包含从start行到end行的list
     */
    public static List<String> readFile2List(File file, int st, int end, String charsetName) {
        if (file == null) {
            return null;
        }

        if (st > end) {
            return null;
        }

        BufferedReader reader = null;

        try {
            String line;
            int curLine = 1;
            List<String> list = new ArrayList<>();

            if (charsetName == null || charsetName.trim().length() == 0) {
                reader = new BufferedReader(new FileReader(file));

            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
            }

            while ((line = reader.readLine()) != null) {
                if (curLine > end) {
                    break;
                }

                if (st <= curLine && curLine <= end) {
                    list.add(line);
                }

                ++curLine;
            }

            return list;

        } catch (IOException e) {
            e.printStackTrace();
            return null;

        } finally {
            closeIO(reader);
        }
    }

    /**
     * 关闭IO
     *
     * @param closeables closeable
     */
    public static void closeIO(Closeable... closeables) {
        if (closeables == null) {
            return;
        }

        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将字符串写入文件
     *
     * @param filePath 文件路径
     * @param content  写入内容
     * @param append   是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean writeFileFromString(String filePath, String content, boolean append) {
        return writeFileFromString(getFileByPath(filePath), content, append);
    }

    /**
     * 将字符串写入文件
     *
     * @param file    文件
     * @param content 写入内容
     * @param append  是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean writeFileFromString(File file, String content, boolean append) {
        if (file == null || content == null) {
            return false;
        }

        if (!createOrExistsFile(file)) {
            return false;
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, append));
            bw.write(content);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;

        } finally {
            closeIO(bw);
        }
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(File file) {
        if (file == null) {
            return false;
        }

        // 如果存在，是文件则返回true，是目录则返回false
        if (file.exists()) {
            return file.isFile();
        }

        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }

        try {
            return file.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(File file) {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

}