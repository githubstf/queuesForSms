package com.buybal.queues.listener;

import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.buybal.queues.service.SendSmsService;

/**
 * 通邦短信队列监听
 * @author Hippo
 *
 */
public class QueueTBSmsListener implements MessageListener{
    private final static Logger logger = Logger.getLogger(QueueTBSmsListener.class);
    public SendSmsService sendSmsService;

	public void setSendSmsService(SendSmsService sendSmsService) {
		this.sendSmsService = sendSmsService;
	}


	@Override
	public void onMessage(Message message){
        if(message instanceof ObjectMessage){
            final ObjectMessage objectMessage = (ObjectMessage) message;
            try{
                logger.info(objectMessage.getObject());
                @SuppressWarnings("unchecked")
				Map<String,String> map= (Map<String, String>)objectMessage.getObject();
                if(null==map){
                	logger.error("[队列短信发送]短信发送消息格式错误");
                	return ;
                }
        		if(null==map.get("mobiles")||"".equals(map.get("mobiles"))){
        			logger.error("[队列短信发送]短信发送手机号不能为空！");
        			return ;
        		}
        		if(null==map.get("message")||"".equals(map.get("message"))){
        			logger.error("[队列短信发送]短信发送内容不能为空！");
        			return ;
        		}
        		String dateTime = map.get("dateTime");
        		if(null==map.get("dateTime")||"".equals(map.get("dateTime"))){
        			dateTime = "";
        		}
        		sendSmsService.sendSMSForTB(map.get("mobiles"), dateTime, map.get("message"));
                Thread.sleep(1000);
            }catch(final Exception e){
                logger.error("[队列短信发送]短信发送失败",e);
            }
        }
    }

}
