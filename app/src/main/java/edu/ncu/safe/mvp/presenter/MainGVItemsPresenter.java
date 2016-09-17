package edu.ncu.safe.mvp.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedList;
import java.util.List;

import edu.ncu.safe.MyApplication;
import edu.ncu.safe.R;
import edu.ncu.safe.domain.MainGVItemInfo;
import edu.ncu.safe.mvp.view.MainGVMvpView;
import edu.ncu.safe.ui.AppManagerAcitvity;
import edu.ncu.safe.ui.BackUpsActivity;
import edu.ncu.safe.ui.CommunicationProtectorActivity;
import edu.ncu.safe.ui.FlowsProtectorActivity;
import edu.ncu.safe.ui.PhoneLostProtectActivity;
import edu.ncu.safe.ui.SystemQuickenActivity;
import edu.ncu.safe.ui.fragment.MainFragment;
import edu.ncu.safe.util.MD5Encoding;
import edu.ncu.safe.util.MyDialogHelper;

/**
 * Created by Mr_Yang on 2016/9/17.
 */
public class MainGVItemsPresenter {
    private MainGVMvpView view;
    private List<MainGVItemInfo> mainGVItemInfos;
    private Context context;

    public MainGVItemsPresenter(MainGVMvpView view) {
        this.view = view;
        context = ((MainFragment)view).getContext();
    }

    public void getItems() {
        mainGVItemInfos = new LinkedList<MainGVItemInfo>();
        mainGVItemInfos.add(new MainGVItemInfo(R.drawable.phoneprotector, "手机防盗", context.getString(R.string.main_fragment_note_phone_lost_not_in_protecting), context.getResources().getColor(R.color.state_not_ok), "toPhoneLostInterceptor", PhoneLostProtectActivity.class));
        mainGVItemInfos.add(new MainGVItemInfo(R.drawable.gprsflows, "流量监控", context.getString(R.string.main_fragment_note_flows_not_in_protecting), context.getResources().getColor(R.color.state_not_ok), null, FlowsProtectorActivity.class));
        mainGVItemInfos.add(new MainGVItemInfo(R.drawable.databackup, "数据备份", "", context.getResources().getColor(R.color.state_not_ok), null, BackUpsActivity.class));
        mainGVItemInfos.add(new MainGVItemInfo(R.drawable.communication, "通讯卫士", "", context.getResources().getColor(R.color.state_not_ok), null, CommunicationProtectorActivity.class));
        mainGVItemInfos.add(new MainGVItemInfo(R.drawable.softwaremanager, "软件管理", "", context.getResources().getColor(R.color.state_not_ok), null, AppManagerAcitvity.class));
        mainGVItemInfos.add(new MainGVItemInfo(R.drawable.systemfaster, "手机加速", "", context.getResources().getColor(R.color.state_not_ok), null, SystemQuickenActivity.class));
        view.onMainGVItemsGet(mainGVItemInfos);
    }

    public void itemsUpdate() {
        SharedPreferences sp = MyApplication.getSharedPreferences();
        for (MainGVItemInfo info : mainGVItemInfos) {
            switch (info.getTitle()) {
                case "手机防盗":
                    boolean isInProtecting = sp.getBoolean(MyApplication.SP_BOOLEAN_IS_IN_PROTECTING, false);
                    if (isInProtecting) {
                        info.setNote(context.getString(R.string.main_fragment_note_phone_lost_in_protecting));
                        info.setColor(context.getResources().getColor(R.color.state_ok));
                    } else {
                        info.setNote(context.getString(R.string.main_fragment_note_phone_lost_not_in_protecting));
                        info.setColor(context.getResources().getColor(R.color.state_not_ok));
                    }
                    break;
                case "流量监控":
                    long flows = sp.getLong(MyApplication.SP_LONG_TOTAL_FLOWS, 0);
                    if (flows > 0) {
                        info.setNote(context.getString(R.string.main_fragment_note_flows_in_protecting));
                        info.setColor(context.getResources().getColor(R.color.state_ok));
                    } else {
                        info.setNote(context.getString(R.string.main_fragment_note_flows_not_in_protecting));
                        info.setColor(context.getResources().getColor(R.color.state_not_ok));
                    }
                    break;
            }
        }
        view.onMainGVItemsUpdate(mainGVItemInfos);
    }

    /**
     * 在进入手机防盗模块市进入的一个方法，用于验证密码
     */
    public void toPhoneLostInterceptor(){
        final SharedPreferences sp = MyApplication.getSharedPreferences();
        boolean hasSetPWD = sp.getBoolean(
                MyApplication.SP_BOOLEAN_HAS_PWD, false);
        if (hasSetPWD) {// 已经设置过密码
            MyDialogHelper.showInputPWDDialog(context, new MyDialogHelper.InputChecker() {
                public boolean checkPWDCorrect(String inputPWD){
                    String enterPwd = sp.getString(MyApplication.SP_STRING_PWD, "");
                    return enterPwd.equals(MD5Encoding.encoding(inputPWD));
                }
            }, new MyDialogHelper.InputCallBack() {
                @Override
                public void inputSucceed(String input) {
                    view.toAntherActivity(PhoneLostProtectActivity.class);
                }
                @Override
                public void inputError(String error) {

                }
            });
        } else {// 还未设置密码
            MyDialogHelper.showResetPWDDialog(context, new MyDialogHelper.InputChecker() {
                @Override
                public boolean checkInputFormatLegal(String input) {
                    return input.trim().length()>=4;
                }
            }, new MyDialogHelper.InputCallBack() {
                @Override
                public void inputSucceed(String input) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(MyApplication.SP_STRING_PWD, MD5Encoding.encoding(input));
                    editor.putBoolean(MyApplication.SP_BOOLEAN_HAS_PWD, true);
                    editor.apply();
                    // 进入界面
                    view.toAntherActivity(PhoneLostProtectActivity.class);
                }

                @Override
                public void inputError(String error) {

                }
            });
        }
    }
}
