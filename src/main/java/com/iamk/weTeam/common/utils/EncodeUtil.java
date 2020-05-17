package com.iamk.weTeam.common.utils;

import org.springframework.util.DigestUtils;

public class EncodeUtil {


    public static String md5Encode(String password) {
        return DigestUtils.md5DigestAsHex(password.getBytes());
    }

    public static boolean md5Verify(String password, String pass) {
        return md5Encode(password).equals(pass);
    }
}
