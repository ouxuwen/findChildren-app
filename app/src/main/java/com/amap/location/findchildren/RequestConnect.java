package com.amap.location.findchildren;

import com.amap.api.location.AMapLocation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by moon 2018/2/12.
 */

public class RequestConnect {
    //  public static String SERVER_URL="http://spgpvr.natappfree.cc/indoor/";
   public static String SERVER_URL="http://47.88.58.120/indoor/";

    public static String LOGIN_URL = "login.php";
    public static String REGISTER_URL ="register.php";
    public static String GET_POSITION = "getPosition.php";
    public static String PUT_POSITION = "putPosition.php";
    public static String BIND_CHILDREN = "bindChildren.php";
    public static String UNBIND_CHILDREN = "unBindChildren.php";
    public static String DELETEDATA = "delete.php";
    public static String myLogin(String email,String password,String url){

        String msg = "";

        try {
            HttpURLConnection conn =(HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            JSONObject json = new JSONObject();
            json.put("email",email);
            json.put("password",password);
            String jsonStr = json.toString();
//            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//            conn.setRequestProperty("Accept", "application/json");

            String data = "password="+ URLEncoder.encode(password, "UTF-8")+
                    "&email="+ URLEncoder.encode(email, "UTF-8");
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(data.getBytes());
            wr.flush();
            wr.close();
            int statusCode = conn.getResponseCode();
            if (statusCode == 200) {
                InputStream is = conn.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    message.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                message.close();
                // 返回字符串
                msg = new String(message.toByteArray());
                return msg;
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return msg;
    }

    public static String myRegister(String url,String username,String email ,String phone,String type,String password){

        String msg = "" ;
        try {
            HttpURLConnection conn =(HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

//            JSONObject json = new JSONObject();
//            json.put("email",email);
//            json.put("password",password);
//            String jsonStr = json.toString();
//            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//            conn.setRequestProperty("Accept", "application/json");

            String data = "password="+ URLEncoder.encode(password, "UTF-8")+
                    "&email="+ URLEncoder.encode(email, "UTF-8")+
                    "&username="+ URLEncoder.encode(username, "UTF-8")+
                    "&type="+ URLEncoder.encode(type, "UTF-8")+
                    "&phone="+ URLEncoder.encode(phone, "UTF-8");
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(data.getBytes());
            wr.flush();
            wr.close();
            int statusCode = conn.getResponseCode();
            if (statusCode == 200) {
                InputStream is = conn.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    message.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                message.close();
                // 返回字符串
                msg = new String(message.toByteArray());
                return msg;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        return msg;
    }


    public static String myPutData(String email,String username, AMapLocation location, String url){
        String msg = "" ;
        try {
            HttpURLConnection conn =(HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);



            StringBuffer data = new StringBuffer();
            data.append("email="+ email);
            data.append("&username="+ username);
            data.append("&latitude="+ String.valueOf(location.getLatitude()));
            data.append("&longitude="+ String.valueOf(location.getLongitude()));
            data.append("&province="+ location.getProvince());
            data.append("&city="+ location.getCity());
            data.append("&district="+ location.getDistrict());
            data.append("&address="+ location.getAddress());
            data.append("&time="+ Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") );

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(data.toString().getBytes());
            wr.flush();
            wr.close();
            int statusCode = conn.getResponseCode();
            if (statusCode == 200) {
                InputStream is = conn.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    message.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                message.close();
                // 返回字符串
                msg = new String(message.toByteArray());
                return msg;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        return msg;

    }


    public static String getChildData(String email,String url){
        String msg = "" ;
        try {
            HttpURLConnection conn =(HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
//            JSONObject json = new JSONObject();
//            json.put("email",email);
//            json.put("password",password);
//            String jsonStr = json.toString();
//            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//            conn.setRequestProperty("Accept", "application/json");

            String data = "email="+ URLEncoder.encode(email, "UTF-8");
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(data.getBytes());
            wr.flush();
            wr.close();
            int statusCode = conn.getResponseCode();
            if (statusCode == 200) {
                InputStream is = conn.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    message.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                message.close();
                // 返回字符串
                msg = new String(message.toByteArray());
                return msg;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        return msg;

    }

    public static String bindChildren (String parent_email,String children_email,String url){
        String msg = "" ;
        try {
            HttpURLConnection conn =(HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
//            JSONObject json = new JSONObject();
//            json.put("email",email);
//            json.put("password",password);
//            String jsonStr = json.toString();
//            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//            conn.setRequestProperty("Accept", "application/json");

            String data = "parent_email="+ URLEncoder.encode(parent_email, "UTF-8") +
                    "&child_email="+ URLEncoder.encode(children_email, "UTF-8");
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(data.getBytes());
            wr.flush();
            wr.close();
            int statusCode = conn.getResponseCode();
            if (statusCode == 200) {
                InputStream is = conn.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    message.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                message.close();
                // 返回字符串
                msg = new String(message.toByteArray());
                return msg;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        return msg;

    }

    public static String delete (String url){
        String msg = "" ;
        try {
            HttpURLConnection conn =(HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
//            JSONObject json = new JSONObject();
//            json.put("email",email);
//            json.put("password",password);
//            String jsonStr = json.toString();
//            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//            conn.setRequestProperty("Accept", "application/json");


            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

            wr.flush();
            wr.close();
            int statusCode = conn.getResponseCode();
            if (statusCode == 200) {
                InputStream is = conn.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    message.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                message.close();
                // 返回字符串
                msg = new String(message.toByteArray());
                return msg;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        return msg;

    }


}
