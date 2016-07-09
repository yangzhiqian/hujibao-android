package edu.ncu.safe.ui.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.BackupLVAdapter;
import edu.ncu.safe.domain.ImageInfo;
import edu.ncu.safe.domain.User;
import edu.ncu.safe.domainadapter.ITarget;
import edu.ncu.safe.domainadapter.ImageAdapter;
import edu.ncu.safe.engine.DataStorer;
import edu.ncu.safe.engine.LoadLocalImageInfo;
import edu.ncu.safe.myadapter.BackupBaseFragment;
import edu.ncu.safe.ui.TouchImageViewActivity;

/**
 * Created by Mr_Yang on 2016/6/1.
 */
public class PictureBackupFragment extends BackupBaseFragment {

    private static final String TAG = "PictureBackupFragment";

    public PictureBackupFragment(User user,int type) {
        super(user,type);
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
                    if(ptr.isRefreshing()){
                        ptr.refreshComplete();
                    }
                }
                super.onPostExecute(infos);
            }
        }.execute();
        return null;
    }

    protected View getRecorveryView(View parent, int position, final ITarget info) {
        LinearLayout layout = getLayout();
        TextView tv_bk = getTextView("恢复到本机");
        TextView tv_del = getTextView("删除");
        layout.addView(tv_bk);
        layout.addView(getDivider());
        layout.addView(tv_del);

        tv_bk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info.setIsInDownload(true);
                adapter.notifyDataSetChanged();
                popupWindow.dismiss();
            }
        });
        tv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        return layout;
    }

    protected View getBackupView(final View parent, final int position, final ITarget info) {
        LinearLayout layout = getLayout();
        TextView tv_bk = getTextView("备份到云端");
        layout.addView(tv_bk);
        tv_bk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setItemInDownloading(parent,position,true);
                popupWindow.dismiss();
                String url = getContext().getResources().getString(R.string.storeimg);
                BackupLVAdapter.ViewHolder holder = (BackupLVAdapter.ViewHolder) parent.getTag();
                DataStorer storer = new DataStorer(getContext());
                storer.setOnImgUploadedListener(new DataStorer.OnImgUploadedListener() {
                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        adapter.setItemInDownloading(parent,position,false);
                    }

                    @Override
                    public void onSucceed(String message) {
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        adapter.setItemInDownloading(parent, position, false);
                    }
                });
                storer.storeImg(info.getIconPath(), url, user.getToken(), holder.mpb_downloadProgress);
            }
        });
        return layout;
    }

    public List<ITarget> parseToInfos(String json) throws JSONException,RuntimeException {
        List<ITarget> infos = new ArrayList<ITarget>();
        JSONObject object = new JSONObject(json);
        boolean succeed = object.optBoolean("succeed", false);
        int code = object.optInt("code", -1);
        if (succeed) {
            JSONArray jsonArray = object.getJSONObject("message").getJSONArray("data");
            JSONObject item = null;
            ImageAdapter info;
            ImageInfo imageInfo ;
            for(int i =0 ;i<jsonArray.length();i++){
                item = jsonArray.getJSONObject(i);
                long lastModified = item.getLong("lastModified");
                String name = item.getString("name");
                int size = item.getInt("size");
                imageInfo = new ImageInfo(name,name,lastModified,size);
                info = new ImageAdapter(imageInfo);
                infos.add(info);
            }
        } else {
            throw new RuntimeException(code+"");
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
        intent.putExtra("token", getUser().getToken());
        startActivity(intent);
    }
}