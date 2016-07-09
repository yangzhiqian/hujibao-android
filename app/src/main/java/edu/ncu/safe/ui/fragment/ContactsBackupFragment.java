package edu.ncu.safe.ui.fragment;

import android.view.View;
import android.widget.AdapterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.domain.ContactsInfo;
import edu.ncu.safe.domain.User;
import edu.ncu.safe.domainadapter.ContactsAdapter;
import edu.ncu.safe.domainadapter.ITarget;
import edu.ncu.safe.engine.ContactsService;
import edu.ncu.safe.myadapter.BackupBaseFragment;

/**
 * Created by Mr_Yang on 2016/6/1.
 */
public class ContactsBackupFragment extends BackupBaseFragment {
    public ContactsBackupFragment(User user,int type){
        super(user, type);
    }
    @Override
    public void init() {
        currentShowType = SHOWTYPE_LOCAL;
        loadLocalInfos();;
    }

    @Override
    public List<ITarget> loadLocalInfos() {
        List<ITarget> infos = new ArrayList<ITarget>();
        List<ContactsInfo> contactsInfos = new ContactsService(getContext()).getContactsInfos();
        for(ContactsInfo info : contactsInfos){
            infos.add(new ContactsAdapter(info));
        }
        localInfos = infos;
        adapter.setInfos(localInfos);
        adapter.notifyDataSetChanged();
        showLoader(false);
        if(ptr.isRefreshing()){
            ptr.refreshComplete();
        }
        return infos;
    }

    @Override
    public void onDownloadProgressBarClicked(View parent, int position, ITarget data) {

    }

    @Override
    public void onCheckBoxCheckedChanged(View parent, int position, ITarget data, boolean isChecked) {

    }

    @Override
    public List<ITarget> parseToInfos(String json) throws JSONException {
        List<ITarget> infos = new ArrayList<ITarget>();
        JSONObject object = new JSONObject(json);
        boolean succeed = object.optBoolean("succeed", false);
        int code = object.optInt("code", -1);
        if (succeed) {
            JSONArray jsonArray = object.getJSONObject("message").getJSONArray("data");
            JSONObject item = null;
            ContactsAdapter info;
            ContactsInfo contactsInfo ;
            for(int i =0 ;i<jsonArray.length();i++){
                item = jsonArray.getJSONObject(i);
                String name = item.getString("name");
                String phoneNumber = item.getString("phoneNumber");
                contactsInfo = new ContactsInfo(name,phoneNumber);
                info = new ContactsAdapter(contactsInfo);
                infos.add(info);
            }
        } else {
            throw new RuntimeException(code+"");
        }
        return infos;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
