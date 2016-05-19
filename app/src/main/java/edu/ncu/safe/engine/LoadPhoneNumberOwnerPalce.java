package edu.ncu.safe.engine;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.ncu.safe.R;

/**
 * Created by Mr_Yang on 2016/5/15.
 */
public class LoadPhoneNumberOwnerPalce {
    private Context context;
    private String number = "phone=13133846188";

    private OnOwnerPlaceObtainedListener listener;

    public LoadPhoneNumberOwnerPalce(Context context) {
        this.context = context;
    }

    public LoadPhoneNumberOwnerPalce(Context context, String number) {
        this.context = context;
        this.number = "phone=" + number;
    }

    public void setNumber(String number) {
        this.number = "phone=" + number;
    }

    public void setOnOwnerPlaceObtainedListener(OnOwnerPlaceObtainedListener listener) {
        this.listener = listener;
    }

    public interface OnOwnerPlaceObtainedListener {
        public void OnOwnerPalceObtained(NumberPlaceInfo info);
    }

    private void notifyListener(NumberPlaceInfo info) {
        if (listener != null) {
            listener.OnOwnerPalceObtained(info);
        }
    }

    /**
     * 尝试联网去获取json数据
     */
    public void request() {
        new Thread() {
            @Override
            public void run() {


                String url = context.getResources().getString(R.string.baiduphonenumberownerplaceurl);
                String key = context.getResources().getString(R.string.baiduphonenumberownerplacekey);
                BufferedReader reader = null;
                String result = null;
                StringBuffer sbf = new StringBuffer();
                String httpUrl = url + "?" + number;
                try {
                    URL urlCon = new URL(httpUrl);
                    HttpURLConnection connection = (HttpURLConnection) urlCon.openConnection();
                    connection.setRequestMethod("GET");
                    // 填入apikey到HTTP header
                    connection.setRequestProperty("apikey", key);
                    connection.connect();
                    InputStream is = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String strRead = null;
                    while ((strRead = reader.readLine()) != null) {
                        sbf.append(strRead);
                        sbf.append("\r\n");
                    }
                    reader.close();
                    result = sbf.toString();
                    //获取成功
                    notifyListener(pullJsonToDomain(result));
                } catch (Exception e) {
                    e.printStackTrace();
                    //获取失败
                    notifyListener(null);
                }
            }
        }.start();
    }

    //
//    {
//        "error":"0",
//            "msg":"",
//            "data":{
//                    "phone":"13588888888",
//                    "province":"浙江",//可能没有
//                    "city":"杭州",
//                    "areacode":"0571",
//                    "postcode":"310000",
//                    "operator":"中国移动"
//                  }
//    }
    private NumberPlaceInfo pullJsonToDomain(String json) {
        NumberPlaceInfo info = null;
        try {
            info = new NumberPlaceInfo();
            JSONObject obj = new JSONObject(json).getJSONObject("data");
            try {info.setAddress(obj.getString("phone"));          }catch (Exception e){}
            try {info.setProvince(obj.getString("province"));      }catch (Exception e){}
            try {info.setCity(obj.getString("city"));               }catch (Exception e){}
            try {info.setAreacode(obj.getString("areacode"));      }catch (Exception e){}
            try {info.setPostcode(obj.getString("postcode"));      }catch (Exception e){}
            try {info.setOperator(obj.getString("operator"));      }catch (Exception e){}
            return info;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class NumberPlaceInfo {
        private String address;
        private String province;
        private String city;
        private String areacode;
        private String postcode;
        private String operator;

        public NumberPlaceInfo() {
        }

        public NumberPlaceInfo(String address, String province, String city, String areacode, String postcode, String operator) {
            this.address = address;
            this.province = province;
            this.city = city;
            this.areacode = areacode;
            this.postcode = postcode;
            this.operator = operator;
        }

        public String getPostcode() {
            return postcode;
        }

        public void setPostcode(String postcode) {
            this.postcode = postcode;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getAreacode() {
            return areacode;
        }

        public void setAreacode(String areacode) {
            this.areacode = areacode;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }
        @Override
        public String toString() {
            String re="";
            if(province!=null){
                re+=province;
            }
            if(city!=null){
                re = re+" "+ city;
            }
            if(operator!=null){
                re= re+" " + operator;
            }
            return re;
        }

    }
}
