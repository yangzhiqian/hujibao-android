package edu.ncu.safe.ui.fragment;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.domain.SmsInfo;
import edu.ncu.safe.domainadapter.ITarget;
import edu.ncu.safe.domainadapter.MessageAdapter;
import edu.ncu.safe.engine.BackUpDataOperator;
import edu.ncu.safe.engine.NetDataOperator.BACKUP_TYPE;
import edu.ncu.safe.engine.PhoneSmsCloudOperator;
import edu.ncu.safe.engine.PhoneSmsOperator;
import edu.ncu.safe.myadapter.BackupBaseFragment;
import edu.ncu.safe.util.ContactUtil;

/**
 * Created by Mr_Yang on 2016/6/1.
 */
public class MessageBackupFragment extends BackupBaseFragment {
    protected PhoneSmsOperator phoneSmsOperator;
    protected PhoneSmsCloudOperator phoneSmsCloudOperator;

   public static MessageBackupFragment newInstance(BACKUP_TYPE type) {
        MessageBackupFragment f = new MessageBackupFragment();
        f.type = type;
        return f;
    }
    @Override
    public void init() {
        phoneSmsOperator = new PhoneSmsOperator(getActivity());
        phoneSmsCloudOperator = new PhoneSmsCloudOperator(getActivity());
    }
    @Override
    protected void loadLocalInfos() {
        List<ITarget> infos = new ArrayList<ITarget>();
        List<SmsInfo> smsInfos = phoneSmsOperator.getSms();
        for(SmsInfo info:smsInfos){
           infos.add(new MessageAdapter(info));
        }
        onLocalInfosLoaded(infos);
    }

    @Override
    protected void loadCloudInfos(final int beginIndex, final int endIndex) {
        phoneSmsCloudOperator.loadCloudDatas(beginIndex, endIndex - beginIndex, new BackUpDataOperator.OnLoadDatasResponseListener<MessageAdapter>() {
            @Override
            public void onFailure(String message) {
                makeToast(message);
            }

            @Override
            public void onDatasGet(List datas, int requestSize) {
                onCloudInfosLoaded(datas,requestSize>datas.size());
            }
        });
    }

    @Override
    protected boolean isSameInfo(ITarget target1, ITarget target2) {
        SmsInfo t1 = (SmsInfo) target1;
        SmsInfo t2 = (SmsInfo) target2;
        if(t1.getAddress().equals(t2.getAddress()) && t1.getDate()==t2.getDate()){
            return true;
        }
        return false;
    }

    @Override
    protected View createShowLocalPopupWindowContentView(View parent, final int position,final ITarget info) {
        LinearLayout layout = getPopupWindowLayout();
        TextView tv_1 = getPopupWindowTextView("备份");
        layout.addView(tv_1);
        layout.addView(getPopupWindowDivider());

        TextView tv_2 = getPopupWindowTextView("回复短信");
        layout.addView(tv_2);
        layout.addView(getPopupWindowDivider());

        TextView tv_3 = getPopupWindowTextView("回拨电话");
        layout.addView(tv_3);

        tv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmissPopupWindow();
                phoneSmsCloudOperator.storeDataToCloud((SmsInfo) info, new BackUpDataOperator.OnStoreDatasResponseListener<SmsInfo>() {
                    @Override
                    public void onError(List<SmsInfo> datas, String message) {
                        makeToast(message);
                    }

                    @Override
                    public void onFailure(SmsInfo data, String message) {
                        makeToast(message);
                    }

                    @Override
                    public void onSucceed(SmsInfo data, String message) {
                        makeToast(message);
                    }

                    @Override
                    public void onProgressUpdated(SmsInfo data, int progress) {

                    }
                });
            }
        });
        tv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmissPopupWindow();
                ContactUtil.sendMessageTo(getActivity(),((SmsInfo)info).getAddress());
            }
        });
        tv_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmissPopupWindow();
                ContactUtil.callTo(getActivity(),((SmsInfo)info).getAddress());
            }
        });
        return layout;
    }

    @Override
    protected View createShowCloudPopupWindowContentView(View parent,final int position, final ITarget info) {
        LinearLayout layout = getPopupWindowLayout();
        TextView tv_1 = getPopupWindowTextView("恢复到短信");
        layout.addView(tv_1);
        layout.addView(getPopupWindowDivider());

        TextView tv_2 = getPopupWindowTextView("删除");
        layout.addView(tv_2);

        tv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dissmissPopupWindow();
                backToPhone(info);
            }
        });
        tv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmissPopupWindow();
                phoneSmsCloudOperator.deleteDataFromCloud(info.getID(), new BackUpDataOperator.OnDeleteDatasResponseListener() {
                    @Override
                    public void onFailure(int id, String errorMessage) {
                        makeToast(errorMessage);
                    }

                    @Override
                    public void onSucceed(int id, String message) {
                        makeToast(message);
                    }
                });
            }
        });
        return layout;
    }

    @Override
    protected View createShowRecoveryPopupWindowContentView(View parent, final int position,ITarget info) {
        return createShowCloudPopupWindowContentView(parent,position,info);
    }


    public void backToPhone(ITarget info) {
        if(phoneSmsOperator.recoveryOneSms((MessageAdapter) info)){
            makeToast("短信已经恢复到短信列表");
        }else{
            makeToast("恢复失败，可能信息已经存在！");
        }
    }
}
