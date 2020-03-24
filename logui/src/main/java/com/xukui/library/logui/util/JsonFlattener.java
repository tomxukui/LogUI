package com.xukui.library.logui.util;

import com.elvishew.xlog.flattener.Flattener;
import com.elvishew.xlog.flattener.Flattener2;
import com.xukui.library.logui.bean.LogItem;

public class JsonFlattener implements Flattener, Flattener2 {

    @Override
    public CharSequence flatten(int logLevel, String tag, String message) {
        return flatten(System.currentTimeMillis(), logLevel, tag, message);
    }

    @Override
    public CharSequence flatten(long timeMillis, int logLevel, String tag, String message) {
        LogItem logItem = new LogItem(timeMillis, logLevel, tag, message);

        return logItem.toJsonString();
    }

}