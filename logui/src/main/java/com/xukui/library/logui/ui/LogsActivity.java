package com.xukui.library.logui.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xukui.library.logui.LogUI;
import com.xukui.library.logui.R;
import com.xukui.library.logui.adapter.LogsRecyclerAdapter;
import com.xukui.library.logui.util.FileUtil;
import com.xukui.library.logui.widget.DividerDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LogsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressBar progress_bar;
    private RecyclerView recycler_logs;

    private LogsRecyclerAdapter mLogsAdapter;

    private FetchLogsTask mFetchLogsTask;
    private ClearLogsTask mClearLogsTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logui_activity_logs);
        initData();
        initView();
        setActionBar();
        setView();

        mFetchLogsTask = new FetchLogsTask();
        mFetchLogsTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFetchLogsTask != null) {
            mFetchLogsTask.cancel(true);
        }

        if (mClearLogsTask != null) {
            mClearLogsTask.cancel(true);
        }
    }

    private void initData() {
        mLogsAdapter = new LogsRecyclerAdapter();
        mLogsAdapter.setOnItemClickListener(new LogsRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(File file, int position) {
                Intent intent = LogActivity.buildIntent(LogsActivity.this, file);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(final File file, int position) {
                if (position == 0) {
                    alertEmptyFile(file);

                } else {
                    alertDeleteFile(file);
                }
            }

        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        progress_bar = findViewById(R.id.progress_bar);
        recycler_logs = findViewById(R.id.recycler_logs);
    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });
    }

    private void setView() {
        recycler_logs.setLayoutManager(new LinearLayoutManager(this));
        recycler_logs.addItemDecoration(new DividerDecoration(this, DividerDecoration.VERTICAL, R.drawable.logui_divider_horizontal_1));
        recycler_logs.setAdapter(mLogsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logui_menu_logs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_clear) {
            List<File> files = mLogsAdapter.getData();

            if (files == null || files.isEmpty()) {
                Toast.makeText(LogsActivity.this, "日志已清空", Toast.LENGTH_SHORT).show();

            } else {
                alertClearAllFiles(files);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 弹窗提示是否清空日志内容
     */
    private void alertEmptyFile(final File file) {
        new AlertDialog.Builder(LogsActivity.this)
                .setTitle("提示")
                .setMessage("是否清空" + file.getName() + "日志?")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean success = FileUtil.writeFileFromString(file, "", false);

                        if (success) {
                            Toast.makeText(LogsActivity.this, "清空日志成功", Toast.LENGTH_SHORT).show();
                        }
                    }

                })
                .create()
                .show();
    }

    /**
     * 弹窗提示是否删除日志
     */
    private void alertDeleteFile(final File file) {
        new AlertDialog.Builder(LogsActivity.this)
                .setTitle("提示")
                .setMessage("是否删除" + file.getName() + "日志?")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean success = FileUtil.deleteFile(file);

                        if (success) {
                            mLogsAdapter.delete(file);

                            Toast.makeText(LogsActivity.this, "删除日志成功", Toast.LENGTH_SHORT).show();
                        }
                    }

                })
                .create()
                .show();
    }

    /**
     * 弹窗提示是否清空所有日志
     */
    private void alertClearAllFiles(final List<File> files) {
        new AlertDialog.Builder(LogsActivity.this)
                .setTitle("提示")
                .setMessage("是否清空所有日志?")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mClearLogsTask != null) {
                            mClearLogsTask.cancel(true);
                        }

                        mClearLogsTask = new ClearLogsTask();
                        mClearLogsTask.execute(mLogsAdapter.getData().toArray(new File[files.size()]));
                    }

                })
                .create()
                .show();
    }

    private class FetchLogsTask extends AsyncTask<Void, Integer, List<File>> {

        @Override
        protected void onPreExecute() {
            progress_bar.setVisibility(View.VISIBLE);
            recycler_logs.setVisibility(View.GONE);
        }

        @Override
        protected List<File> doInBackground(Void... voids) {
            List<File> files = FileUtil.listFilesInDir(LogUI.Dir_Path);

            if (files == null) {
                files = new ArrayList<>();

            } else {
                Collections.sort(files, new Comparator<File>() {

                    @Override
                    public int compare(File o1, File o2) {
                        long t1 = o1.lastModified();
                        long t2 = o2.lastModified();

                        if (t1 < t2) {
                            return 1;

                        } else if (t1 == t2) {
                            return 0;

                        } else {
                            return -1;
                        }
                    }

                });
            }

            return files;
        }

        @Override
        protected void onPostExecute(List<File> files) {
            progress_bar.setVisibility(View.GONE);
            recycler_logs.setVisibility(View.VISIBLE);
            mLogsAdapter.setNewData(files);
        }

    }

    private class ClearLogsTask extends AsyncTask<File, Integer, List<File>> {

        @Override
        protected void onPreExecute() {
            progress_bar.setVisibility(View.VISIBLE);
            recycler_logs.setVisibility(View.GONE);
        }

        @Override
        protected List<File> doInBackground(File... files) {
            List<File> fileList = new ArrayList<>();

            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File item = files[i];

                    if (i == 0) {
                        FileUtil.writeFileFromString(item, "", false);
                        fileList.add(item);

                    } else {
                        boolean success = FileUtil.deleteFile(item);

                        if (!success) {
                            fileList.add(item);
                        }
                    }
                }
            }

            return fileList;
        }

        @Override
        protected void onPostExecute(List<File> files) {
            progress_bar.setVisibility(View.GONE);
            recycler_logs.setVisibility(View.VISIBLE);
            mLogsAdapter.setNewData(files);

            Toast.makeText(LogsActivity.this, "删除所有日志成功", Toast.LENGTH_SHORT).show();
        }

    }

    public static Intent buildIntent(Context context) {
        Intent intent = new Intent(context, LogsActivity.class);
        return intent;
    }

}