package edu.ncu.safe.mvp.view;

import android.graphics.Bitmap;

import java.util.ArrayList;

import edu.ncu.safe.domain.MainMenuInfo;
import edu.ncu.safe.domain.User;

/**
 * Created by Mr_Yang on 2016/9/17.
 */
public interface MainMenuMvpView extends MvpView {
    void onLogIn(User user);
    void onCheckStateOnLine(User user);
    void onCheckStateOffline(String description);
    void onLogOut(User lastUser,String message);
    void onItemsGet(ArrayList<MainMenuInfo> infos);
    void onAvatorGet(Bitmap bitmap);
    void onAvatorGetError(String message);
}
