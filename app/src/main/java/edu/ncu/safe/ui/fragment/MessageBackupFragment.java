package edu.ncu.safe.ui.fragment;

import android.view.View;
import android.widget.AdapterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.adapter.BackupLVAdapter;
import edu.ncu.safe.domain.SmsInfo;
import edu.ncu.safe.domain.User;
import edu.ncu.safe.domainadapter.ITarget;
import edu.ncu.safe.domainadapter.MessageAdapter;
import edu.ncu.safe.engine.SmsService;
import edu.ncu.safe.myadapter.BackupBaseFragment;

/**
 * Created by Mr_Yang on 2016/6/1.
 */
public class MessageBackupFragment extends BackupBaseFragment {
    public MessageBackupFragment(User user,int type){
        super(user, type);
    }
    @Override
    public void init() {
        currentShowType = SHOWTYPE_LOCAL;
        loadLocalInfos();
    }
    @Override
    public List<ITarget> loadLocalInfos() {
        List<ITarget> infos = new ArrayList<ITarget>();
        List<SmsInfo> smsInfos = new SmsService(getContext()).getSms();
        for(SmsInfo info:smsInfos){
           infos.add(new MessageAdapter(info));
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
    public List<ITarget> parseToInfos(String json) throws JSONException {
        List<ITarget> infos = new ArrayList<ITarget>();
        JSONObject object = new JSONObject(json);
        boolean succeed = object.optBoolean("succeed", false);
        int code = object.optInt("code", -1);
        if (succeed) {
            JSONArray jsonArray = object.getJSONObject("message").getJSONArray("data");
            JSONObject item = null;
            MessageAdapter info;
            SmsInfo smsInfo ;
            for(int i =0 ;i<jsonArray.length();i++){
                item = jsonArray.getJSONObject(i);
                long date = item.getLong("date");
                String address = item.getString("address");
                String body = item.getString("body");
                int type = item.getInt("type");
                smsInfo = new SmsInfo(address,date,type,body);
                info = new MessageAdapter(smsInfo);
                infos.add(info);
            }
        } else {
            throw new RuntimeException(code+"");
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(adapter.isShowMultiChoice()){
            BackupLVAdapter.ViewHolder holder = (BackupLVAdapter.ViewHolder) view.getTag();
            holder.cb_check.setChecked(!holder.cb_check.isChecked());
            return;
        }
    }
}
