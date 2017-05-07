package edu.ncu.safe.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.constant.Constant;
import edu.ncu.safe.domain.ImageInfo;
import edu.ncu.safe.domainadapter.ITarget;
import edu.ncu.safe.domainadapter.ImageAdapter;
import edu.ncu.safe.engine.BackUpDataOperator;
import edu.ncu.safe.engine.ImageLoader;
import edu.ncu.safe.engine.NetDataOperator;
import edu.ncu.safe.engine.PhonePhotoOperator;
import edu.ncu.safe.engine.PhonePhotoCloudOperator;
import edu.ncu.safe.base.fragment.BackupBaseFragment;
import edu.ncu.safe.ui.TouchImageViewActivity;
import edu.ncu.safe.engine.NetDataOperator.BACKUP_TYPE;
import edu.ncu.safe.util.BitmapUtil;

/**
 * Created by Mr_Yang on 2016/6/1.
 */
public class PictureBackupFragment extends BackupBaseFragment {
    protected PhonePhotoCloudOperator phonePhotoCloudOperator;
    protected PhonePhotoOperator phonePhotoOperator;
    protected ImageLoader imageLoader;
    public static PictureBackupFragment newInstance(BACKUP_TYPE type) {
        PictureBackupFragment f = new PictureBackupFragment();
        f.type = type;
        return f;
    }

    @Override
    public void init() {
        phonePhotoCloudOperator = new PhonePhotoCloudOperator(getActivity());
        phonePhotoOperator = new PhonePhotoOperator(getActivity());
        imageLoader = new ImageLoader(getActivity());
    }

    @Override
    protected void loadLocalInfos() {
        List<ImageInfo> localImageInfos = phonePhotoOperator.getLocalImageInfos();
        List<ITarget> infos = new ArrayList<>();
        for (ImageInfo localImageInfo : localImageInfos) {
            infos.add(new ImageAdapter(localImageInfo));
        }
        onLocalInfosLoaded(infos);
    }

    @Override
    protected void loadCloudInfos(final int beginIndex, final int endIndex) {
        phonePhotoCloudOperator.loadCloudDatas(beginIndex, endIndex - beginIndex, new BackUpDataOperator.OnLoadDatasResponseListener<ImageAdapter>() {
            @Override
            public void onFailure(String message) {
                makeToast(message);
            }

            @Override
            public void onDatasGet(List datas, int requestSize) {
                onCloudInfosLoaded(datas,requestSize>endIndex-beginIndex);
            }
        });
    }

    @Override
    protected boolean isSameInfo(ITarget target1, ITarget target2) {
        ImageInfo ii1 = (ImageInfo) target1;
        ImageInfo ii2 = (ImageInfo) target2;
        if(ii1.getPath().equals(ii2.getPath())){
            return true;
        }
        return false;
    }

    @Override
    protected View createShowLocalPopupWindowContentView(final View parent,final int position, final ITarget info) {
        LinearLayout layout = getPopupWindowLayout();
        TextView tv_1 = getPopupWindowTextView("备份");
        layout.addView(tv_1);

        tv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmissPopupWindow();
                onProgressStateChanged(position,true);
                phonePhotoCloudOperator.storeDataToCloud((ImageInfo) info, new BackUpDataOperator.OnStoreDatasResponseListener<ImageInfo>() {

                    @Override
                    public void onError(List<ImageInfo> datas, String message) {
                        onProgressStateChanged(position,false);
                        makeToast(message);
                    }

                    @Override
                    public void onFailure(ImageInfo data, String message) {
                        onProgressStateChanged(position,false);
                        makeToast(message);
                    }

                    @Override
                    public void onSucceed(ImageInfo data, String message) {
                        onProgressStateChanged(position,false);
                        makeToast(message);
                    }

                    @Override
                    public void onProgressUpdated(ImageInfo data, int progress) {
                        onProgressChanged(position,progress);
                    }
                });
            }
        });
        return layout;
    }

    @Override
    protected View createShowCloudPopupWindowContentView(View parent, final int position, final ITarget info) {
        LinearLayout layout = getPopupWindowLayout();
        TextView tv_1 = getPopupWindowTextView("下载");
        layout.addView(tv_1);
        layout.addView(getPopupWindowDivider());
        TextView tv_2 = getPopupWindowTextView("删除");
        layout.addView(tv_2);

        tv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmissPopupWindow();
                onProgressStateChanged(position,true);
                imageLoader.loadImage(info.getTitle(), NetDataOperator.IMG_TYPE.TYPE_SHARPEST, new NetDataOperator.OnImageLoadingListener() {
                    @Override
                    public void onFailure(String error) {
                        makeToast(error);
                        onProgressStateChanged(position,false);
                    }

                    @Override
                    public void onResponse(Bitmap bmp) {
                        try {
                            BitmapUtil.saveBitmapToFile(Constant.getImageFolerPath(),info.getTitle(),bmp);
                        } catch (IOException e) {
                            onFailure("保存失败！");
                        }
                        bmp.recycle();
                        onProgressStateChanged(position,false);
                    }

                    @Override
                    public void onLoadingProgressChanged(int percent) {
                        onProgressChanged(position,percent);
                    }
                });
            }
        });
        tv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phonePhotoCloudOperator.deleteDataFromCloud(info.getID(), new BackUpDataOperator.OnDeleteDatasResponseListener() {
                    @Override
                    public void onFailure(int id, String errorMessage) {
                        makeToast(errorMessage);
                    }

                    @Override
                    public void onSucceed(int id, String message) {
                        makeToast(message);
                    }
                });
            }
        });
        return layout;
    }

    @Override
    protected View createShowRecoveryPopupWindowContentView(View parent,int position, ITarget info) {
        return createShowCloudPopupWindowContentView(parent,position,info);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ImageInfo info = (ImageInfo) adapter.getInfos().get(position);
        Intent intent = new Intent();
        intent.setClass(getActivity(), TouchImageViewActivity.class);
        intent.putExtra("filename", info.getPath());
        startActivity(intent);
    }
}