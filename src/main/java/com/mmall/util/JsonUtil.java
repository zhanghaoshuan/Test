package com.mmall.util;


import com.google.common.collect.Lists;
import com.mmall.pojo.User;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2018/4/26.
 */
public class JsonUtil {
    private static Logger logger= LoggerFactory.getLogger(JsonUtil.class);

    private static ObjectMapper objectMapper=new ObjectMapper();

    static {
        //对象的所有字段全部列入
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);
        //取消默认转换timestamps形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);
        //忽略空bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        //所有的日期格式都统一为以下样式
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));
        //忽略在json字符串中存在，但是在java对象中不存在对应属性的情况。防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    public static <T> String obj2String(T obj){
        if(obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj:objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.warn("Parse object to String error"+e);
            return null;
        }
    }

    public static <T> String obj2StringPretty(T obj){
        if(obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj:objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            logger.warn("Parse object to String error",e);
            return null;
        }
    }

    public static <T> T string2Obj(String str,Class<T> clazz){
        if(StringUtils.isEmpty(str)||clazz==null){
            return null;
        }
        try {
            return clazz.equals(String.class)?(T)str:objectMapper.readValue(str,clazz);
        } catch (Exception e) {
            logger.warn("Parse String to object error",e);
            return null;
        }
    }

    public static <T> T String2Obj(String str, TypeReference<T> typeReference){
        if(StringUtils.isEmpty(str)||typeReference==null){
            return null;
        }
        try {
            return typeReference.getType().equals(String.class)?(T)str:(T)objectMapper.readValue(str,typeReference);
        } catch (Exception e) {
            logger.warn("Parse String to object error",e);
            return null;
        }
    }

    public static <T> T String2Obj(String str,Class<?> collectionClass,Class<?>... elementClass){
        JavaType javaType=objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClass);

        try {
            return objectMapper.readValue(str,javaType);
        } catch (Exception e) {
            logger.warn("Parse String to object error",e);
            return null;
        }
    }

    public static void main(String[] args) {
        User u1=new User();
        u1.setId(1);
        u1.setEmail("iiiii");

        User u2=new User();
        u1.setId(2);
        u1.setEmail("iiiii2222");
        String user1Json=JsonUtil.obj2String(u1);
        String user1JsonPretty=JsonUtil.obj2StringPretty(u1);
        logger.info("user1json:{}",user1Json);
        logger.info("user1json:{}",user1JsonPretty);
        User user=JsonUtil.string2Obj(user1Json,User.class);

        List<User> userList= Lists.newArrayList();
        userList.add(u1);
        userList.add(u2);

        String userListStr=JsonUtil.obj2StringPretty(userList);
        logger.info("========");
        logger.info(userListStr);

        List<User> result=JsonUtil.String2Obj(userListStr, new TypeReference<List<User>>(){});
        List<User> result2=JsonUtil.String2Obj(userListStr, List.class,User.class);
        System.out.println("end");
    }


}
