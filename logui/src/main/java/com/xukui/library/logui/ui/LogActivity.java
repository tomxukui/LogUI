package com.xukui.library.logui.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xukui.library.logui.R;
import com.xukui.library.logui.adapter.LogLevelsRecyclerAdapter;
import com.xukui.library.logui.adapter.LogRecyclerAdapter;
import com.xukui.library.logui.adapter.LogTagsRecyclerAdapter;
import com.xukui.library.logui.bean.LogItem;
import com.xukui.library.logui.util.ClipboardUtil;
import com.xukui.library.logui.util.FileUtil;
import com.xukui.library.logui.widget.DividerDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LogActivity extends AppCompatActivity {

    private static final String EXTRA_FILE = "EXTRA_FILE";

    private Toolbar toolbar;
    private SearchView searchView;
    private LinearLayout linear_levels;
    private RecyclerView recycler_levels;
    private LinearLayout linear_tags;
    private RecyclerView recycler_tags;
    private ProgressBar progress_bar;
    private RecyclerView recycler_log;

    private LogLevelsRecyclerAdapter mLevelsAdapter;
    private LogTagsRecyclerAdapter mTagsAdapter;
    private LogRecyclerAdapter mLogAdapter;

    private FetchLogTask mFetchLogTask;

    private File mFile;
    private Set<Integer> mLevels;
    private Set<String> mTags;
    private List<LogItem> mLogItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logui_activity_log);
        initData();
        initView();
        setActionBar();
        setView();

        mFetchLogTask = new FetchLogTask();
        mFetchLogTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFetchLogTask != null) {
            mFetchLogTask.cancel(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logui_menu_log, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryHint("输入关键词搜索");

        SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setBackgroundResource(R.drawable.logui_bg_search);
        searchAutoComplete.setHintTextColor(Color.parseColor("#30FFFFFF"));
        searchAutoComplete.setTextColor(Color.WHITE);
        searchAutoComplete.setTextSize(13);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                setLogView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                setLogView();
                return false;
            }

        });
        return true;
    }

    private void initData() {
        mFile = (File) getIntent().getSerializableExtra(EXTRA_FILE);

        mLevelsAdapter = new LogLevelsRecyclerAdapter();
        mLevelsAdapter.setOnItemCheckListener(new LogLevelsRecyclerAdapter.OnItemCheckListener() {

            @Override
            public void onItemCheck(int level, boolean checked, int position) {
                setLogView();
            }

        });

        mTagsAdapter = new LogTagsRecyclerAdapter();
        mTagsAdapter.setOnItemCheckListener(new LogTagsRecyclerAdapter.OnItemCheckListener() {

            @Override
            public void onItemCheck(String tag, boolean checked, int position) {
                setLogView();
            }

        });

        mLogAdapter = new LogRecyclerAdapter();
        mLogAdapter.setOnItemClickListener(new LogRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(LogItem logItem, int position) {
                String message = logItem.getMessage();

                ClipboardUtil.copyText(LogActivity.this, message);
                Toast.makeText(LogActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        linear_levels = findViewById(R.id.linear_levels);
        recycler_levels = findViewById(R.id.recycler_levels);
        linear_tags = findViewById(R.id.linear_tags);
        recycler_tags = findViewById(R.id.recycler_tags);
        progress_bar = findViewById(R.id.progress_bar);
        recycler_log = findViewById(R.id.recycler_log);
    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mFile.getName());
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });
    }

    private void setView() {
        recycler_levels.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recycler_levels.setAdapter(mLevelsAdapter);

        recycler_tags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recycler_tags.setAdapter(mTagsAdapter);

        recycler_log.setLayoutManager(new LinearLayoutManager(this));
        recycler_log.addItemDecoration(new DividerDecoration(this, DividerDecoration.VERTICAL, R.drawable.logui_divider_horizontal_10_trans));
        recycler_log.setAdapter(mLogAdapter);
    }

    private void setLogView(Set<Integer> levels, Set<String> tags, String keywords) {
        List<LogItem> logItems = new ArrayList<>();

        if (mLogItems != null) {
            for (LogItem logItem : mLogItems) {
                if (levels == null || levels.contains(logItem.getLevel())) {
                    if (tags == null || tags.contains(logItem.getTag())) {
                        if (TextUtils.isEmpty(keywords) || (logItem.getMessage() != null && logItem.getMessage().toLowerCase().contains(keywords.toLowerCase()))) {
                            logItems.add(logItem);
                        }
                    }
                }
            }
        }

        mLogAdapter.setNewData(logItems);
    }

    private void setLogView() {
        Set<Integer> levels = mLevelsAdapter.getCheckedLevels();
        Set<String> tags = mTagsAdapter.getCheckedTags();
        String keywords = (searchView == null ? null : searchView.getQuery().toString().trim());

        setLogView(levels, tags, keywords);
    }

    private class FetchLogTask extends AsyncTask<Void, Integer, Object[]> {

        @Override
        protected void onPreExecute() {
            progress_bar.setVisibility(View.VISIBLE);
            recycler_log.setVisibility(View.GONE);
        }

        @Override
        protected Object[] doInBackground(Void... voids) {
            List<String> jsons = FileUtil.readFile2List(mFile, "utf-8");

            if (jsons == null) {
                jsons = new ArrayList<>();
            }

            Set<Integer> levles = new HashSet<>();
            Set<String> tags = new HashSet<>();
            List<LogItem> logItems = new ArrayList<>();

            for (String json : jsons) {
                LogItem logItem = new LogItem().parseJson(json);

                levles.add(logItem.getLevel());
                tags.add(logItem.getTag());
                logItems.add(0, logItem);
            }

            return new Object[]{levles, tags, logItems};
        }

        @Override
        protected void onPostExecute(Object[] objects) {
            mLevels = (Set<Integer>) objects[0];
            mTags = (Set<String>) objects[1];
            mLogItems = (List<LogItem>) objects[2];

            progress_bar.setVisibility(View.GONE);

            linear_levels.setVisibility((mLevels == null || mLevels.isEmpty()) ? View.GONE : View.VISIBLE);
            mLevelsAdapter.setNewData(mLevels);

            linear_tags.setVisibility((mTags == null || mTags.isEmpty()) ? View.GONE : View.VISIBLE);
            mTagsAdapter.setNewData(mTags);

            recycler_log.setVisibility(View.VISIBLE);
            setLogView();
        }

    }

    public static Intent buildIntent(Context context, File file) {
        Intent intent = new Intent(context, LogActivity.class);
        intent.putExtra(EXTRA_FILE, file);
        return intent;
    }

}