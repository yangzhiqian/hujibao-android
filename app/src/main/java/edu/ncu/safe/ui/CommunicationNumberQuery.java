package edu.ncu.safe.ui;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.CommunicationLVNumberPlaceAdapter;
import edu.ncu.safe.db.dao.CommunicationDatabase;
import edu.ncu.safe.engine.LoadPhoneNumberOwnerPalce;
import edu.ncu.safe.engine.LoadPhoneNumberOwnerPalce.NumberPlaceInfo;
import edu.ncu.safe.engine.LoadPhoneNumberOwnerPalce.OnOwnerPlaceObtainedListener;

/**
 * Created by Mr_Yang on 2016/5/16.
 */
public class CommunicationNumberQuery extends Activity implements OnOwnerPlaceObtainedListener, View.OnClickListener {

    private static final int FAIL = 1;
    private static final int SUCCESS = 2;
    private ImageView iv_back;
    private EditText et_number;
    private ImageButton ib_to;
    private ListView lv_queryRecoder;
    private CommunicationDatabase db;

    private CommunicationLVNumberPlaceAdapter adapter;
    private List<NumberPlaceInfo> infos;

    private LoadPhoneNumberOwnerPalce place;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    NumberPlaceInfo info = (NumberPlaceInfo) msg.obj;
                    makeToast(info.toString());
                    db.insertOneNumberPlace(info);
                    infos.add(0, info);
                    adapter.notifyDataSetChanged();
                    break;
                case FAIL:
                    makeToast((String) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonenumberplace);
        //初始化控件
        iv_back = (ImageView) this.findViewById(R.id.back);
        et_number = (EditText) findViewById(R.id.et_number);
        ib_to = (ImageButton) findViewById(R.id.ib_to);
        lv_queryRecoder = (ListView) findViewById(R.id.lv_queryrecoder);
        //初始化查询器
        place = new LoadPhoneNumberOwnerPalce(this);
        place.setOnOwnerPlaceObtainedListener(this);
        //设置点击事件
        iv_back.setOnClickListener(this);
        ib_to.setOnClickListener(this);
        //设置lv数据
        db = new CommunicationDatabase(this);
        infos = db.queryAllNumberPlaceInfosFromDB();
        adapter = new CommunicationLVNumberPlaceAdapter(infos, this);
        //设置适配器
        lv_queryRecoder.setAdapter(adapter);
    }

    @Override
    public void OnOwnerPalceObtained(NumberPlaceInfo info) {
        dialog.dismiss();
        Message message = new Message();
        if (info == null) {
            message.what = FAIL;
            message.obj = "抱歉，无法查询到该号码信息";
            handler.sendMessage(message);
        } else {
            message.what = SUCCESS;
            message.obj = info;
            handler.sendMessage(message);
        }
    }

    private ProgressDialog dialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.activit3dtoright_in, R.anim.activit3dtoright_out);
                break;
            case R.id.ib_to:
                place.setNumber(et_number.getText().toString().trim());
                place.request();
                dialog = new ProgressDialog(this);
                dialog.setCancelable(false);
                dialog.show();
                break;
        }
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
