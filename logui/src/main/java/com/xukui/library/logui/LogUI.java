package com.xukui.library.logui;

import android.graphics.Color;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy;
import com.elvishew.xlog.printer.file.clean.NeverCleanStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.xukui.library.logui.util.JsonFlattener;

public class LogUI {

    public static String Dir_Path;

    private LogUI() {
    }

    /**
     * 初始化, 建议在application中执行
     */
    public static void init(boolean enable, String dirPath) {
        Dir_Path = dirPath;

        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(enable ? LogLevel.ALL : LogLevel.NONE)
                .build();

        //编辑器打印器
        Printer editorPrinter = new AndroidPrinter();

        //文件打印器
        Printer filePrinter = new FilePrinter.Builder(Dir_Path)
                .fileNameGenerator(new DateFileNameGenerator())
                .backupStrategy(new FileSizeBackupStrategy(1024 * 1024))
                .cleanStrategy(new NeverCleanStrategy())
                .flattener(new JsonFlattener())
                .build();

        XLog.init(config, editorPrinter, filePrinter);
    }

    /**
     * 啰嗦输出(黑色)
     */
    public static void v(String tag, String msg) {
        XLog.tag(tag).v(msg);
    }

    /**
     * 调试输出(蓝色)
     */
    public static void d(String tag, String msg) {
        XLog.tag(tag).d(msg);
    }

    /**
     * 提示输出(绿色)
     */
    public static void i(String tag, String msg) {
        XLog.tag(tag).i(msg);
    }

    /**
     * 警告输出(橙色)
     */
    public static void w(String tag, String msg) {
        XLog.tag(tag).w(msg);
    }

    /**
     * 错误输出(红色)
     */
    public static void e(String tag, String msg) {
        XLog.tag(tag).e(msg);
    }

    /**
     * 带异常信息的错误输出(红色)
     */
    public static void e(String tag, String msg, Throwable tr) {
        XLog.tag(tag).e(msg, tr);
    }

    /**
     * JSON输出(蓝色)
     */
    public static void json(String tag, String json) {
        XLog.tag(tag).json(json);
    }

    /**
     * XML输出(蓝色)
     */
    public static void xml(String tag, String xml) {
        XLog.tag(tag).xml(xml);
    }

    /**
     * 根据等级获取Tag颜色
     */
    public static int getTagColor(int level) {
        switch (level) {

            case 2:
                return Color.parseColor("#000000");

            case 3:
                return Color.parseColor("#1E90FF");

            case 4:
                return Color.parseColor("#00EE76");

            case 5:
                return Color.parseColor("#F4A460");

            case 6:
                return Color.parseColor("#FF0000");

            default:
                return Color.parseColor("#cccccc");

        }
    }

    /**
     * 根据等级获取Tag名称
     */
    public static String getTagName(int level) {
        switch (level) {

            case 2:
                return "V";

            case 3:
                return "D";

            case 4:
                return "I";

            case 5:
                return "W";

            case 6:
                return "E";

            default:
                return "ALL";

        }
    }

}