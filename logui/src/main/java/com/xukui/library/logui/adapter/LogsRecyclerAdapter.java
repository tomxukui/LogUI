package com.xukui.library.logui.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xukui.library.logui.R;
import com.xukui.library.logui.util.FileUtil;
import com.xukui.library.logui.util.TimeUtil;

import java.io.File;
import java.util.List;

public class LogsRecyclerAdapter extends RecyclerView.Adapter<LogsRecyclerAdapter.RowHolder> {

    private List<File> mFiles;

    private LayoutInflater mInflater;

    @Nullable
    private OnItemClickListener mOnItemClickListener;

    @Override
    public int getItemCount() {
        return mFiles == null ? 0 : mFiles.size();
    }

    public File getItem(int position) {
        if (mFiles != null && position >= 0 && position < mFiles.size()) {
            return mFiles.get(position);

        } else {
            return null;
        }
    }

    @Override
    public RowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }

        View view = mInflater.inflate(R.layout.logui_item_recycler_dev_logs, parent, false);
        return new RowHolder(view);
    }

    @Override
    public void onBindViewHolder(RowHolder vh, final int position) {
        final File file = mFiles.get(position);

        vh.linear_row.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(file, position);
                }
            }

        });
        vh.linear_row.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemLongClick(file, position);
                }
                return true;
            }

        });

        vh.tv_name.setText(file.getName());
        vh.tv_info.setText(FileUtil.getFileSize(file) + " | " + TimeUtil.millis2String(file.lastModified(), "yyyy/MM/dd HH:mm:ss"));
    }

    public void setNewData(List<File> files) {
        mFiles = files;

        notifyDataSetChanged();
    }

    public void delete(File file) {
        if (mFiles != null && file != null && mFiles.contains(file)) {
            int position = mFiles.indexOf(file);

            mFiles.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        if (mFiles != null) {
            mFiles.clear();
        }

        notifyDataSetChanged();
    }

    public List<File> getData() {
        return mFiles;
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    static class RowHolder extends RecyclerView.ViewHolder {

        LinearLayout linear_row;
        TextView tv_name;
        TextView tv_info;

        public RowHolder(View view) {
            super(view);
            linear_row = view.findViewById(R.id.linear_row);
            tv_name = view.findViewById(R.id.tv_name);
            tv_info = view.findViewById(R.id.tv_info);
        }

    }

    public interface OnItemClickListener {

        void onItemClick(File file, int position);

        void onItemLongClick(File file, int position);

    }

}