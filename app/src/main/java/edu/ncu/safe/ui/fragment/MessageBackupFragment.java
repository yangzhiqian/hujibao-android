package edu.ncu.safe.ui.fragment;

import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.adapter.BackupLVAdapter;
import edu.ncu.safe.domain.BackupInfo;
import edu.ncu.safe.domain.SmsInfo;
import edu.ncu.safe.engine.SmsService;
import edu.ncu.safe.myadapter.BackupBaseFragment;

/**
 * Created by Mr_Yang on 2016/6/1.
 */
public class MessageBackupFragment extends BackupBaseFragment {
    private SmsService smsService;
    @Override
    public void init() {
        smsService = new SmsService(getContext());
        currentType = SHOWTYPE_LOCAL;
        localInfos = loadLocalInfos();
        adapter.setInfos(localInfos);
        adapter.notifyDataSetChanged();
    }

    @Override
    public List<BackupInfo> loadCloudInfos(int beginIndex, int size) {
        List<BackupInfo> backupInfos = new ArrayList<BackupInfo>();
        return backupInfos;
    }

    @Override
    public List<BackupInfo> loadLocalInfos() {
        List<BackupInfo> backupInfos = new ArrayList<BackupInfo>();
        List<SmsInfo> smsInfos = smsService.getSms();

        String title;
        String body;
        long date;
        BackupInfo backupInfo;
        for(SmsInfo info:smsInfos){
            title = info.getAddress()+info.getType();
            body = info.getBody();
            date = info.getDate();
            backupInfo = new BackupInfo(-1,BackupInfo.MESSAGE,null,title,body,date);
            backupInfo.setExtra(info);
            backupInfos.add(backupInfo);
        }
        return backupInfos;

    }

    @Override
    public void onShowPopupClicked(View parent, View view, int position, BackupInfo info) {

    }

    @Override
    public void onDownloadProgressBarClicked(View parent, int position, BackupInfo data) {

    }

    @Override
    public void onCheckBoxCheckedChanged(View parent, int position, BackupInfo data, boolean isChecked) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(adapter.isShowMultiChoice()){
            BackupLVAdapter.ViewHolder holder = (BackupLVAdapter.ViewHolder) view.getTag();
            holder.cb_check.setChecked(!holder.cb_check.isChecked());
            return;
        }
    }
}
