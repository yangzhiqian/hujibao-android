package edu.ncu.safe.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import edu.ncu.safe.R;
import edu.ncu.safe.customerview.MyDialog;
import edu.ncu.safe.adapter.CommunicationLVInterceptionSetAdapter;
import edu.ncu.safe.domain.InterceptionSetInfo;
import edu.ncu.safe.engine.InterceptionSetOperation;

/**
 * Created by Mr_Yang on 2016/5/17.
 */
public class CommunicationInterceptionSet extends MyAppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ListView lv_interceptionMode;
    private LinearLayout ll_nightMode;
    private TextView tv_nightMode;
    private CheckBox cb_nightMode;

    private CommunicationLVInterceptionSetAdapter adapter;
    private InterceptionSetOperation operation;
    private InterceptionSetInfo setInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communicationinterceptionset);
        initToolBar(getResources().getString(R.string.title_communication_protector_set));
        //初始化
        lv_interceptionMode = (ListView) this.findViewById(R.id.lv_interceptionmode);
        ll_nightMode = (LinearLayout) this.findViewById(R.id.ll_nightmode);
        tv_nightMode = (TextView) this.findViewById(R.id.tv_nightmode);
        cb_nightMode = (CheckBox) this.findViewById(R.id.cb_nightmode);

        operation = new InterceptionSetOperation(this);
        setInfo = operation.getInterceptionSetInfo();//获取配置信息
        adapter = new CommunicationLVInterceptionSetAdapter(setInfo.getMode(), this);
        lv_interceptionMode.setAdapter(adapter);

        //设置点击事件
        ll_nightMode.setOnClickListener(this);
        lv_interceptionMode.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
       init();
    }
    private void init(){
        tv_nightMode.setText(setInfo.getNote());
        cb_nightMode.setChecked(setInfo.isNightMode());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_nightmode:
                if (setInfo.isNightMode()) {
                    setInfo.setIsNightMode(false);
                    operation.commitInterceptionSetInfo(setInfo);
                    makeToast(getResources().getString(R.string.toast_closed_night_mode));
                    cb_nightMode.setChecked(false);
                    return;
                }
                nightModeSet();
                break;
        }
    }

    private void nightModeSet() {
        View contentView = View.inflate(this, R.layout.dialog_nighmode, null);
        final EditText et_beginH = (EditText) contentView.findViewById(R.id.et_beginhour);
        final EditText et_beginM = (EditText) contentView.findViewById(R.id.et_beginminute);
        final EditText et_endH = (EditText) contentView.findViewById(R.id.et_endhour);
        final EditText et_endM = (EditText) contentView.findViewById(R.id.et_endminute);

        final CheckBox cb_1 = (CheckBox) contentView.findViewById(R.id.cb_1);
        final CheckBox cb_2 = (CheckBox) contentView.findViewById(R.id.cb_2);
        final CheckBox cb_3 = (CheckBox) contentView.findViewById(R.id.cb_3);
        final CheckBox cb_4 = (CheckBox) contentView.findViewById(R.id.cb_4);
        final CheckBox cb_5 = (CheckBox) contentView.findViewById(R.id.cb_5);
        final CheckBox cb_6 = (CheckBox) contentView.findViewById(R.id.cb_6);
        final CheckBox cb_7 = (CheckBox) contentView.findViewById(R.id.cb_7);

        final RadioGroup rg_mode = (RadioGroup) contentView.findViewById(R.id.rg_mode);
        final RadioButton[] rbs = {(RadioButton) contentView.findViewById(R.id.rb_1),
                (RadioButton) contentView.findViewById(R.id.rb_2),
                (RadioButton) contentView.findViewById(R.id.rb_3),
                (RadioButton) contentView.findViewById(R.id.rb_4),
                (RadioButton) contentView.findViewById(R.id.rb_5),};

        et_beginH.setText(setInfo.getBeginHour()+"");
        et_beginM.setText(setInfo.getBeginMinute()+"");
        et_endH.setText(setInfo.getEndHour()+"");
        et_endM.setText(setInfo.getEndMinute() + "");

        cb_1.setChecked(setInfo.getWeekEnable()[1]);
        cb_2.setChecked(setInfo.getWeekEnable()[2]);
        cb_3.setChecked(setInfo.getWeekEnable()[3]);
        cb_4.setChecked(setInfo.getWeekEnable()[4]);
        cb_5.setChecked(setInfo.getWeekEnable()[5]);
        cb_6.setChecked(setInfo.getWeekEnable()[6]);
        cb_7.setChecked(setInfo.getWeekEnable()[0]);

        rbs[setInfo.getNightMode()].setChecked(true);

        final MyDialog dialog = new MyDialog(this);
        dialog.setTitle(getResources().getString(R.string.dialog_title_night_mode));
        dialog.setMessageView(contentView);
        dialog.setCancelable(false);

        dialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int bh, bm, eh, em;
//                try {
                bh = Integer.parseInt(et_beginH.getText().toString().trim());
                bm = Integer.parseInt(et_beginM.getText().toString().trim());
                eh = Integer.parseInt(et_endH.getText().toString().trim());
                em = Integer.parseInt(et_endM.getText().toString().trim());
                if (bh > 23 || eh > 23 || bm > 59 || em > 59) {
                    makeToast(getResources().getString(R.string.toast_error_time_format_error));
                    return;
                }
                boolean[] weekEnable = {cb_7.isChecked(),
                        cb_1.isChecked(),
                        cb_2.isChecked(),
                        cb_3.isChecked(),
                        cb_4.isChecked(),
                        cb_5.isChecked(),
                        cb_6.isChecked()};
                int nightMode = 3;
                for(int i=0;i<rbs.length;i++){
                    if(rbs[i].isChecked()){
                        nightMode = i;
                    }
                }
                InterceptionSetInfo info = new InterceptionSetInfo(setInfo.getMode(),true,bh,bm,eh,em,weekEnable,nightMode);
                operation.commitInterceptionSetInfo(info);
                setInfo = info;
                makeToast(getResources().getString(R.string.toast_opened_night_mode));
                init();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        adapter.setItemSelected(view, position);
        setInfo.setMode(position);
        operation.commitInterceptionSetInfo(setInfo);
    }

}
