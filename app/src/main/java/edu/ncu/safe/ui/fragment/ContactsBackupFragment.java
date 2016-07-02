package edu.ncu.safe.ui.fragment;

import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.domain.BackupInfo;
import edu.ncu.safe.domain.ContactsInfo;
import edu.ncu.safe.engine.ContactsService;
import edu.ncu.safe.myadapter.BackupBaseFragment;

/**
 * Created by Mr_Yang on 2016/6/1.
 */
public class ContactsBackupFragment extends BackupBaseFragment {
    private ContactsService contactsService;
    @Override
    public void init() {
        contactsService = new ContactsService(getContext());
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
        List<ContactsInfo> contactsInfos = contactsService.getContactsInfos();
        List<BackupInfo> infos = new ArrayList<BackupInfo>();
        for(int i=0;i<contactsInfos.size();i++){
            backupInfos.add(new BackupInfo(i,BackupInfo.CONTACTS,null,contactsInfos.get(i).getName(),contactsInfos.get(i).getPhoneNumber(),0));
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

    }
}
