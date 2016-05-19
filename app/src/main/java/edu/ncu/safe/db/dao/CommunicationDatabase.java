package edu.ncu.safe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.db.CommunicationDatabaseHelper;
import edu.ncu.safe.domain.InterceptionInfo;
import edu.ncu.safe.domain.WhiteBlackNumberInfo;
import edu.ncu.safe.engine.LoadPhoneNumberOwnerPalce;

public class CommunicationDatabase {
    private CommunicationDatabaseHelper helper;

    public CommunicationDatabase(Context context) {
        helper = new CommunicationDatabaseHelper(context, "communication");
    }


    //-------------------------------------拦截表----------------------------------

    /**
     * 查询数据库中拦截的短信/电话的条数
     *
     * @param type CommunicationDatabaseHelper.INTERCEPTIONTYPE_MSG(短信)     CommunicationDatabaseHelper.INTERCEPTIONTYPE_PHONE（电话）
     * @return 查询类型的数量  其他类型0
     */
    private int queryInterceptionCount(int type) {
        int count = 0;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(
                CommunicationDatabaseHelper.INTERCEPTIONTABLENAME,
                new String[]{"count(*)"},
                CommunicationDatabaseHelper.INTERCEPTIONCOLUMNS[6] + "=?",
                new String[]{type + ""}, null, null, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        return count;
    }
    /**
     * 查询数据库中拦截的短信/电话的具体信息，封装在InterceptionInfo对象中
     *
     * @param type CommunicationDatabaseHelper.INTERCEPTIONTYPE_MSG(短信)     CommunicationDatabaseHelper.INTERCEPTIONTYPE_PHONE（电话）
     * @param numbers 要查询的数量
     * @param offset 偏移量
     * @return 封装了结果的list结果集
     */
    private List<InterceptionInfo> queryInterceptionInfos(int type,int numbers,int offset){
        List<InterceptionInfo> infos = new ArrayList<InterceptionInfo>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(
                CommunicationDatabaseHelper.INTERCEPTIONTABLENAME,
                null,
                CommunicationDatabaseHelper.INTERCEPTIONCOLUMNS[6] + "=?",
                new String[]{type + ""},
                null,
                null,
                CommunicationDatabaseHelper.INTERCEPTIONCOLUMNS[0] + " desc",
                null);//limit暂时没用

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(CommunicationDatabaseHelper.INTERCEPTIONCOLUMNS[0]));
            String number = cursor.getString(cursor.getColumnIndex(CommunicationDatabaseHelper.INTERCEPTIONCOLUMNS[1]));
            long time = cursor.getLong(cursor.getColumnIndex(CommunicationDatabaseHelper.INTERCEPTIONCOLUMNS[2]));
            String messageBody = cursor.getString(cursor.getColumnIndex(CommunicationDatabaseHelper.INTERCEPTIONCOLUMNS[3]));
            String name = cursor.getString(cursor.getColumnIndex(CommunicationDatabaseHelper.INTERCEPTIONCOLUMNS[4]));
            int numberType = cursor.getInt(cursor.getColumnIndex(CommunicationDatabaseHelper.INTERCEPTIONCOLUMNS[5]));
            InterceptionInfo info = new InterceptionInfo(id, name, number, time, messageBody, numberType);
            infos.add(info);
        }
        db.close();
        return infos;
    }

    /**
     * 项数据库中插入一套拦截信息，拦截信息的内容封装在InterceptionInfo对象中
     * @param type   要插入的类型 CommunicationDatabaseHelper.INTERCEPTIONTYPE_MSG(短信)     CommunicationDatabaseHelper.INTERCEPTIONTYPE_PHONE（电话）
     * @param info 封装了拦截信息的对象
     * @return  true代表插入成功
     */
    private boolean insertOneInterceptionInfo(int type,InterceptionInfo info){
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CommunicationDatabaseHelper.INTERCEPTIONCOLUMNS[1], info.getNumber());
        values.put(CommunicationDatabaseHelper.INTERCEPTIONCOLUMNS[2], info.getInterceptionTime());
        values.put(CommunicationDatabaseHelper.INTERCEPTIONCOLUMNS[3], info.getMessageBody());
        values.put(CommunicationDatabaseHelper.INTERCEPTIONCOLUMNS[4], info.getName());
        values.put(CommunicationDatabaseHelper.INTERCEPTIONCOLUMNS[5], info.getNumberType());
        values.put(CommunicationDatabaseHelper.INTERCEPTIONCOLUMNS[6], type);
        long re = db.insert(CommunicationDatabaseHelper.INTERCEPTIONTABLENAME, null,
                values);
        db.close();
        if(re==-1){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 在数据库中删除一条拦截的信息
     * @param type   要插入的类型 CommunicationDatabaseHelper.INTERCEPTIONTYPE_MSG(短信)     CommunicationDatabaseHelper.INTERCEPTIONTYPE_PHONE（电话）
     * @param id  拦截信息在数据库中的id
     * @return  true代表删除成功
     */
    private boolean deleteOneInterceptionInfo(int type,int id){
        SQLiteDatabase db = helper.getWritableDatabase();
        int re = db.delete(CommunicationDatabaseHelper.INTERCEPTIONTABLENAME,
                CommunicationDatabaseHelper.INTERCEPTIONCOLUMNS[0] + "=?  and " +
                        CommunicationDatabaseHelper.INTERCEPTIONCOLUMNS[6] + "=?",
                new String[]{id + "", type + ""});
        db.close();
        if (re <= 0) {
            return false;
        } else {
            return true;
        }
    }


    //-----------------------------短信拦截表-----------------------------------------
    /**
     * 查询数据库中拦截的短信的条数
     * @return 拦截的短信的条数
     */
    public int queryInterceptionMSGCount(){
        return queryInterceptionCount(CommunicationDatabaseHelper.INTERCEPTIONTYPE_MSG);
    }

    /**
     * 查询数据库中拦截的短信的具体信息，封装在InterceptionInfo对象中
     * @param numbers 要查询的数量
     * @param offset 偏移量
     * @return 封装了结果的list结果集
     */
    public List<InterceptionInfo> queryInterceptionMSGInfos(int numbers,int offset){
        return queryInterceptionInfos(CommunicationDatabaseHelper.INTERCEPTIONTYPE_MSG, numbers, offset);
    }
    /**
     * 项数据库中插入一套拦截信息，拦截信息的内容封装在InterceptionInfo对象中
     * @param info 封装了拦截信息的对象
     * @return  true代表插入成功
     */
    public boolean insertOneInterceptionMSGInfo(InterceptionInfo info){
        return insertOneInterceptionInfo(CommunicationDatabaseHelper.INTERCEPTIONTYPE_MSG, info);
    }
    /**
     * 在数据库中删除一条拦截的信息
     * @param id  拦截信息在数据库中的id
     * @return  true代表删除成功
     */
    public boolean deleteOneInterceptionMSGInfo(int id){
        return deleteOneInterceptionInfo(CommunicationDatabaseHelper.INTERCEPTIONTYPE_MSG, id);
    }

    //-----------------------------电话拦截表-----------------------------------------
    /**
     * 查询数据库中拦截的电话的条数
     * @return 拦截的电话的条数
     */
    public int queryInterceptionPhoneCount(){
        return queryInterceptionCount(CommunicationDatabaseHelper.INTERCEPTIONTYPE_PHONE);
    }

    /**
     * 查询数据库中拦截的电话的具体信息，封装在InterceptionInfo对象中
     * @param numbers 要查询的数量
     * @param offset 偏移量
     * @return 封装了结果的list结果集
     */
    public List<InterceptionInfo> queryInterceptionPhoneInfos(int numbers,int offset){
        return queryInterceptionInfos(CommunicationDatabaseHelper.INTERCEPTIONTYPE_PHONE, numbers, offset);
    }
    /**
     * 项数据库中插入一套拦截信息，拦截信息的内容封装在InterceptionInfo对象中
     * @param info 封装了拦截信息的对象
     * @return  true代表插入成功
     */
    public boolean insertOneInterceptionPhoneInfo(InterceptionInfo info){
        return insertOneInterceptionInfo(CommunicationDatabaseHelper.INTERCEPTIONTYPE_PHONE, info);
    }
    /**
     * 在数据库中删除一条拦截的信息
     * @param id  拦截信息在数据库中的id
     * @return  true代表删除成功
     */
    public boolean deleteOneInterceptionPhoneInfo(int id){
        return deleteOneInterceptionInfo(CommunicationDatabaseHelper.INTERCEPTIONTYPE_PHONE, id);
    }



    // -------------------黑白名单表-------------

    /**
     * 插入一个黑白名单号码 类型有type决定
     *
     * @param info 要插入的名单信息 包括号码，备注，短信权限 电话权限
     * @param type 插入的类型 CommunicationDatabaseHelper.NUMBERTYPE_HIWTE(白名单)
     *             CommunicationDatabaseHelper.NUMBERTYPE_HIWTE(黑名单)
     * @return 成功返回true
     */
    public boolean insertNumber(WhiteBlackNumberInfo info, int type) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CommunicationDatabaseHelper.LISTCOLUMNS[0], info.getNumber());
        values.put(CommunicationDatabaseHelper.LISTCOLUMNS[1], info.getNote());
        values.put(CommunicationDatabaseHelper.LISTCOLUMNS[2], type);
        values.put(CommunicationDatabaseHelper.LISTCOLUMNS[3], info.isSms());
        values.put(CommunicationDatabaseHelper.LISTCOLUMNS[4], info.isPhoneCall());
        long id = db.insert(
                CommunicationDatabaseHelper.WHITEBLACKLISTTABLENAME, null,
                values);
        db.close();

        if (id == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 从黑白名单表中删除一条号码记录
     *
     * @param number 要删除的号码
     * @param type   号码的类型
     * @return 删除成功返回true
     */
    private boolean deleteNumber(String number, int type) {
        SQLiteDatabase db = helper.getWritableDatabase();

        int re = db.delete(CommunicationDatabaseHelper.WHITEBLACKLISTTABLENAME,
                CommunicationDatabaseHelper.LISTCOLUMNS[0] + "=? and "
                        + CommunicationDatabaseHelper.LISTCOLUMNS[2] + "=?",
                new String[]{number, type + ""});
        db.close();
        if (re > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 更新一条号码信息
     *
     * @param info 号码信息
     * @param type 号码类型
     * @return 更新成功返回true
     */
    private boolean updateNumber(WhiteBlackNumberInfo info, int type) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CommunicationDatabaseHelper.LISTCOLUMNS[1], info.getNote());
        values.put(CommunicationDatabaseHelper.LISTCOLUMNS[3], info.isSms());
        values.put(CommunicationDatabaseHelper.LISTCOLUMNS[4],
                info.isPhoneCall());
        int re = db.update(CommunicationDatabaseHelper.WHITEBLACKLISTTABLENAME,
                values, CommunicationDatabaseHelper.LISTCOLUMNS[0] + "=? and "
                        + CommunicationDatabaseHelper.LISTCOLUMNS[2] + "=?",
                new String[]{info.getNumber(), type + ""});
        db.close();

        if (re > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询某种类型名单表
     *
     * @param type 查询的类型 白或黑
     * @return
     */
    private List<WhiteBlackNumberInfo> queryNumberInfos(int type) {
        List<WhiteBlackNumberInfo> infos = new ArrayList<WhiteBlackNumberInfo>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(
                CommunicationDatabaseHelper.WHITEBLACKLISTTABLENAME, null,
                CommunicationDatabaseHelper.LISTCOLUMNS[2] + "=?",
                new String[]{type + ""}, null, null, null);
        while (cursor.moveToNext()) {
            String number = cursor
                    .getString(cursor
                            .getColumnIndex(CommunicationDatabaseHelper.LISTCOLUMNS[0]));
            String note = cursor
                    .getString(cursor
                            .getColumnIndex(CommunicationDatabaseHelper.LISTCOLUMNS[1]));

            boolean isSms = cursor
                    .getInt(cursor
                            .getColumnIndex(CommunicationDatabaseHelper.LISTCOLUMNS[3])) > 0;
            boolean isPhoneCall = cursor
                    .getInt(cursor
                            .getColumnIndex(CommunicationDatabaseHelper.LISTCOLUMNS[4])) > 0;
            WhiteBlackNumberInfo info = new WhiteBlackNumberInfo(number, note,
                    isSms, isPhoneCall);
            infos.add(info);
        }
        db.close();
        return infos;
    }

    /**
     * 查询某个号码是否在黑白名单表中
     * @param type   要查询的类型
     * @param number   号码
     * @return   true代表在相应的表中
     */
    private WhiteBlackNumberInfo queryNumberInWhiteBlackList(int type,String number){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(
                CommunicationDatabaseHelper.WHITEBLACKLISTTABLENAME, null,
                CommunicationDatabaseHelper.LISTCOLUMNS[0] + "=? and   " +
                        CommunicationDatabaseHelper.LISTCOLUMNS[2] + " =? ",
                new String[]{number, type + ""}, null, null, null);
        if(cursor.moveToFirst()){
            String note = cursor
                    .getString(cursor
                            .getColumnIndex(CommunicationDatabaseHelper.LISTCOLUMNS[1]));

            boolean isSms = cursor
                    .getInt(cursor
                            .getColumnIndex(CommunicationDatabaseHelper.LISTCOLUMNS[3])) > 0;
            boolean isPhoneCall = cursor
                    .getInt(cursor
                            .getColumnIndex(CommunicationDatabaseHelper.LISTCOLUMNS[4])) > 0;
            WhiteBlackNumberInfo info = new WhiteBlackNumberInfo(number, note,
                    isSms, isPhoneCall);
            db.close();
            return info;
        }
        db.close();
        return null;
    }

    // --------------------------------白名单----------------------------

    /**
     * 向白名单中插入一条信息
     *
     * @param info 插入的信息对象
     * @return 成功返回true
     */
    public boolean insertWhiteNumber(WhiteBlackNumberInfo info) {
        return insertNumber(info, CommunicationDatabaseHelper.NUMBERTYPE_WHITE);
    }

    /**
     * 删除一条白名单项
     *
     * @param number 要删除的白名单项的号码
     * @return true代表删除成功
     */
    public boolean deleteWhiteNumber(String number) {
        return deleteNumber(number,
                CommunicationDatabaseHelper.NUMBERTYPE_WHITE);
    }

    /**
     * 更新一条白名单的数据内容
     *
     * @param info 要更新的数据内容
     * @return true代表更新成功
     */
    public boolean updateWhiteNumber(WhiteBlackNumberInfo info) {
        return updateNumber(info, CommunicationDatabaseHelper.NUMBERTYPE_WHITE);
    }

    /**
     * 在数据库中查询所有的白名单信息
     *
     * @return 返回一个白名单数据集的集合
     */
    public List<WhiteBlackNumberInfo> queryWhiteNumberInfos() {
        return queryNumberInfos(CommunicationDatabaseHelper.NUMBERTYPE_WHITE);
    }

    /**
     * 查询某个号码是否在白名单表中
     * @param number   号码
     * @return   号码信息  null代表没有找到
     */
    public WhiteBlackNumberInfo queryNumberInWhiteList(String number){
        return queryNumberInWhiteBlackList(CommunicationDatabaseHelper.NUMBERTYPE_WHITE,number);
    }

    // --------------------------------黑名单----------------------------

    /**
     * 向黑名单中插入一条信息
     *
     * @param info 插入的信息对象
     * @return 成功返回true
     */
    public boolean insertBlackNumber(WhiteBlackNumberInfo info) {
        return insertNumber(info, CommunicationDatabaseHelper.NUMBERTYPE_BLACK);
    }

    /**
     * 删除一条黑名单项
     *
     * @param number 要删除的黑名单项的号码
     * @return true代表删除成功
     */
    public boolean deleteBlackNumber(String number) {
        return deleteNumber(number,
                CommunicationDatabaseHelper.NUMBERTYPE_BLACK);
    }

    /**
     * 更新一条黑名单的数据内容
     *
     * @param info 要更新的数据内容
     * @return true代表更新成功
     */
    public boolean updateBlackNumber(WhiteBlackNumberInfo info) {
        return updateNumber(info, CommunicationDatabaseHelper.NUMBERTYPE_BLACK);
    }

    /**
     * 在数据库中查询所有的黑名单信息
     *
     * @return 返回一个黑名单数据集的集合
     */
    public List<WhiteBlackNumberInfo> queryBlackNumberInfos() {
        return queryNumberInfos(CommunicationDatabaseHelper.NUMBERTYPE_BLACK);
    }

    /**
     * 查询某个号码是否在黑名单表中
     * @param number   号码
     * @return   号码信息  null代表没有找到
     */
    public WhiteBlackNumberInfo queryNumberInBlackList(String number){
        return queryNumberInWhiteBlackList(CommunicationDatabaseHelper.NUMBERTYPE_BLACK, number);
    }

    //--------------------------------号码归属地-----------------------------------

    /**
     * 向数据库插入一条号码归属地信息
     *
     * @param info 号码归属地信息对象
     * @return true代表成功
     */
    public boolean insertOneNumberPlace(LoadPhoneNumberOwnerPalce.NumberPlaceInfo info) {
        ContentValues values = new ContentValues();
        values.put(CommunicationDatabaseHelper.NUMBERPLACE[1], info.getAddress());
        values.put(CommunicationDatabaseHelper.NUMBERPLACE[2], info.getProvince());
        values.put(CommunicationDatabaseHelper.NUMBERPLACE[3], info.getCity());
        values.put(CommunicationDatabaseHelper.NUMBERPLACE[4], info.getAreacode());
        values.put(CommunicationDatabaseHelper.NUMBERPLACE[5], info.getPostcode());
        values.put(CommunicationDatabaseHelper.NUMBERPLACE[6], info.getOperator());
        SQLiteDatabase db = helper.getWritableDatabase();
        long insert = db.insert(CommunicationDatabaseHelper.NUMBERPLACETABLENAME, null, values);
        db.close();
        if (insert > 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取所有以前查询过的号码归属地信息
     *
     * @return
     */
    public List<LoadPhoneNumberOwnerPalce.NumberPlaceInfo> queryAllNumberPlaceInfosFromDB() {

        List<LoadPhoneNumberOwnerPalce.NumberPlaceInfo> infos = new ArrayList<LoadPhoneNumberOwnerPalce.NumberPlaceInfo>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(CommunicationDatabaseHelper.NUMBERPLACETABLENAME, null, null, null, null, null, CommunicationDatabaseHelper.NUMBERPLACE[0] + " desc", null);
        while (cursor.moveToNext()) {
            String address = cursor.getString(cursor.getColumnIndex(CommunicationDatabaseHelper.NUMBERPLACE[1]));
            String province = cursor.getString(cursor.getColumnIndex(CommunicationDatabaseHelper.NUMBERPLACE[2]));
            String city = cursor.getString(cursor.getColumnIndex(CommunicationDatabaseHelper.NUMBERPLACE[3]));
            String areacode = cursor.getString(cursor.getColumnIndex(CommunicationDatabaseHelper.NUMBERPLACE[4]));
            String postcode = cursor.getString(cursor.getColumnIndex(CommunicationDatabaseHelper.NUMBERPLACE[5]));
            String operator = cursor.getString(cursor.getColumnIndex(CommunicationDatabaseHelper.NUMBERPLACE[6]));
            LoadPhoneNumberOwnerPalce.NumberPlaceInfo info = new LoadPhoneNumberOwnerPalce.NumberPlaceInfo(address, province, city, areacode, postcode, operator);
            infos.add(info);
        }
        db.close();
        return infos;
    }

    /**
     * 删除一条号码归宿地查询记录
     *
     * @param number 要删除的号码
     */
    public void deleteNumberPlace(String number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(CommunicationDatabaseHelper.NUMBERPLACETABLENAME,
                CommunicationDatabaseHelper.NUMBERPLACE[1] + "=?", new String[]{number});
        db.close();
    }


    //----------------------------------------other----------------------------------------

    /**
     * 查询某个号码的类型  黑名单 白名单  普通号码
     *
     * @param number
     * @return
     */
    public int queryNumberType(String number) {
        int re = CommunicationDatabaseHelper.NUMBERTYPE_NORMAL;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(CommunicationDatabaseHelper.WHITEBLACKLISTTABLENAME,
                new String[]{CommunicationDatabaseHelper.LISTCOLUMNS[2]}, CommunicationDatabaseHelper.LISTCOLUMNS[0] + "=?", new String[]{number}, null, null, null);
        if (cursor.moveToFirst()) {
            re = cursor.getInt(0);
        }
        db.close();
        return re;
    }
}
