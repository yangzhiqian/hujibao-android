package edu.ncu.safe.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.engine.NetDataOperator;
import edu.ncu.safe.myadapter.BackupBaseFragment;
import edu.ncu.safe.ui.fragment.ContactsBackupFragment;
import edu.ncu.safe.ui.fragment.MessageBackupFragment;
import edu.ncu.safe.ui.fragment.PictureBackupFragment;

/**
 * Created by Mr_Yang on 2016/6/1.
 * 2016/10/27进行大修改
 */
public class BackUpsActivity  extends MyAppCompatActivity implements Toolbar.OnMenuItemClickListener {
    private Fragment[] fragments = new Fragment[3];
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backups);
        initToolBar(getResources().getString(R.string.title_data_backup));
        toolbar.setOnMenuItemClickListener(this);
        fragmentChanged(0);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_backup, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void fragmentChanged(int position) {
        // 开启一个Fragment事务
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        if (fragments[position] == null) {
            fragments[position] = BackupBaseFragment.BackUpFragmentFactory.createFragment(NetDataOperator.BACKUP_TYPE.values()[position]);
            transaction.add(R.id.fl_container, fragments[position]);
        } else {
            transaction.show(fragments[position]);
        }
        transaction.commit();
    }
    private void hideFragments( FragmentTransaction transaction){
        for (Fragment fragment : fragments) {
            if(fragment!=null){
                transaction.hide(fragment);
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getTitle().equals(getToolBarTitle())){
            return false;
        }
        switch (item.getItemId()){
            case R.id.menu_photo:
                fragmentChanged(0);
                break;
            case R.id.menu_sms:
                fragmentChanged(1);
                break;
            case R.id.menu_contact:
                fragmentChanged(2);
                break;
        }
        setToolBarTitle(item.getTitle());
        return false;
    }
}
