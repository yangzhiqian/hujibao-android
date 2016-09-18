package edu.ncu.safe.mvp.view;

import android.widget.EditText;

import edu.ncu.safe.domain.User;

/**
 * Created by Mr_Yang on 2016/9/18.
 */
public interface RegistMvpView extends MvpView {
    void onStartRegist();
    void onRegistSucceed(User user);
    void onRegistFail(EditText view,String errorMessage);
}
