package edu.ncu.safe.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;

import edu.ncu.safe.R;
import edu.ncu.safe.customerview.MyDialog;

/**
 * Created by Mr_Yang on 2016/9/17.
 */
public class MyDialogHelper {
    public static void showInputPWDDialog(@NonNull final Context context, @NonNull final InputChecker checker, final InputCallBack callBack) {
        final MyDialog myDialog = new MyDialog(context);
        myDialog.setTitle(context.getString(R.string.dialog_input_pwd));
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_passwordenter, null);
        final AutoCompleteTextView pwd = (AutoCompleteTextView) view.findViewById(R.id.actv_pwd);
        myDialog.setMessageView(view);
        myDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pd =pwd.getText().toString().trim();
                if(checker.checkInputFormatLegal(pd)){
                    //格式正确
                    if(checker.checkPWDCorrect(pd)){
                        //验证密码正确
                        myDialog.dismiss();
                        if(callBack!=null){
                            callBack.inputSucceed(pd);
                        }
                    }else{
                        pwd.setError(context.getString(R.string.error_pwd));
                        if(callBack!=null){
                            callBack.inputError(context.getString(R.string.error_pwd));
                        }
                    }
                }else{
                    pwd.setError(context.getString(R.string.error_pwd_format));
                    if(callBack!=null){
                        callBack.inputError(context.getString(R.string.error_pwd_format));
                    }
                }
            }
        });
        myDialog.show();
    }

    public static void showResetPWDDialog(@NonNull final Context context, @NonNull final InputChecker checker, final InputCallBack callBack) {
        final MyDialog myDialog = new MyDialog(context);
        myDialog.setTitle(context.getString(R.string.dialog_enter_pwd));
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_passwordregister, null);
        final AutoCompleteTextView pwd_one = (AutoCompleteTextView) view.findViewById(R.id.actv_pwd_one);
        final AutoCompleteTextView pwd_two = (AutoCompleteTextView) view.findViewById(R.id.actv_pwd_two);
        myDialog.setMessageView(view);
        myDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pd = pwd_one.getText().toString().trim();
                String pdAgain = pwd_two.getText().toString().trim();
                if (pd.equals(pdAgain)) {//比对两次输入
                    if(checker.checkInputFormatLegal(pd)){
                        //验证格式正确
                        myDialog.dismiss();
                        if(callBack!=null){
                            callBack.inputSucceed(pd);
                        }
                    }else{
                        pwd_two.setError(context.getString(R.string.error_pwd_format));
                        if(callBack!=null){
                            callBack.inputError(context.getString(R.string.error_pwd_format));
                        }
                    }
                } else {
                    pwd_two.setError(context.getString(R.string.error_pwd_different));
                    if(callBack!=null){
                        callBack.inputError(context.getString(R.string.error_pwd_different));
                    }
                }
            }
        });
        myDialog.show();
    }


    public static class InputChecker{
        public boolean checkPWDCorrect(String inputPWD){
            return false;
        }
        public boolean checkInputFormatLegal(String input){
            if(input.trim().length()>=3){
                return true;
            }else{
                return false;
            }
        }
    }

    public static interface InputCallBack{
        void inputSucceed(String input);
        void inputError(String error);
    }
}
