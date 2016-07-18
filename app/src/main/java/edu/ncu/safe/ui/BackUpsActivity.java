package edu.ncu.safe.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import edu.ncu.safe.R;
import edu.ncu.safe.myadapter.BackupBaseFragment;
import edu.ncu.safe.myadapter.MyAppCompatActivity;
import edu.ncu.safe.ui.fragment.ContactsBackupFragment;
import edu.ncu.safe.ui.fragment.MessageBackupFragment;
import edu.ncu.safe.ui.fragment.PictureBackupFragment;

/**
 * Created by Mr_Yang on 2016/6/1.
 */
public class BackUpsActivity  extends MyAppCompatActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener {
    private static final int MESSAGEFRAGMENT = 0;
    private static final int PICTUREFRAGMENT = 1;
    private static final int CONTACTSFRAGMENT = 2;

    private static final String[] TITLE = {"短信","照片","联系人"};
    private static final String[] TYPENAME = {"-本地","-网络","-未备份","-未还原"};

//    private ImageView iv_back;
//    private TextView tv_selectAll;
//    private TextView tv_title;
//    private TextView tv_multChoices;

    private AbsoluteLayout al_content;
    private ImageButton ib_message;
    private ImageButton ib_picture;
    private ImageButton ib_contacts;
    private ImageButton ib_more;

    private boolean isShowed = false;
    private Animation appear;
    private Animation disappear;

    private BackupBaseFragment messageBackupFragment;
    private BackupBaseFragment pictureBackupFragment;
    private BackupBaseFragment contactsBackupFragment;
    private BackupBaseFragment currentFragment;
    private int currentFragmentType = PICTUREFRAGMENT;

//    private PopupWindow popupWindow;
//    private View popupView ;
//    private LinearLayout ll_local;
//    private LinearLayout ll_cloud;
//    private LinearLayout ll_backup;
//    private LinearLayout ll_recovery;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backups);
        initToolBar(getResources().getString(R.string.title_data_backup));
//        iv_back = (ImageView) this.findViewById(R.id.back);
//        tv_selectAll = (TextView) findViewById(R.id.tv_selectall);
//        tv_title = (TextView) this.findViewById(R.id.tv_title);
//        tv_multChoices = (TextView) this.findViewById(R.id.tv_mulitchoices);

        al_content = (AbsoluteLayout) this.findViewById(R.id.al_content);
        ib_more = (ImageButton) this.findViewById(R.id.ib_more);
        ib_message = (ImageButton) this.findViewById(R.id.ib_message);
        ib_picture = (ImageButton) this.findViewById(R.id.ib_picture);
        ib_contacts = (ImageButton) this.findViewById(R.id.ib_contacts);

        appear = AnimationUtils.loadAnimation(this,R.anim.backupmenuappearrotate);
        disappear =  AnimationUtils.loadAnimation(this,R.anim.backupmenudisappearrotate);

//        iv_back.setOnClickListener(this);
//        tv_selectAll.setOnClickListener(this);
//        tv_multChoices.setOnClickListener(this);
//        tv_title.setOnClickListener(this);

        ib_more.setOnClickListener(this);
        ib_message.setOnClickListener(this);
        ib_picture.setOnClickListener(this);
        ib_contacts.setOnClickListener(this);
        toolbar.setOnMenuItemClickListener(this);
        initPopup();
        fragmentChanged(PICTUREFRAGMENT);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_backup, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setTitle(){
        ((TextView)findViewById(R.id.tv_title)).setText(TITLE[currentFragmentType] + TYPENAME[currentFragment.getCurrentShowType()]);
    }
    private void initPopup(){
//        popupView = View.inflate(this,R.layout.popupwindow_backup_type, null);
//        ll_local = (LinearLayout) popupView.findViewById(R.id.ll_local);
//        ll_cloud = (LinearLayout) popupView.findViewById(R.id.ll_cloud);
//        ll_backup = (LinearLayout) popupView.findViewById(R.id.ll_backup);
//        ll_recovery = (LinearLayout) popupView.findViewById(R.id.ll_recovery);
//
//        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        //popupWindow设置animation一定要在show之前
//        popupWindow.setTouchable(true);
//        popupWindow.setFocusable(true);
//        popupWindow.setOutsideTouchable(true);
//        //一定要设置背景，否则无法自动消失
//        Drawable background = getResources().getDrawable(
//                R.drawable.popupbgtopmiddle);
//        popupWindow.setBackgroundDrawable(background);
//
//        ll_local.setOnClickListener(this);
//        ll_cloud.setOnClickListener(this);
//        ll_backup.setOnClickListener(this);
//        ll_recovery.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
//            case R.id.back:
//                this.finish();
//                overridePendingTransition(R.anim.activit3dtoright_in, R.anim.activit3dtoright_out);
//                break;
//            case R.id.tv_selectall:
//                if(tv_selectAll.getText().equals("全选")){
//                    tv_selectAll.setText("全不选");
//                    currentFragment.selectAll();
//                }else{
//                    tv_selectAll.setText("全选");
//                    currentFragment.selectNone();
//                }
//                break;
//            case R.id.tv_title:
//                tv_title.measure(0, 0);
//                popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//                popupWindow.showAsDropDown(tv_title,
//                        (popupView.getMeasuredWidth()-tv_title.getMeasuredWidth())/-2, -10);
//                break;
//            case R.id.tv_mulitchoices:
//                if(tv_selectAll.isShown()){
//                    tv_multChoices.setText("多选");
//                    iv_back.setVisibility(View.VISIBLE);
//                    tv_selectAll.setVisibility(View.GONE);
//                }else{
//                    tv_multChoices.setText("取消");
//                    tv_selectAll.setVisibility(View.VISIBLE);
//                    tv_selectAll.setText("全选");
//                    iv_back.setVisibility(View.GONE);
//                }
//                currentFragment.showMultiChoice(!currentFragment.isShowMultiChoice());
//                break;
            case R.id.ib_more:
                if(isShowed){
                    al_content.startAnimation(disappear);
                }else{
                    al_content.startAnimation(appear);
                }
                isShowed = !isShowed;
                break;
            case R.id.ib_message:
            case R.id.ib_picture:
            case R.id.ib_contacts:
//                if(tv_selectAll.isShown()){
//                    tv_multChoices.setText("多选");
//                    iv_back.setVisibility(View.VISIBLE);
//                    tv_selectAll.setVisibility(View.GONE);
//                    currentFragment.showMultiChoice(false);
//                }
                fragmentChanged(id==R.id.ib_message?MESSAGEFRAGMENT:
                        id==R.id.ib_picture?PICTUREFRAGMENT:CONTACTSFRAGMENT);
                break;
//            case R.id.ll_local:
//                currentFragment.showLocal();
//                popupWindow.dismiss();
//                tv_title.setText(TITLE[currentFragmentType] + TYPENAME[currentFragment.getCurrentShowType()]);
//                break;
//            case R.id.ll_cloud:
//                currentFragment.showCloud();
//                popupWindow.dismiss();
//                tv_title.setText(TITLE[currentFragmentType] + TYPENAME[currentFragment.getCurrentShowType()]);
//                break;
//            case R.id.ll_backup:
//                currentFragment.showBackup();
//                popupWindow.dismiss();
//                tv_title.setText(TITLE[currentFragmentType] + TYPENAME[currentFragment.getCurrentShowType()]);
//                break;
//            case R.id.ll_recovery:
//                currentFragment.showRecovery();
//                popupWindow.dismiss();
//                tv_title.setText(TITLE[currentFragmentType]+TYPENAME[currentFragment.getCurrentShowType()]);
//                break;
        }
    }

    private void fragmentChanged(int position) {
        // 开启一个Fragment事务
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (position) {
            case MESSAGEFRAGMENT:
                if (messageBackupFragment == null) {
                    messageBackupFragment = MessageBackupFragment.newInstance(BackupBaseFragment.TYPE_MESSAGE);
                    transaction.add(R.id.fl_container, messageBackupFragment);
                } else {
                    transaction.show(messageBackupFragment);
                }
                transaction.commit();
                currentFragment = messageBackupFragment;
                currentFragmentType = MESSAGEFRAGMENT;
                break;
            case PICTUREFRAGMENT:
                if (pictureBackupFragment == null) {
                    pictureBackupFragment = PictureBackupFragment.newInstance(BackupBaseFragment.TYPE_IMG);
                    transaction.add(R.id.fl_container, pictureBackupFragment);
                } else {
                    transaction.show(pictureBackupFragment);
                }
                transaction.commit();
                currentFragment = pictureBackupFragment;
                currentFragmentType = PICTUREFRAGMENT;
                break;
            case CONTACTSFRAGMENT:
                if (contactsBackupFragment == null) {
                    contactsBackupFragment = ContactsBackupFragment.newInstance(BackupBaseFragment.TYPE_CONTACT);
                    transaction.add(R.id.fl_container, contactsBackupFragment);
                } else {
                    transaction.show(contactsBackupFragment);
                }
                transaction.commit();
                currentFragment = contactsBackupFragment;
                currentFragmentType = CONTACTSFRAGMENT;
                break;
        }
        setTitle();
    }
    private void hideFragments( FragmentTransaction transaction){
        if(messageBackupFragment!=null){
            transaction.hide(messageBackupFragment);
        }
        if(pictureBackupFragment!=null){
            transaction.hide(pictureBackupFragment);
        }
        if(contactsBackupFragment!=null){
            transaction.hide(contactsBackupFragment);
        }

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_local:
                currentFragment.showLocal();
                setTitle();
                break;
            case R.id.menu_net:
                currentFragment.showCloud();
                setTitle();
                break;
//            case R.id.menu_backup:
//                currentFragment.showBackup();
//                setTitle();
//                break;
            case R.id.menu_recovery:
                currentFragment.showRecovery();
                setTitle();
                break;
        }
        return false;
    }
}
