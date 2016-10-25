package edu.ncu.safe.test;

import android.test.AndroidTestCase;

import edu.ncu.safe.engine.IpTable;
import edu.ncu.safe.util.MyMathUtil;

/**
 * Created by Mr_Yang on 2016/5/23.
 */
public class IpTableTest extends AndroidTestCase{
    public void test(){
//        List<UserAppSimpleInfo> infos = new AppInfosLoader(getContext()).getUserAppSimpleInfos();
//        List<Integer> uids = new ArrayList<Integer>();
//        for(UserAppSimpleInfo info:infos){
//            uids.add(info.getUid());
//        }

        IpTable.clearIpTable(getContext());
//       IpTable.updateBlackIPTable(getContext(),uids,uids);
        IpTable.loadIpTableItems(getContext());
    }

    public void testmaht(){
        System.out.println(MyMathUtil.toAngle(0,360,10,20));
        System.out.println(MyMathUtil.toAngle(0,360,20,20));
        System.out.println(MyMathUtil.toAngle(0,360,30,20));
        System.out.println(MyMathUtil.toAngle(0,360,40,20));
        System.out.println(MyMathUtil.toAngle(0,360,50,20));
        System.out.println(MyMathUtil.toAngle(0,360,60,20));
        System.out.println(MyMathUtil.toAngle(0,360,70,20));
        System.out.println(MyMathUtil.toAngle(0,360,80,20));
        System.out.println(MyMathUtil.toAngle(0,360,90,20));
        System.out.println(MyMathUtil.toAngle(0,360,100,20));
    }
}
