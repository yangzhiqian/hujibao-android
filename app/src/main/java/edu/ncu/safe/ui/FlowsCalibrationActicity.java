package edu.ncu.safe.ui;

import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import edu.ncu.safe.R;
import edu.ncu.safe.base.activity.BackAppCompatActivity;
import edu.ncu.safe.mvp.presenter.FlowsCalibrationPresenter;
import edu.ncu.safe.mvp.view.FlowsCalibrationMvpView;
import edu.ncu.safe.util.FlowsFormartUtil;
import edu.ncu.safe.util.MyDialogHelper;

public class FlowsCalibrationActicity extends BackAppCompatActivity implements OnClickListener, FlowsCalibrationMvpView {
    private static final String[] MESSAGES = {"cxll", "CXLL", "1081"};
    private static final String[] NUMBERS = {"10086", "10010", "10001"};

    private LinearLayout ll_messageCelibration;
    private EditText et_flowsRemian;
    private EditText et_flowsTotal;

    private FlowsCalibrationPresenter presenter;

    @Override
    protected CharSequence initTitle() {
        return getResources().getString(R.string.title_flows_calibration);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_flowscalibration;
    }

    /**
     * 初始化控件
     */
    protected void initViews() {
        ll_messageCelibration = (LinearLayout) this
                .findViewById(R.id.ll_messagecelibration);
        et_flowsRemian = (EditText) this.findViewById(R.id.et_flowsremain);
        et_flowsTotal = (EditText) this.findViewById(R.id.et_flowstotal);

        ll_messageCelibration.setOnClickListener(this);
        et_flowsRemian.setOnClickListener(this);
        et_flowsTotal.setOnClickListener(this);
    }

    @Override
    protected void initCreate() {
        presenter = new FlowsCalibrationPresenter(this, getApplicationContext());
        presenter.init();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        presenter.refresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_messagecelibration:
                showSendMessageSelectorDialog();
                break;
            case R.id.et_flowsremain:
                showInputRemainDialog();
                break;
            case R.id.et_flowstotal:
                showInputTotalDialog();
                break;
        }
    }

    /**
     * 用于调用显示选择运营商的对话框
     */
    private void showSendMessageSelectorDialog() {
        MyDialogHelper.showSingleChoiceDialog(this,
                getString(R.string.dialog_title_choose_operator),
                R.array.company,
                new MyDialogHelper.ChoiceCallBack() {
                    @Override
                    public void onChoiceSucceed(int... choices) {
                        // 发送查询流量的消息
                        Uri uri = Uri.parse("smsto:" + NUMBERS[choices[0]]);
                        Intent intent = new Intent(Intent.ACTION_SENDTO,
                                uri);
                        intent.putExtra("sms_body", MESSAGES[choices[0]]);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancled(int... beforCancledChoices) {
                        makeToast(getString(R.string.toast_user_cancled));
                    }
                });

    }

    /**
     * 用于调用显示输入剩余流量的对话框
     */
    private void showInputRemainDialog() {
        MyDialogHelper.showSingleInputDialog(this,
                getString(R.string.dialog_title_input_remaind_flows),
                getString(R.string.input_hint_as_MB),
                InputType.TYPE_CLASS_NUMBER,
                new MyDialogHelper.InputCallBack() {
                    @Override
                    public void inputSucceed(String input) {
                        try {
                            presenter.resetFlowsRemain(input);
                        } catch (Exception e) {
                            inputError(getString(R.string.toast_error_invalid_input));
                        }
                    }

                    @Override
                    public void inputError(String error) {
                        makeToast(error);
                    }
                });
    }

    /**
     * 用于调用显示输入总流量的对话框
     */
    private void showInputTotalDialog() {
        MyDialogHelper.showSingleInputDialog(this,
                getString(R.string.dialog_title_total_flows),
                getString(R.string.input_hint_as_MB),
                InputType.TYPE_CLASS_NUMBER,
                new MyDialogHelper.InputCallBack() {
                    @Override
                    public void inputSucceed(String input) {
                        try {
                            presenter.resetTotalFlows(input);
                        } catch (Exception e) {
                            inputError(getString(R.string.toast_error_invalid_input));
                        }
                    }

                    @Override
                    public void inputError(String error) {
                        makeToast(error);
                    }
                });
    }

    @Override
    public void onCurrentMonthFlowsRemainedGet(long flows) {
        et_flowsRemian.setText(FlowsFormartUtil.toMBFormat(flows));
    }

    @Override
    public void onTotalFlowsGet(long flows) {
        et_flowsTotal.setText(FlowsFormartUtil.toMBFormat(flows));
    }
}
