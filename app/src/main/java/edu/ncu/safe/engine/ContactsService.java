package edu.ncu.safe.engine;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.domain.ContactsInfo;

public class ContactsService {
    private Context context;

    public ContactsService(Context context) {
        this.context = context;
    }

    public List<ContactsInfo> getContactsInfos() {
        List<ContactsInfo> infos = new ArrayList<ContactsInfo>();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            //一个人有几个手机号码
            int isHas = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
            if (isHas > 0) {
                Cursor c = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                while (c.moveToNext()) {
                    String number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    ContactsInfo info = new ContactsInfo(name, number);
                    infos.add(info);
                }
                c.close();
            }
        }
        cursor.close();
        return infos;
    }

    /**
     * 根据number获取联系人的备注
     * @param number
     * @return
     */
    public String getContactName(String number) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = " + number,
                null,
                null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            Cursor c = resolver.query(ContactsContract.Contacts.CONTENT_URI,
                    null,
                    ContactsContract.Contacts._ID + "=?",
                    new String[]{id + ""}, null);
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            c.close();
            cursor.close();
            return number;
        }
        cursor.close();
        return null;
    }
}
