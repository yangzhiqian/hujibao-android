package edu.ncu.safe.mvp.view;

import android.widget.EditText;

import edu.ncu.safe.domain.User;

/**
 * Created by Mr_Yang on 2016/9/18.
 */
public interface LoginMvpView extends MvpView {
    void loginSucceed(User user);
    void loginFail(EditText view, String errorMessage);
    void startLogin(String uName);
}
