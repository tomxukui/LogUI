package com.xukui.library.logui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.xukui.library.logui.R;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class LogTagsRecyclerAdapter extends RecyclerView.Adapter<LogTagsRecyclerAdapter.RowHolder> {

    private Map<String, Boolean> mTagMap;
    private String[] mTags;

    private LayoutInflater mInflater;

    @Nullable
    private OnItemCheckListener mOnItemCheckListener;

    @Override
    public int getItemCount() {
        return mTagMap == null ? 0 : mTagMap.size();
    }

    @Override
    public RowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }

        View view = mInflater.inflate(R.layout.logui_item_recycler_log_tags, parent, false);
        return new RowHolder(view);
    }

    @Override
    public void onBindViewHolder(RowHolder vh, final int position) {
        final String tag = mTags[position];
        boolean checked = mTagMap.get(tag);

        vh.check_tag.setText(tag);
        vh.check_tag.setChecked(checked);

        vh.check_tag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTagMap.put(tag, isChecked);

                if (mOnItemCheckListener != null) {
                    mOnItemCheckListener.onItemCheck(tag, isChecked, position);
                }
            }
        });
    }

    public Set<String> getCheckedTags() {
        Set<String> tags = new HashSet<>();

        if (mTagMap != null) {
            for (Map.Entry<String, Boolean> entry : mTagMap.entrySet()) {
                if (entry.getValue()) {
                    tags.add(entry.getKey());
                }
            }
        }

        return tags;
    }

    public void setNewData(Set<String> tags) {
        Map<String, Boolean> map = new TreeMap<>();

        if (tags != null) {
            for (String tag : tags) {
                if (mTagMap != null && mTagMap.containsKey(tag)) {
                    map.put(tag, mTagMap.get(tag));

                } else {
                    map.put(tag, true);
                }
            }
        }

        mTagMap = map;
        mTags = map.keySet().toArray(new String[0]);

        notifyDataSetChanged();
    }

    static class RowHolder extends RecyclerView.ViewHolder {

        CheckBox check_tag;

        public RowHolder(View view) {
            super(view);
            check_tag = view.findViewById(R.id.check_tag);
        }

    }

    public void setOnItemCheckListener(@Nullable OnItemCheckListener listener) {
        mOnItemCheckListener = listener;
    }

    public interface OnItemCheckListener {

        void onItemCheck(String tag, boolean checked, int position);

    }

}