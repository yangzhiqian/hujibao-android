package edu.ncu.safe.test;//package edu.ncu.safe.test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.test.AndroidTestCase;
//import edu.ncu.safe.domain.SmsInfo;
//import edu.ncu.safe.engine.PhoneSmsOperator;
//
//public class SmsTest extends AndroidTestCase {
//	public void testGet(){
//		PhoneSmsOperator sms = new PhoneSmsOperator(getContext());
//		List<SmsInfo> infos = sms.getSms();
//		System.out.println(infos.size());
//		for(SmsInfo info : infos){
//			System.out.println("     id:"+info.getId());
//			System.out.println("address:"+info.getAddress());
//			System.out.println("   date:"+info.getDate());
//			System.out.println("   type:"+info.getType());
//			System.out.println("   body:"+info.getBody());
//			System.out.println("-----------------------------");
//		}
//	}
//
//	public void testRecover(){
//		PhoneSmsOperator sms = new PhoneSmsOperator(getContext());
//		List<SmsInfo> infos = new ArrayList<SmsInfo>();
//		SmsInfo info = new SmsInfo("dfasgas", "123456789", 132164646, 2, "testtesttestesdsetsetestts");
//		infos.add(info);
//		sms.recoverSms(infos);
//		testGet();
//	}
//}
