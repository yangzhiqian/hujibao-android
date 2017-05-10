package edu.ncu.safe.engine;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.domain.ContactsInfo;

public class PhoneContactsOperator {
    private Context context;

    public PhoneContactsOperator(Context context) {
        this.context = context.getApplicationContext();
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

    /**
     * 将一条联系人信息恢复到联系人数据库
     * @param info     联系人数据
     * @throws SecurityException   如果app没有被授予写入联系人权限，将会抛出该异常
     */
    public void recoveryOneContact(ContactsInfo info) throws SecurityException{
        ContentValues values = new ContentValues();
        //首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获取系统返回的rawContactId
        Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);

        //往data表插入姓名数据
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);//内容类型
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, info.getName());
        context.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
        //插入电话号码
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, info.getPhoneNumber());
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        context.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
    }
}
