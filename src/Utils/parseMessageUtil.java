package Utils;

import DateManager.FormattingDate;
import Responses.BaseResponse;
import Responses.MultiArticleResponse;
import Responses.TextResponse;
import Service.DataBaseService.QueryArticle;
import Service.QueryDB;
import log4j.Log4j;

import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static DateManager.FormattingDate.isMatchDate;

/**
 * Created by Alchemist on 2016/5/5.
 */
public class parseMessageUtil {
    public static String parseTextMessage(Map<String,String> requestMap) throws Exception{
        String responseXML=null;
        String fromUserName = requestMap.get("FromUserName");
        String toUserName = requestMap.get("ToUserName");
        QueryDB queryDB = new QueryDB();
        String content = requestMap.get("Content");
        BaseResponse ResponseMessage=null;
        if(isQuery(fromUserName)) {
            Log4j log4j = new Log4j();
            log4j.infolog(content);
            if(content.equals("c")){
                queryDB.stopQueryArticle(fromUserName);
                TextResponse textResponse = new TextResponse();
                textResponse.setContent("成功取消查询");
                textResponse.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                ResponseMessage = textResponse;
            }else if (isDate(content)){
                log4j.infolog("Content:"+content);
                String date = FormattingDate.FormattingDate(content);
                log4j.infolog("Date:"+date);
                ResponseMessage = QueryArticle.queryArticleDate(date);
            }else {
                ResponseMessage = QueryArticle.queryArticleText(requestMap.get("Content"));
            }
        }else if(content.equals("q")) {
            queryDB.startQuery(fromUserName);
            TextResponse textResponse = new TextResponse();
            textResponse.setContent("输入文章标题或日期(20160623)查询往期文章,回复\"c\"取消查询");
            textResponse.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
            ResponseMessage = textResponse;
        }else{
            TextResponse text = new TextResponse();
            text.setContent("谢谢您的留言，小编会尽快回复：）点击下方菜单栏，可以查看各栏目精选哟~输入q查询往期文章");
            text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
            ResponseMessage = text;
        }
        ResponseMessage.setToUserName(fromUserName);
        ResponseMessage.setFromUserName(toUserName);
        ResponseMessage.setCreateTime(new Date().getTime());
        responseXML = MessageUtil.messageToXML(ResponseMessage);
        return responseXML;
    }

    private static boolean isDate(String content){
        String regex1="20[0-9][0-9]-[0-1][0-9]-[0-3][0-9]";
        String regex2="20[0-9][0-9]/[0-1][0-9]/[0-3][0-9]";
        String regex3="20[0-9][0-9][0-1][0-9][0-3][0-9]";
        return isMatchDate(content,regex1)||isMatchDate(content,regex2)||isMatchDate(content,regex3);
    }
    private static boolean isNum(String content){
        String regex = "^[1-9]([0-9]?){2}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        return matcher.matches();
    }
    private static boolean isQuery(String fromUserName){
        boolean result=false;
        QueryDB queryDB = new QueryDB();
        if(queryDB.isInQuery(fromUserName)){
            result=true;
        }
        return result;
    }
    public static void main(String args[]){
        System.out.println(isDate("2016-05-19"));
    }
}
