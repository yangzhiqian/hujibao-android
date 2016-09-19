package edu.ncu.safe.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import edu.ncu.safe.MyApplication;
import edu.ncu.safe.R;
import edu.ncu.safe.adapter.ContactsDialogAdapter;
import edu.ncu.safe.customerview.MyDialog;
import edu.ncu.safe.engine.ContactsService;

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

    public static void showResetPhoneNumbers(@NonNull final Context context, @NonNull final InputChecker checker, final InputCallBack callBack){
        //拿到本机的手机号
        String phoneNumber = MyUtil.getPhoneNumber(context);
        SharedPreferences sp = MyApplication.getSharedPreferences();
        String userNumber = sp.getString(MyApplication.SP_STRING_USER_PHONE_NUMBER, null);
        String safeNumber = sp.getString(MyApplication.SP_STRING_SAFE_PHONE_NUMBER, null);

        final MyDialog myDialog = new MyDialog(context);
        myDialog.setTitle(context.getString(R.string.dialog_title_set_numbers));
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_phonenumber, null);
        myDialog.setMessageView(view);
        final EditText et_userNumber = (EditText) view
                .findViewById(R.id.usernumber);
        final EditText et_safeNumber = (EditText) view
                .findViewById(R.id.safenumber);
        ImageView contacts = (ImageView) view.findViewById(R.id.contects);

        //填入默认值
        if (userNumber != null) {
            et_userNumber.setText(userNumber);
        } else if (phoneNumber != null) {
            et_userNumber.setText(phoneNumber);
        } else {
            et_userNumber.setHint(context.getString(R.string.dialog_edittext_hine_user_number));
        }

        if (safeNumber != null) {
            et_safeNumber.setText(safeNumber);
        } else {
            et_safeNumber.setHint(context.getString(R.string.dialog_edittext_hine_safe_number));
        }

        myDialog.setPositiveListener(new View.OnClickListener() {
            public void onClick(View v) {
                String userNumber = et_userNumber.getText().toString().trim();
                String safeNumber = et_safeNumber.getText().toString().trim();
                if(!checker.checkInputFormatLegal(userNumber)){
                    //格式不正确
                    et_userNumber.setError(context.getString(R.string.error_number_format));
                    return;
                }
                if(!checker.checkInputFormatLegal(safeNumber)){
                    //格式不正确
                    et_userNumber.setError(context.getString(R.string.error_number_format));
                    return;
                }
                callBack.inputSucceed(userNumber+" "+safeNumber);
                myDialog.dismiss();
            }
        });
        contacts.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectContactInDialog(context, new InputCallBack() {
                    @Override
                    public void inputSucceed(String input) {
                        et_safeNumber.setText(input);
                    }

                    @Override
                    public void inputError(String error) {

                    }
                });
            }
        });
        myDialog.show();
    }


    /**
     * 显示选择联系人的对话框
     * @param context
     * @param callBack    选择后的回调接口
     */
    public static void selectContactInDialog(@NonNull final Context context,  final InputCallBack callBack){
        final MyDialog myDialog = new MyDialog(context);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_contacts, null);
        myDialog.setMessageView(view);
        ListView lv = (ListView) view.findViewById(R.id.lv_contacts);
        final ContactsDialogAdapter adapter = new ContactsDialogAdapter(
                new ContactsService(context).getContactsInfos(), context);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String number = adapter.getNumber(position);
                callBack.inputSucceed(number);
                myDialog.dismiss();
            }
        });
        myDialog.ShowYESNO(false);
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
