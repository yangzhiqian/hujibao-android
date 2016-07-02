package edu.ncu.safe.ui.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.domain.BackupInfo;
import edu.ncu.safe.myadapter.BackupBaseFragment;
import edu.ncu.safe.ui.TouchImageViewActivity;

/**
 * Created by Mr_Yang on 2016/6/1.
 */
public class PictureBackupFragment extends BackupBaseFragment {
    @Override
    public void init() {
        showLocal();
    }
    @Override
    public List<BackupInfo> loadCloudInfos(int beginIndex, int size) {
        new AsyncTask<Integer,Integer, List<BackupInfo>>(){
            @Override
            protected List<BackupInfo> doInBackground(Integer... params) {
                return null;
            }
        };
        return null;
    }

    @Override
    public List<BackupInfo> loadLocalInfos() {
        new AsyncTask<Void, Integer, List<BackupInfo>>() {
            @Override
            protected List<BackupInfo> doInBackground(Void... params) {
                List<BackupInfo> backupInfos = new ArrayList<BackupInfo>();
                ContentResolver resolver = getContext().getContentResolver();
                Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
                String path;
                File file;
                String name;
                long time;
                long size;
                BackupInfo info;
                while (cursor.moveToNext()) {
                    path = cursor.getString(cursor.getColumnIndex("_data"));
                    file = new File(path);
                    name = file.getName();
                    time = file.lastModified();
                    size = file.length();
                    backupInfos.add(new BackupInfo(-1, BackupInfo.PICTURE, path, name, new SimpleDateFormat("yy/MM/dd HH:mm").format(new Date(time)), size));
                }
                return backupInfos;
            }

            @Override
            protected void onPostExecute(List<BackupInfo> backupInfos) {
                localInfos = backupInfos;
                if (currentType == SHOWTYPE_LOCAL) {
                    adapter.setInfos(localInfos);
                    adapter.notifyDataSetChanged();
                    showLoader(false);
                }
                super.onPostExecute(backupInfos);
            }
        }.execute();
        return null;
    }

    @Override
    public void onShowPopupClicked(View parent, View view, final int position, BackupInfo info) {
        switch (getCurrentType()) {
            case SHOWTYPE_LOCAL:
            case SHOWTYPE_BACKUP:
                showPopup(view, getBackupView(parent, position, info));
                break;
            case SHOWTYPE_CLOUD:
            case SHOWTYPE_RECOVERY:
                showPopup(view, getRecorveryView(parent, position, info));
                break;
        }
    }

    private View getRecorveryView(View parent, int position, final BackupInfo info) {
        LinearLayout layout = getLayout();
        TextView tv_bk = getTextView("恢复到本机");
        TextView tv_del = getTextView("删除");
        layout.addView(tv_bk);
        layout.addView(getDivider());
        layout.addView(tv_del);

        tv_bk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info.setIsInDownload(true);
                adapter.notifyDataSetChanged();
                popupWindow.dismiss();
            }
        });
        tv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        return layout;
    }

    private View getBackupView(View parent, int position, final BackupInfo info) {
        LinearLayout layout = getLayout();
        TextView tv_bk = getTextView("备份到云端");
        TextView tv_del = getTextView("删除");
        layout.addView(tv_bk);
        layout.addView(getDivider());
        layout.addView(tv_del);

        tv_bk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info.setIsInDownload(true);
                adapter.notifyDataSetChanged();
                popupWindow.dismiss();
            }
        });
        tv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        return layout;
    }


    @Override
    public void onDownloadProgressBarClicked(View parent, int position, BackupInfo data) {

    }

    @Override
    public void onCheckBoxCheckedChanged(View parent, int position, BackupInfo data, boolean isChecked) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BackupInfo backupInfo = adapter.getInfos().get(position);
        Intent intent = new Intent();
        intent.setClass(getContext(), TouchImageViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("backupinfo", backupInfo);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}