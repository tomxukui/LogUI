package com.xukui.library.logui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.xukui.library.logui.LogUI;
import com.xukui.library.logui.R;
import com.xukui.library.logui.bean.LogItem;
import com.xukui.library.logui.util.TimeUtil;
import com.xukui.library.logui.bean.LogItem;

import java.util.List;

public class LogRecyclerAdapter extends RecyclerView.Adapter<LogRecyclerAdapter.RowHolder> {

    private List<LogItem> mLogItems;

    private LayoutInflater mInflater;

    @Nullable
    private OnItemClickListener mOnItemClickListener;

    @Override
    public int getItemCount() {
        return mLogItems == null ? 0 : mLogItems.size();
    }

    @Override
    public RowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }

        View view = mInflater.inflate(R.layout.logui_item_recycler_log, parent, false);
        return new RowHolder(view);
    }

    @Override
    public void onBindViewHolder(RowHolder vh, final int position) {
        final LogItem logItem = mLogItems.get(position);

        vh.linear_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(logItem, position);
                }
            }
        });

        vh.linear_tag.setBackgroundColor(LogUI.getTagColor(logItem.getLevel()));
        vh.tv_level.setText(LogUI.getTagName(logItem.getLevel()));
        vh.tv_tag.setText(logItem.getTag());
        vh.tv_time.setText(TimeUtil.millis2String(logItem.getTime(), "yyyy/MM/dd HH:mm:ss"));
        vh.tv_message.setText(logItem.getMessage());
    }

    public void setNewData(List<LogItem> logItems) {
        mLogItems = logItems;

        notifyDataSetChanged();
    }

    static class RowHolder extends RecyclerView.ViewHolder {

        LinearLayout linear_row;
        LinearLayout linear_tag;
        TextView tv_level;
        TextView tv_tag;
        TextView tv_time;
        TextView tv_message;

        public RowHolder(View view) {
            super(view);
            linear_row = view.findViewById(R.id.linear_row);
            linear_tag = view.findViewById(R.id.linear_tag);
            tv_level = view.findViewById(R.id.tv_level);
            tv_tag = view.findViewById(R.id.tv_tag);
            tv_time = view.findViewById(R.id.tv_time);
            tv_message = view.findViewById(R.id.tv_message);
        }

    }

    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {

        void onItemClick(LogItem logItem, int position);

    }

}