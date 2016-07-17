package edu.ncu.safe.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.MyApplication;
import edu.ncu.safe.R;
import edu.ncu.safe.adapter.BackupLVAdapter;
import edu.ncu.safe.constant.Constant;
import edu.ncu.safe.domain.ImageInfo;
import edu.ncu.safe.domain.User;
import edu.ncu.safe.domainadapter.ITarget;
import edu.ncu.safe.domainadapter.ImageAdapter;
import edu.ncu.safe.engine.DataLoader;
import edu.ncu.safe.engine.DataStorer;
import edu.ncu.safe.engine.LoadLocalImageInfo;
import edu.ncu.safe.external.ACache;
import edu.ncu.safe.myadapter.BackupBaseFragment;
import edu.ncu.safe.ui.TouchImageViewActivity;
import edu.ncu.safe.util.BitmapUtil;

/**
 * Created by Mr_Yang on 2016/6/1.
 */
public class PictureBackupFragment extends BackupBaseFragment {
    public PictureBackupFragment(int type) {
        super(type);
    }
    @Override
    public void init() {
        showLocal();
    }

    @Override
    public List<ITarget> loadLocalInfos() {
        new AsyncTask<Void, Integer, List<ITarget>>() {
            @Override
            protected List<ITarget> doInBackground(Void... params) {
                LoadLocalImageInfo loadLocalImage = new LoadLocalImageInfo(getContext());
                List<ImageInfo> imageInfos = loadLocalImage.getLocalImageInfos();
                List<ITarget> infos = new ArrayList<ITarget>();
                for (ImageInfo info : imageInfos) {
                    infos.add(new ImageAdapter(info));
                }
                return infos;
            }

            @Override
            protected void onPostExecute(List<ITarget> infos) {
                localInfos = infos;
                if (currentShowType == SHOWTYPE_LOCAL) {
                    adapter.setInfos(localInfos);
                    adapter.notifyDataSetChanged();
                    showLoader(false);
                    if (ptr.isRefreshing()) {
                        ptr.refreshComplete();
                    }
                }
                super.onPostExecute(infos);
            }
        }.execute();
        return null;
    }

    protected View getBackupView(final View parent, final int position, final ITarget info) {
        LinearLayout layout = getLayout();
        TextView tv_bk = getTextView(getResources().getString(R.string.backup_pupup_item_backup_cloud));
        layout.addView(tv_bk);
        tv_bk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupWindow.dismiss();
                String url = getContext().getResources().getString(R.string.storeimg);
                BackupLVAdapter.ViewHolder holder = (BackupLVAdapter.ViewHolder) parent.getTag();
                DataStorer storer = new DataStorer(getContext());
                storer.setOnImgUploadedListener(new DataStorer.OnImgUploadedListener() {
                    @Override
                    public void onFailure(String error) {
                        makeToast(error);
                        adapter.setItemInDownloading(parent, position, false);
                    }

                    @Override
                    public void onSucceed(String message) {
                        adapter.setItemInDownloading(parent, position, false);
                        try {
                            JSONObject object = new JSONObject(message);
                            message = object.getString("message");
                            if(object.getBoolean("succeed")){
                                User user = User.getUserFromSP(getContext());
                                File file = new File(info.getIconPath());
                                user.setUsed( user.getUsed() +  file.length());
                                SharedPreferences.Editor edit = MyApplication.getSharedPreferences().edit();
                                edit.putString(MyApplication.SP_STRING_USER,user.toJson());
                                edit.apply();
                            }
                            makeToast(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                            makeToast(getResources().getString(R.string.toast_error_unknow));
                        }
                    }
                });
                User user = User.getUserFromSP(getContext());
                if(user==null){
                    makeToast(getResources().getString(R.string.toast_un_log_in));
                    return;
                }
                adapter.setItemInDownloading(parent, position, true);
                storer.storeImg(info.getIconPath(), url, user.getToken(), holder.mpb_downloadProgress);
            }
        });
        return layout;
    }

    public List<ITarget> parseToInfos(String json) throws JSONException, RuntimeException {
        List<ITarget> infos = new ArrayList<ITarget>();
        JSONObject object = new JSONObject(json);
        boolean succeed = object.optBoolean("succeed", false);
        int code = object.optInt("code", -1);
        if (succeed) {
            JSONArray jsonArray = object.getJSONObject("message").getJSONArray("data");
            JSONObject item = null;
            ImageAdapter info;
            ImageInfo imageInfo;
            for (int i = 0; i < jsonArray.length(); i++) {
                item = jsonArray.getJSONObject(i);
                int id = item.getInt("pid");
                long lastModified = item.getLong("lastModified");
                String name = item.getString("name");
                int size = item.getInt("size");
                imageInfo = new ImageInfo(name, name, lastModified, size);
                info = new ImageAdapter(imageInfo);
                info.setID(id);
                infos.add(info);
            }
        } else {
            throw new RuntimeException(code + "");
        }
        return infos;
    }

    @Override
    public void backToPhone(final View parent,final int position, final ITarget info) {
        info.setIsInDownload(true);
        adapter.setItemInDownloading(parent, position, true);
        popupWindow.dismiss();
        BackupLVAdapter.ViewHolder holder = (BackupLVAdapter.ViewHolder) parent.getTag();

        new DataLoader(getContext()).loadImage( info.getTitle(), DataLoader.TYPE_BIG, holder.mpb_downloadProgress, new DataLoader.OnImageObtainedListener() {
            @Override
            public void onFailure(String error) {
                info.setIsInDownload(false);
                adapter.setItemInDownloading(parent, position, false);
                makeToast(getResources().getString(R.string.toast_error_fail_to_load_img));
            }

            @Override
            public void onResponse(Bitmap bmp) {
                info.setIsInDownload(false);
                adapter.setItemInDownloading(parent, position, false);
                //缓存七天
                ACache.get(getContext()).put(Constant.getImageCacheFileName(info.getTitle(), DataLoader.TYPE_BIG), bmp, Constant.ACACHE_LIFETIME);
                //下载图片，要保存在指定文件中
                try {
                    if (BitmapUtil.saveBitmapToFile(Constant.getImageFolerPath(), info.getTitle(), bmp)) {
                        makeToast("图片已保存在" + Constant.getImageFolerPath() + "目录下");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    makeToast(getResources().getString(R.string.toast_error_fail_to_save_img));
                }
            }
        });
    }

    @Override
    public List<ITarget> getBackupInfos() {
        return new ArrayList<ITarget>();
    }

    @Override
    public List<ITarget> getRecoveryInfos() {
        if(cloudInfos==null){
            return null;
        }
        List<ITarget> infos  = new ArrayList<ITarget>();
        for(ITarget cloudInfo:cloudInfos){
            ImageInfo imageInfo = (ImageInfo) cloudInfo;
            boolean b = true;
            for(ITarget localInfo:localInfos){
                ImageInfo temp = (ImageInfo) localInfo;
                if(temp.getName().equals(imageInfo.getName())){
                    //存在
                    b = false;
                    break;
                }
            }
            if(b){
                infos.add(cloudInfo);
            }
        }
        return infos;
    }


    @Override
    public void onDownloadProgressBarClicked(View parent, int position, ITarget data) {

    }

    @Override
    public void onCheckBoxCheckedChanged(View parent, int position, ITarget data, boolean isChecked) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ImageInfo info = (ImageInfo) adapter.getInfos().get(position);
        Intent intent = new Intent();
        intent.setClass(getContext(), TouchImageViewActivity.class);
        intent.putExtra("filename", info.getPath());
        startActivity(intent);
    }
}