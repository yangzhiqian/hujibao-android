package edu.ncu.safe.ui.fragment;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.domain.ContactsInfo;
import edu.ncu.safe.domainadapter.ContactsAdapter;
import edu.ncu.safe.domainadapter.ITarget;
import edu.ncu.safe.engine.BackUpDataOperator;
import edu.ncu.safe.engine.PhoneContactsOperator;
import edu.ncu.safe.engine.PhoneContactCloudOperator;
import edu.ncu.safe.base.fragment.BackupBaseFragment;
import edu.ncu.safe.engine.NetDataOperator.BACKUP_TYPE;
import edu.ncu.safe.util.ContactUtil;

/**
 * Created by Mr_Yang on 2016/6/1.
 */
public class ContactsBackupFragment extends BackupBaseFragment {
    private PhoneContactCloudOperator phoneContactCloudOperator;
    private PhoneContactsOperator phoneContactsOperator;

    public static ContactsBackupFragment newInstance(BACKUP_TYPE type) {
        ContactsBackupFragment f = new ContactsBackupFragment();
        f.type = type;
        return f;
    }


    @Override
    public void init() {
        phoneContactCloudOperator = new PhoneContactCloudOperator(getActivity());
        phoneContactsOperator = new PhoneContactsOperator(getActivity());
    }

    @Override
    protected void loadLocalInfos() {
        List<ContactsInfo> contactsInfos = phoneContactsOperator.getContactsInfos();
        List<ITarget> contactsAdapters = new ArrayList<>();
        for (ContactsInfo contactsInfo : contactsInfos) {
            contactsAdapters.add(new ContactsAdapter(contactsInfo));
        }
        onLocalInfosLoaded(contactsAdapters);
    }

    @Override
    protected void loadCloudInfos(final int beginIndex, final int endIndex) {
        phoneContactCloudOperator.loadCloudDatas(beginIndex, endIndex - beginIndex, new BackUpDataOperator.OnLoadDatasResponseListener() {
            @Override
            public void onFailure(String message) {
                makeToast(message);
            }

            @Override
            public void onDatasGet(List datas, int requestSize) {
                onCloudInfosLoaded(datas, requestSize > endIndex - beginIndex);
            }
        });
    }

    @Override
    protected boolean isSameInfo(ITarget target1, ITarget target2) {
        ContactsInfo c1 = (ContactsInfo) target1;
        ContactsInfo c2 = (ContactsInfo) target2;
        if (c1.getName().equals(c2.getName()) && c1.getPhoneNumber().equals(c2.getPhoneNumber())) {
            return true;
        }
        return false;
    }

    @Override
    protected View createShowLocalPopupWindowContentView(View parent, final int position,final ITarget info) {
        LinearLayout layout = getPopupWindowLayout();
        TextView tv_1 = getPopupWindowTextView("备份");
        layout.addView(tv_1);
        layout.addView(getPopupWindowDivider());

        TextView tv_2 = getPopupWindowTextView("回复短信");
        layout.addView(tv_2);
        layout.addView(getPopupWindowDivider());

        TextView tv_3 = getPopupWindowTextView("回拨电话");
        layout.addView(tv_3);

        tv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmissPopupWindow();
                phoneContactCloudOperator.storeDataToCloud((ContactsInfo) info, new BackUpDataOperator.OnStoreDatasResponseListener<ContactsInfo>() {
                    @Override
                    public void onError(List<ContactsInfo> datas, String message) {
                        makeToast(message);
                    }

                    @Override
                    public void onFailure(ContactsInfo data, String message) {
                        makeToast(message);
                    }

                    @Override
                    public void onSucceed(ContactsInfo data, String message) {
                        makeToast(message);
                    }

                    @Override
                    public void onProgressUpdated(ContactsInfo data, int progress) {
                    }
                });
            }
        });
        tv_2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dissmissPopupWindow();
                                        ContactUtil.sendMessageTo(getActivity(), ((ContactsInfo) info).getPhoneNumber());
                                    }
                                }

        );
        tv_3.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View view) {
                                        dissmissPopupWindow();
                                        ContactUtil.callTo(getActivity(), ((ContactsInfo) info).getPhoneNumber());
                                    }
                                }

        );
        return layout;
    }

    @Override
    protected View createShowCloudPopupWindowContentView(View parent,final int position, final ITarget info) {
        LinearLayout layout = getPopupWindowLayout();
        TextView tv_1 = getPopupWindowTextView("恢复到短信");
        layout.addView(tv_1);
        layout.addView(getPopupWindowDivider());

        TextView tv_2 = getPopupWindowTextView("删除");
        layout.addView(tv_2);

        tv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmissPopupWindow();
                phoneContactsOperator.recoveryOneContact((ContactsInfo) info);
            }
        });
        tv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmissPopupWindow();
                phoneContactCloudOperator.deleteDataFromCloud(info.getID(), new BackUpDataOperator.OnDeleteDatasResponseListener() {
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
    protected View createShowRecoveryPopupWindowContentView(View parent,final int position, ITarget info) {
        return createShowCloudPopupWindowContentView(parent,position,info);
    }
}
