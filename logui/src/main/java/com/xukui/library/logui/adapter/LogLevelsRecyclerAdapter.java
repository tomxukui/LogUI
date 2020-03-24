package com.xukui.library.logui.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.xukui.library.logui.LogUI;
import com.xukui.library.logui.R;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class LogLevelsRecyclerAdapter extends RecyclerView.Adapter<LogLevelsRecyclerAdapter.RowHolder> {

    private Map<Integer, Boolean> mLevelMap;
    private Integer[] mLevels;

    private LayoutInflater mInflater;

    @Nullable
    private OnItemCheckListener mOnItemCheckListener;

    @Override
    public int getItemCount() {
        return mLevelMap == null ? 0 : mLevelMap.size();
    }

    @Override
    public RowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }

        View view = mInflater.inflate(R.layout.logui_item_recycler_log_levels, parent, false);
        return new RowHolder(view);
    }

    @Override
    public void onBindViewHolder(RowHolder vh, final int position) {
        final int level = mLevels[position];
        boolean checked = mLevelMap.get(level);

        vh.check_level.setText(LogUI.getTagName(level));
        vh.check_level.setChecked(checked);
        vh.check_level.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mLevelMap.put(level, isChecked);

                if (mOnItemCheckListener != null) {
                    mOnItemCheckListener.onItemCheck(level, isChecked, position);
                }
            }
        });
    }

    public Set<Integer> getCheckedLevels() {
        Set<Integer> levels = new HashSet<>();

        if (mLevelMap != null) {
            for (Map.Entry<Integer, Boolean> entry : mLevelMap.entrySet()) {
                if (entry.getValue()) {
                    levels.add(entry.getKey());
                }
            }
        }

        return levels;
    }

    public void setNewData(Set<Integer> levels) {
        Map<Integer, Boolean> map = new TreeMap<>();

        if (levels != null) {
            for (Integer level : levels) {
                if (mLevelMap != null && mLevelMap.containsKey(level)) {
                    map.put(level, mLevelMap.get(level));

                } else {
                    map.put(level, true);
                }
            }
        }

        mLevelMap = map;
        mLevels = map.keySet().toArray(new Integer[0]);

        notifyDataSetChanged();
    }

    static class RowHolder extends RecyclerView.ViewHolder {

        CheckBox check_level;

        public RowHolder(View view) {
            super(view);
            check_level = view.findViewById(R.id.check_level);
        }

    }

    public void setOnItemCheckListener(@Nullable OnItemCheckListener listener) {
        mOnItemCheckListener = listener;
    }

    public interface OnItemCheckListener {

        void onItemCheck(int level, boolean checked, int position);

    }

}