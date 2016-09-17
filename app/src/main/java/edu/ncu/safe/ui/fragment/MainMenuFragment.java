package edu.ncu.safe.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.ncu.safe.MyApplication;
import edu.ncu.safe.R;
import edu.ncu.safe.adapter.MainMenuRecyclerViewAdapter;
import edu.ncu.safe.customerview.CircleImageView;
import edu.ncu.safe.customerview.DividerItemDecoration;
import edu.ncu.safe.customerview.MyProgressBar;
import edu.ncu.safe.domain.MainMenuInfo;
import edu.ncu.safe.domain.User;
import edu.ncu.safe.mvp.presenter.MainMenuPresenter;
import edu.ncu.safe.mvp.view.MainMenuMvpView;
import edu.ncu.safe.ui.LoginActivity;
import edu.ncu.safe.ui.MainActivity;
import edu.ncu.safe.ui.ShareActivity;
import edu.ncu.safe.util.FlowsFormartUtil;

/**
 * Created by Mr_Yang on 2016/7/12.
 */
public class MainMenuFragment extends Fragment implements MainMenuRecyclerViewAdapter.OnItemClickListener, View.OnClickListener,MainMenuMvpView {
    private View view;
    private ImageView iv_icon;
    private TextView tv_name;
    private MyProgressBar mpb_memory;
    private RecyclerView rv;
    private Button bt_login;
    private MainMenuRecyclerViewAdapter adapter;

    private User user;
    private MainActivity activity;

    private MainMenuPresenter  presenter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.besidelayout_mainmenu, null);
            initViews();
            rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            adapter = new MainMenuRecyclerViewAdapter(getContext());
            //设置Item增加、移除动画
            rv.setAdapter(adapter);
            rv.setItemAnimator(new DefaultItemAnimator());
            //添加分割线
            rv.addItemDecoration(new DividerItemDecoration(
                    getActivity(), DividerItemDecoration.VERTICAL_LIST));
            adapter.setOnItemClickListener(this);
            bt_login.setOnClickListener(this);

            presenter = new MainMenuPresenter(this);
            presenter.init();
        }
        return view;
    }

    private void initViews() {
        iv_icon = (CircleImageView) view.findViewById(R.id.iv_icon);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        mpb_memory = (MyProgressBar) view.findViewById(R.id.mpb_memory);
        rv = (RecyclerView) view.findViewById(R.id.rv_mainmenu);
        bt_login = (Button) view.findViewById(R.id.bt_login);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.reflesh();
    }


    @Override
    public void onClick(View v) {
        if (user == null) {
            activity.startActivityForResult(new Intent(activity, LoginActivity.class), 1);
            activity.overridePendingTransition(R.anim.activit3dtoleft_in,
                    R.anim.activit3dtoleft_out);
        } else {
            SharedPreferences sp = MyApplication.getSharedPreferences();
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(MyApplication.SP_STRING_USER, "");
            edit.apply();
            presenter.logOut(user);
        }
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode + requestCode == 0) {
            return;
        }
        if (data == null) {
            return;
        }
        user = (User) data.getExtras().getSerializable("user");
        if (user == null) {
            return;
        }
        //保存到sp中
        SharedPreferences sp = MyApplication.getSharedPreferences();
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(MyApplication.SP_STRING_USER, user.toJson());
        edit.commit();
        presenter.init();
    }

    @Override
    public void itemClicked(View view, MainMenuInfo data, int position) {
        switch (position) {
            case 0:
                activity.startActivity(new Intent(activity, ShareActivity.class));
                activity.overridePendingTransition(R.anim.activit3dtoleft_in,
                        R.anim.activit3dtoleft_out);
                break;
        }
    }

    @Override
    public void onLogIn(User user) {
        this.user = user;
        if (user == null) {
            tv_name.setText(getContext().getString(R.string.menu_default_title));
            mpb_memory.setPercentSlow(0);
            mpb_memory.setTitle("云空间 0M/0M");
            iv_icon.setImageResource(R.drawable.appicon);//设置默认图标
            bt_login.setText(getResources().getString(R.string.button_log_in));
        }else{
            tv_name.setText(user.getName());
            mpb_memory.setPercentSlow((float) (user.getUsed() * 100.0 / user.getTotal()));
            mpb_memory.setTitle("云空间 " + FlowsFormartUtil.toMBFormat(user.getUsed()) + "M/" + FlowsFormartUtil.toMBFormat(user.getTotal()) + "M");

            presenter.loadAvator(user.getIconUrl(),user.getToken());
            bt_login.setText(getResources().getString(R.string.button_log_out));
        }
    }

    @Override
    public void onCheckStateOnLine(User user) {

    }

    @Override
    public void onCheckStateOffline(String description) {

    }

    @Override
    public void onLogOut(User lastUser,String message) {
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemsGet(ArrayList<MainMenuInfo> infos) {
        adapter.setList(infos);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAvatorGet(Bitmap bitmap) {
        iv_icon.setImageBitmap(bitmap);
    }

    @Override
    public void onAvatorGetError(String message) {
        iv_icon.setImageResource(R.drawable.appicon);//设置默认图标
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestory();
    }
}
