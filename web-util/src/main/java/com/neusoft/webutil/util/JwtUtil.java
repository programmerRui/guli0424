package com.neusoft.webutil.util;



import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

/**
 * @param
 * @return
 */
public class JwtUtil {

    public static void main(String[] args){

        // 生成token，颁发token
        String salt = "127.0.0.1";
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("userId","2");
        stringStringHashMap.put("nickName","博哥");
        String token = encode("atguigu0328", stringStringHashMap, salt);

        System.out.println(token);


        // 验证身份
        Map userMap = decode("atguigu0328", token, salt);

        System.out.println(userMap);

        Base64UrlCodec b = new Base64UrlCodec();
        byte[] bs = b.decode("eyJuaWNrTmFtZSI6IuWNmuWTpSIsInVzZXJJZCI6IjIifQ");

        String s = new String(bs);

        System.out.println(s+"======================");

    }


    /***
     * jwt加密
     * @param key
     * @param map
     * @param salt
     * @return
     */
    public static String encode(String key,Map map,String salt){

        if(salt!=null){
            key+=salt;
        }
        JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS256, key);
        jwtBuilder.addClaims(map);

        String token = jwtBuilder.compact();
        return token;
    }

    /***
     * jwt解密
     * @param key
     * @param token
     * @param salt
     * @return
     * @throws SignatureException
     */
    public static  Map decode(String key,String token,String salt)throws SignatureException {
        if(salt!=null){
            key+=salt;
        }
        Claims map = null;

        map = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();

        System.out.println("map.toString() = " + map.toString());

        return map;

    }

}
