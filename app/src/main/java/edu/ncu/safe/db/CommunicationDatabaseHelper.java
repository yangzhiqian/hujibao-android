package edu.ncu.safe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CommunicationDatabaseHelper extends SQLiteOpenHelper {
    public static final String INTERCEPTIONTABLENAME = "interception";
    public static final String WHITEBLACKLISTTABLENAME = "whiteblacklist";
    public static final String NUMBERPLACETABLENAME = "numberplace";


    //联系人类型   普通  白名单  黑名单
    public static final int NUMBERTYPE_NORMAL = 0;
    public static final int NUMBERTYPE_WHITE = 1;
    public static final int NUMBERTYPE_BLACK = 2;

    public static final int INTERCEPTIONTYPE_MSG = 0;
    public static final int INTERCEPTIONTYPE_PHONE = 1;
     //拦截表
    //id(自增长)   number(号码)    intercepttime(拦截时间)   messagebody（短信的内容，短信才有）
    // name(名字)   numbertype（联系人的类型,普通、白名单、黑名单）  interceptiontype（拦截类型 短信、电话）
    public static final String[] INTERCEPTIONCOLUMNS = {"id", "number",
            "intercepttime", "messagebody", "name", "numbertype","interceptiontype"};
    //黑白名单表
    //number(号码，主键唯一)   name(备注)     numbertype（号码的类型  白名单、黑名单）sms(短信权限)   phonecall（电话权限）
    public static final String[] LISTCOLUMNS = {"number", "name", "numbertype", "sms", "phonecall"};
    //号码归属地查询表
    public static final String[] NUMBERPLACE = {"id","address","province","city","areacode","postcode","operator"};

    public CommunicationDatabaseHelper(Context context, String name) {
        super(context, name, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建拦截表
        String sql = "create table " + INTERCEPTIONTABLENAME + "(" +
                INTERCEPTIONCOLUMNS[0] + " integer primary key autoincrement ," +                    // id
                INTERCEPTIONCOLUMNS[1] + " varchar not null," +                                        //号码
                INTERCEPTIONCOLUMNS[2] + " long default (datetime('now','localtime'))," +           //拦截时间
                INTERCEPTIONCOLUMNS[3] + " varchar," +                                                  //短信主体
                INTERCEPTIONCOLUMNS[4] + " varchar," +                                                  //备注
                INTERCEPTIONCOLUMNS[5] + " int default " + NUMBERTYPE_NORMAL +","+                        // 号码类型
                INTERCEPTIONCOLUMNS[6] + " int not null)";                                              // 拦截类型
        System.out.println(sql);
        db.execSQL(sql);
        //创建黑白名单表
        sql = "create table " + WHITEBLACKLISTTABLENAME + "(" +
                LISTCOLUMNS[0] + " varchar primary key," +              //号码
                LISTCOLUMNS[1] + " varchar," +                           //备注
                LISTCOLUMNS[2] + " int not null," +                      //号码类型
                LISTCOLUMNS[3] + " boolean default false," +             //短信权限
                LISTCOLUMNS[4] + " boolean default false " + ")";        //电话权限
        db.execSQL(sql);

        //创建号码归宿地查询表
        sql = "create table " + NUMBERPLACETABLENAME + "(" +
                NUMBERPLACE[0] + " INTEGER primary key autoincrement," +  //id
                NUMBERPLACE[1] + " varchar not null," +                 //address
                NUMBERPLACE[2] + " varchar default '未知'," +                     //province
                NUMBERPLACE[3] + " varchar default '未知'," +               //city
                NUMBERPLACE[4] + " varchar default '未知'," +               //areacode
                NUMBERPLACE[5] + " varchar default '未知'," +               //postcode
                NUMBERPLACE[6] + " varchar default '未知')" ;             //operator
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
