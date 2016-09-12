package com.buybal.queues.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.buybal.queues.common.HttpHelper;
import com.buybal.queues.common.HttpRequester;
import com.buybal.queues.common.HttpRespons;
import com.buybal.queues.util.MD5Util;


public class SendSmsService {
	private static final Logger logger=Logger.getLogger(SendSmsService.class);
	ResourceBundle rb = ResourceBundle.getBundle("jms");
    public SendSmsService(){
    	
    }
    
	/**
	 * 游通宝(去哪儿)
	 */
    
	public void sendSMSForYTB(String mobiles,String dateTime,String message)  {
		String result="";
		try {
			ResourceBundle rb = ResourceBundle.getBundle("jms");
			String sendurl = rb.getString("YTB_sendurl");
			StringBuffer sb = new StringBuffer();
			sb.append("type=").append("qunar_pay_ytb").append("&");
			sb.append("date=").append("").append("&");
			sb.append("mobiles=").append(mobiles).append("&");
			sb.append("message=").append(message.replaceAll("<br/>", " ")).append("&");
			sb.append("groupid=").append("").append("&");
			
			result = HttpHelper.doHttp(sendurl, HttpHelper.POST, "UTF-8", sb.toString(), "30000").getBody();
		} catch (Exception e) {
			logger.error("发送短信to:"+mobiles+"内容："+message+"状态：发送失败",e);
			return;
		}
		logger.info("发送短信to:"+mobiles+"内容："+message+"状态：发送成功返回代码："+result);
	}
	
	/**
	 * 发送方法  其他方法同理(通邦)
	 * 请求参数： 以下参数均为String类型
	 */
	public void sendSMSForTB(String mobiles,String dateTime,String message)  {
		String result="";
		try {
			String sendurl = rb.getString("TB_sendurl");
			String smsname = rb.getString("TB_smsname");
			String smspwd = rb.getString("TB_smspwd");
			Map map = new HashMap();
			map.put("name",smsname ); //sms发送类型【稍后提供】
			map.put("pwd",smspwd);//设定发送时间，为null则立即发送，定时发送则使用 yyyy/MM/dd HH:mm:ss格式
			map.put("dst", mobiles); //发送的手机号码数组，发送到多个手机请使用“,”分隔
			map.put("sender","");//发送消息的内容，长度请确保在64个字符以内，否则将拆分多条发送，计费加倍
			map.put("time", dateTime);//默认为null。
			map.put("txt", "");//默认为null。
			map.put("msg", message);//默认为null。
			HttpRequester requester = new HttpRequester();
			HttpRespons respons = requester.sendPost(sendurl, map);
			result =respons.getContent();
		} catch (Exception e) {
			logger.error("发送短信to:"+mobiles+"内容："+message+"状态：发送失败",e);
			return ;
		}
		logger.info("发送短信to:"+mobiles+"内容："+message+"状态：发送成功返回内容："+result);
	}
	
	/**
	 * 通付宝
	 * @param mobile 发送手机号
	 * @param content 内容
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String sendSMSForTFB(String mobiles, String content){
		String TFB_name = rb.getString("TFB_name");
		String TFB_pwd = rb.getString("TFB_pwd");
		String TFB_url = rb.getString("TFB_url");
		String result = "-20";
		Integer x_ac = 10;// 发送信息
		HttpURLConnection httpconn = null;
		try {
			String memo = content.length() < 70 ? content.trim() : content.trim().substring(0, 70);
			StringBuilder sb = new StringBuilder();
			sb.append(TFB_url);
			sb.append("?x_eid=").append(0); //0为个人
			sb.append("&x_uid=").append(TFB_name);
			sb.append("&x_pwd_md5=").append(MD5Util.md5(TFB_pwd));
			sb.append("&x_ac=").append(x_ac);
			sb.append("&x_gate_id=").append(300);
			sb.append("&x_target_no=").append(mobiles);
			sb.append("&x_memo=").append(URLEncoder.encode(memo, "utf-8"));
		
			URL url = new URL(sb.toString());
			httpconn = (HttpURLConnection) url.openConnection();
			BufferedReader rd = new BufferedReader(new InputStreamReader(httpconn.getInputStream()));
			result = rd.readLine();
			rd.close();
		} catch (MalformedURLException e) {
			logger.error("发送短信to:"+mobiles+"内容："+content+"状态：发送失败",e);
		} catch (IOException e) {
			logger.error("发送短信to:"+mobiles+"内容："+content+"状态：发送失败",e);
		} finally {
			if (httpconn != null) {
				httpconn.disconnect();
				httpconn = null;
			}

		}
		logger.info("发送短信to:"+mobiles+"内容："+content+"状态：发送成功返回内容大于0表示发送成功："+result);
		return result;
	}
	
	/**
	 * 漫道短信群发
	 * @param mobile
	 * @param content
	 * @param ext
	 * @param stime
	 * @param rrid
	 */
	public void sendSMSForMD(String mobile,String content,String ext,String stime,String rrid)  {
		
		String result="";
		try {
			String sendurl = rb.getString("MD_url");
			String smsname = rb.getString("MD_sn");
			String smspwd = rb.getString("MD_pwd");
			String MD_COM_NAME = rb.getString("MD_COM_NAME");
			if(MD_COM_NAME != null && MD_COM_NAME.trim().length() > 0){
				if(content.indexOf("["+MD_COM_NAME) == -1 && content.indexOf("【"+MD_COM_NAME) == -1){
					content = content+" ["+MD_COM_NAME+"]";
				}
			}
			Map map = new HashMap();
			map.put("sn",smsname ); //sms发送类型【稍后提供】
			map.put("pwd",MD5Util.md5(smsname+smspwd));//设定发送时间，为null则立即发送，定时发送则使用 yyyy/MM/dd HH:mm:ss格式
			map.put("mobile", mobile); //发送的手机号码数组，发送到多个手机请使用“,”分隔
			map.put("Content",content);//发送消息的内容，长度请确保在64个字符以内，否则将拆分多条发送，计费加倍
			map.put("Ext", "");//默认为null。
			map.put("stime",stime);//设置发送时间
			map.put("Rrid","");//默认为null。
			HttpRequester requester = new HttpRequester();
			HttpRespons respons = requester.sendPost(sendurl, map);
			result =respons.getContent();
		} catch (Exception e) {
			logger.error("发送短信to:"+mobile+"内容："+content+"状态：发送失败",e);
			return ;
		}
		logger.info("发送短信to:"+mobile+"内容："+content+"状态：发送成功返回内容："+result);
	}
	
	/**
	 * 查询余额
	 */
	public void selectYuEForMD(){
		String result="";
		try {
			String sendurl = rb.getString("MD_yueUrl");
			String sn = rb.getString("MD_sn");
			String pwd = rb.getString("MD_pwd");
			Map map = new HashMap();
			map.put("sn",sn );
			map.put("pwd",MD5Util.md5(sn+pwd));
			HttpRequester requester = new HttpRequester();
			HttpRespons respons = requester.sendPost(sendurl, map);
			result =respons.getContent();
		} catch (Exception e) {
			logger.error("查询余额失败",e);
			return ;
		}
		logger.info("查询余额成功"+result);
	}
	
	/**
	 * 多玩(YY聚汇支付)
	 * @param mobile 发送手机号
	 * @param content 内容
	 * @return
	 * 	1 短信发送成功
		-1 mac值验证错误
	    -2 参数信息不完整
	    -3 信息入库失败
	    -4 发送接口失败
	    -5 项目编号错误
	    -6 手机号码错误
	 */
	public String sendSMSForDuoWan(String mobiles, String content){
		String DuoWan_url = rb.getString("DuoWan_url");
		String DuoWan_userid = rb.getString("DuoWan_userid");
		String DuoWan_subuser = rb.getString("DuoWan_subuser");
		String DuoWan_key = rb.getString("DuoWan_key");
		String result = "";
		HttpURLConnection httpconn = null;
		try {
			String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			StringBuilder sb = new StringBuilder();
			sb.append(DuoWan_url);
			sb.append("?userid=").append(DuoWan_userid);
			sb.append("&phone=").append(mobiles);
			sb.append("&subuser=").append(DuoWan_subuser);
			sb.append("&time=").append(time);
			sb.append("&mac=").append(MD5Util.md5(DuoWan_userid+ mobiles + DuoWan_subuser+time+ DuoWan_key).toLowerCase());
			sb.append("&content=").append(URLEncoder.encode(content, "utf-8"));
			logger.info("多玩短信通道发送内容"+sb.toString());
		
			URL url = new URL(sb.toString());
			httpconn = (HttpURLConnection) url.openConnection();
			BufferedReader rd = new BufferedReader(new InputStreamReader(httpconn.getInputStream()));
			String str = "";
			while((str = rd.readLine()) != null){
				result += str;
			}
			rd.close();
		} catch (MalformedURLException e) {
			logger.error("发送短信to:"+mobiles+"内容："+content+"状态：发送失败",e);
		} catch (IOException e) {
			logger.error("发送短信to:"+mobiles+"内容："+content+"状态：发送失败",e);
		} finally {
			if (httpconn != null) {
				httpconn.disconnect();
				httpconn = null;
			}
		}
		logger.info("发送短信to:"+mobiles+", 内容:"+content+", 状态:发送成功返回内容为1表示发送成功:result=["+result+"]");
		return result;
	}
	

	public static void main(String[] args) throws UnsupportedEncodingException {
		SendSmsService s= new SendSmsService();
		//s.sendSMSForTB("13810307099,15032275120","","您好~金飞翔···");
		//s.sendSMSForTFB("13810307099,18600376567","您好~通付宝···");
		s.sendSMSForMD("13521560169", "你好~~~~~~~~~  尚腾飞测试【百博时代科技】~~~~~","","","");
		//s.selectYuEForMD();
		//s.sendSMSForYTB("13810307099,18600376567", null, "测试~游通宝····");
		//s.sendSMSForDuoWan("13521560169","您好~尚腾飞(百博时代科技有限公司)···");
	}
}
