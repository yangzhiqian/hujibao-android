package edu.ncu.safe.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.LinkedList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.MainGVAdapter;
import edu.ncu.safe.domain.MainGVItemInfo;
import edu.ncu.safe.mvp.presenter.MainGVItemsPresenter;
import edu.ncu.safe.mvp.view.MainGVMvpView;
import edu.ncu.safe.ui.MainActivity;

/**
 * Created by Mr_Yang on 2016/7/12.
 */
public class MainFragment extends Fragment implements MainGVMvpView, AdapterView.OnItemClickListener {
    private View view;
    private MainActivity activity;
    private GridView gv;// 主界面下方的六个选项
    private MainGVAdapter adapter;// gv的数据适配器
    private List<MainGVItemInfo> mainGVItemInfos = new LinkedList<MainGVItemInfo>();

    private MainGVItemsPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_main, null);
            gv = (GridView) view.findViewById(R.id.gv_main);
            adapter = new MainGVAdapter(getContext());
            gv.setAdapter(adapter);
            gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// 去掉gridview点击后出现的黄色边框
            gv.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(activity, R.anim.gv_item_appear));
            //设置gv点击事件
            gv.setOnItemClickListener(this);

            presenter = new MainGVItemsPresenter(this);
            presenter.getItems();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.itemsUpdate();
    }


    /**
     * 进入手机防盗要先进行验证
     */
    public void toPhoneLostInterceptor() {
        presenter.toPhoneLostInterceptor();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    //实现跳转
    @Override
    public void toAntherActivity(Class cls) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        startActivity(intent);
        //切换动画
        activity.overridePendingTransition(R.anim.activit3dtoleft_in,
                R.anim.activit3dtoleft_out);
    }

    @Override
    public void onMainGVItemsGet(List<MainGVItemInfo> mainGVItemInfos) {
        this.mainGVItemInfos = mainGVItemInfos;
        adapter.setInfos(this.mainGVItemInfos);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onMainGVItemsUpdate(List<MainGVItemInfo> mainGVItemInfos) {
        this.mainGVItemInfos = mainGVItemInfos;
        adapter.setInfos(this.mainGVItemInfos);
        adapter.notifyDataSetChanged();
        gv.startLayoutAnimation();//展现动画
    }

    @Override
    public void onMainGVItemAdded(MainGVItemInfo info) {

    }

    @Override
    public void onMainGVItemDel(MainGVItemInfo info) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String method = mainGVItemInfos.get(position).getInvokeMethod();
        if (method != null) {
            try {//反射
                MainFragment.class.getMethod(method).invoke(MainFragment.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {//直接进入
            toAntherActivity(mainGVItemInfos.get(position).getClazz());
        }
    }
}
