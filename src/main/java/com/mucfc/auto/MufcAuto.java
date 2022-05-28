package com.mucfc.auto;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mucfc.auto.util.ExcelUtils;
import com.mucfc.auto.vo.MkCase;
import com.mucfc.auto.vo.User;
import com.mucfc.auto.vo.UserDetail;
import com.mucfc.auto.vo.UserNumber;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MufcAuto {

    public static final String COOKIE = "xbte=06xge0e1jysu2w0j71pto1ussqse; MUID=2d4d9aa8d387d80fb1653718494953; MUGY=0; YTSSESSIONID=F279DB34A5CE7EDD822E4F109D35E124.4d; MUSESSIONID=914D6B63D73A097374E94D9BB43B8B6B.4d";

    public static final String BASE_URL = "https://mgp.api.mucfc.com/coop?operationId=mucfc.sale.bench.queryTaskList";

    public static final String ID_URL = "https://mgp.api.mucfc.com/coop?operationId=mucfc.coop.wechat.queryWeChatTaskInfoList";

    public static final String DETAIL_URL = "https://mgp.api.mucfc.com/coop?operationId=mucfc.coop.wechat.queryWeChatTaskDetail";



    public static void main(String[] args) throws InterruptedException {

        List<String> numberList = new ArrayList<>();

        int falseCount = 0;

        int totalCount = 0;

        List<User> baseList = getBaseList();

        for (User user : baseList) {
            List<Integer> userIdByName = getUserIdByName(user.getName());

            if (userIdByName.isEmpty()){
                falseCount++;
                continue;
            }
            for (Integer userId : userIdByName) {

                String stringId = String.valueOf(userId);
                UserDetail userDetailById = getUserDetailById(stringId);
                String maskMobile = userDetailById.getMaskMobile();
                String stringSubMk = maskMobile.substring(0, 3);
                String stringSubUser = user.getPhoneNO().substring(1, 4);

                if (!stringSubMk.equals(stringSubUser)){
                    continue;
                }

                if (stringSubMk.equals(stringSubUser)){
                    //处理
                    String trueNumber = user.getPhoneNO().substring(1,8)+ maskMobile.substring(maskMobile.length()-4,maskMobile.length());

                    totalCount++;
                    numberList.add(trueNumber);

                }

            }

        }


        ExcelUtils.createExcel(numberList,"phoneNumber","电话号码");

        System.out.println("根据名字查不出的个数:"+falseCount);
        System.out.println("电话个数："+totalCount);



    }

    // 获取基本的消息列表
    private static List<User> getBaseList() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("reqEnvParams", "{\"sign\":\"b09224a2e3l7jui42469ff35\",\"channel\":\"0XSYZWEC\",\"appType\":\"PC\"}");
        jsonObject.put("data", "{\"pageNo\":1,\"pageSize\":100,\"primaryMarketPurposeCode\":\"wechatAdd\",\"distributeStartTime\":1653667200000,\"distributeEndTime\":1653753599000,\"advancedSearchFlag\":false}");

        String result = sendHttp(jsonObject, BASE_URL, COOKIE);


        JSONObject datas = JSONObject.parseObject(result);
        JSONObject data = JSONObject.parseObject(datas.get("data").toString());
        JSONObject jo = JSONObject.parseObject(data.toJSONString());
        JSONArray mkCaseList = JSONObject.parseArray(jo.get("mkCaseList").toString());

        List<User> userList = new ArrayList<>();
        if (!mkCaseList.isEmpty()){
            List<MkCase> mkCases = JSONArray.parseArray(mkCaseList.toString(), MkCase.class);

            for (MkCase mkCase : mkCases) {
                User user = new User();
                user.setName(mkCase.getCustName());
                user.setPhoneNO(mkCase.getMobileNo());
                userList.add(user);
            }


        }
        return userList;
    }

    // 通过名字获取用户ID
    private static List<Integer> getUserIdByName(String name) throws InterruptedException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("reqEnvParams", "{\"channel\":\"0XSYZWEC\",\"appType\":\"PC\",\"module\":\"salestool\",\"token\":\"JyUQtBhjvgfYh0EBkCiQjA-hADYs_zbAw6d3030i21\",\"sign\":\"d17f7cf3fqtah57wbf885ca4\",\"pageUrl\":\"https://rts.mucfc.com/0XSYZWEC/salestool/#/task/wx-list?needLogin=1&menuCode=M2020081400000008\",\"mapCode\":\"\",\"clientTime\":\"1653719928333\",\"uuid\":\"2d4d9aa8d387d80fb1653718494953\"}");
        String stringFist = "{\"pageNo\":1,\"pageSize\":15,\"taskStatus\":1,\"custName\":\"";
        String stringLast = "\",\"endTime\":null,\"beginTime\":null}";
        String stringResult = stringFist+name+stringLast;
        jsonObject.put("data", stringResult);

        Thread.sleep(500);
        String result = sendHttp(jsonObject, ID_URL, COOKIE);
        List<Integer> idList = new ArrayList<>();

        JSONObject datas = JSONObject.parseObject(result);
        JSONObject data = JSONObject.parseObject(datas.get("data").toString());
        JSONObject jo = JSONObject.parseObject(data.toJSONString());
        JSONArray list = new JSONArray();
        if (jo.get("list") != null){
             list = JSONObject.parseArray(jo.get("list").toString());
        }


        List<UserNumber> userNumberList = new ArrayList<>();
        if (!list.isEmpty()){
            List<UserNumber> userNumbers = JSONArray.parseArray(list.toString(), UserNumber.class);
            idList = userNumbers.stream().map(UserNumber::getId).collect(Collectors.toList());


        }
        return idList;
    }

    // 通过ID获取用户详细信息
    private static UserDetail getUserDetailById(String id) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("reqEnvParams", "{\"channel\":\"0XSYZWEC\",\"appType\":\"PC\",\"module\":\"salestool\",\"token\":\"KLVAoBLt7gcbc0iBlCEQHA3JADGnefeGA6d3030i49\",\"sign\":\"7194a4aeo35dcckc7c3ada9f\",\"pageUrl\":\"https://rts.mucfc.com/0XSYZWEC/salestool/#/task/wx-detail?taskId=8853767\",\"mapCode\":\"\",\"clientTime\":\"1653720595788\",\"uuid\":\"2d4d9aa8d387d80fb1653718494953\"}");
        String stringFist = "{\"taskId\":";
        String stringLast = "}";
        String stringResult = stringFist+id+stringLast;
        jsonObject.put("data", stringResult);

        String result = sendHttp(jsonObject, DETAIL_URL, COOKIE);
        List<Integer> idList = new ArrayList<>();
        UserDetail userDetail = new UserDetail();
        JSONObject datas = JSONObject.parseObject(result);
        JSONObject data = JSONObject.parseObject(datas.get("data").toString());
        JSONObject jo = JSONObject.parseObject(data.toJSONString());

        userDetail.setMaskMobile(jo.get("maskMobile").toString());
        userDetail.setName(jo.get("name").toString());

        return userDetail;
    }



    public static String sendHttp(JSONObject jsonObject, String url, String cookie) {
        //入参,key需要是对方接口定义的key,不能随便传

        String result = null;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);

            List<BasicNameValuePair> paras = new ArrayList<BasicNameValuePair>();
            for (String key : jsonObject.keySet()) {
                paras.add(new BasicNameValuePair(key, jsonObject.getString(key)));
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paras, "UTF-8");

            httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
            httpPost.setHeader("Cookie", cookie);
            httpPost.setEntity(entity);

            response = httpClient.execute(httpPost);
            //返回状态码
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode==200) {
                result = EntityUtils.toString(response.getEntity(), "UTF-8");

            }



        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}

