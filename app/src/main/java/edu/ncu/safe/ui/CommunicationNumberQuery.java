package edu.ncu.safe.ui;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.CommunicationLVNumberPlaceAdapter;
import edu.ncu.safe.db.dao.CommunicationDatabase;
import edu.ncu.safe.engine.LoadPhoneNumberOwnerPalce;
import edu.ncu.safe.engine.LoadPhoneNumberOwnerPalce.NumberPlaceInfo;
import edu.ncu.safe.engine.LoadPhoneNumberOwnerPalce.OnOwnerPlaceObtainedListener;
import edu.ncu.safe.myadapter.MyAppCompatActivity;

/**
 * Created by Mr_Yang on 2016/5/16.
 */
public class CommunicationNumberQuery extends MyAppCompatActivity implements OnOwnerPlaceObtainedListener, View.OnClickListener {

    private static final int FAIL = 1;
    private static final int SUCCESS = 2;
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
        initToolBar(getResources().getString(R.string.title_communication_protector_number_query));
        //初始化控件
        et_number = (EditText) findViewById(R.id.et_number);
        ib_to = (ImageButton) findViewById(R.id.ib_to);
        lv_queryRecoder = (ListView) findViewById(R.id.lv_queryrecoder);
        //初始化查询器
        place = new LoadPhoneNumberOwnerPalce(this);
        place.setOnOwnerPlaceObtainedListener(this);
        //设置点击事件
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
            message.obj = getResources().getString(R.string.toast_error_can_not_find_number_place);
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
