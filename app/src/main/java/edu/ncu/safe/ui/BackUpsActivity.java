package edu.ncu.safe.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import edu.ncu.safe.R;
import edu.ncu.safe.base.activity.BackAppCompatActivity;
import edu.ncu.safe.base.fragment.BackupBaseFragment;
import edu.ncu.safe.engine.NetDataOperator;
/**
 * Created by Mr_Yang on 2016/6/1.
 * 2016/10/27进行大修改
 * 2017/5/9号大修改，改成使用FloatingActionMenu
 */
public class BackUpsActivity extends BackAppCompatActivity implements Toolbar.OnMenuItemClickListener, View.OnClickListener {
    private BackupBaseFragment[] fragments = new BackupBaseFragment[3];
    private BackupBaseFragment currentFragment;
    private FloatingActionButton actionButton;
    private SubActionButton pictureAction;
    private SubActionButton messageAction;
    private SubActionButton contactsAction;
    private FloatingActionMenu actionMenu;

    @Override
    protected int initLayout() {
        return R.layout.activity_backups;
    }

    @Override
    protected void initViews() {
        //添加FloatingActionButton
        ImageView action = new ImageView(this);
        action.setImageResource(R.drawable.add);
        actionButton = new FloatingActionButton.Builder(this)
                .setContentView(action)
                .build();
        //照片项
        ImageView itemPicture = new ImageView(this);
        itemPicture.setImageResource(R.drawable.pic);
        pictureAction = new SubActionButton.Builder(this).setContentView(itemPicture).build();
        //短信
        ImageView itemMessage = new ImageView(this);
        itemMessage.setImageResource(R.drawable.message);
        messageAction = new SubActionButton.Builder(this).setContentView(itemMessage).build();
        //联系人
        ImageView itemContacts = new ImageView(this);
        itemContacts.setImageResource(R.drawable.contacts);
        contactsAction = new SubActionButton.Builder(this).setContentView(itemContacts).build();

        actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(pictureAction)
                .addSubActionView(messageAction)
                .addSubActionView(contactsAction)
                .attachTo(actionButton)
                .build();
        //设置点击监听
        pictureAction.setOnClickListener(this);
        messageAction.setOnClickListener(this);
        contactsAction.setOnClickListener(this);

    }

    @Override
    protected CharSequence initTitle() {
        return getResources().getString(R.string.title_data_backup);
    }

    @Override
    protected void initCreate() {
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
            //显示的fragment还没有创建，创建
            fragments[position] = BackupBaseFragment.BackUpFragmentFactory.createFragment(NetDataOperator.BACKUP_TYPE.values()[position]);
            //添加到显示的容器中
            transaction.add(R.id.fl_container, fragments[position]);
        } else {
            //原来已经显示过了，直接显示
            transaction.show(fragments[position]);
        }
        transaction.commit();
        currentFragment = fragments[position];
        setToolBarTitle(currentFragment.getTitle());
    }

    private void hideFragments(FragmentTransaction transaction) {
        for (Fragment fragment : fragments) {
            if (fragment != null) {
                transaction.hide(fragment);
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_local:
                currentFragment.showLocal();
                setToolBarTitle(currentFragment.getTitle());
                break;
            case R.id.menu_cloud:
                currentFragment.showCloud();
                setToolBarTitle(currentFragment.getTitle());
                break;
            case R.id.menu_recovery:
                currentFragment.showRecovery();
                setToolBarTitle(currentFragment.getTitle());
                break;
            case R.id.menu_mulitchoice:
                currentFragment.showMulitChoice(!currentFragment.isShowingMulitChoice());
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if(view == pictureAction){
            currentFragment.showMulitChoice(false);
            fragmentChanged(0);
        }
        if(view == messageAction){
            currentFragment.showMulitChoice(false);
            fragmentChanged(1);
        }
        if(view == contactsAction){
            currentFragment.showMulitChoice(false);
            fragmentChanged(2);
        }

    }
}
