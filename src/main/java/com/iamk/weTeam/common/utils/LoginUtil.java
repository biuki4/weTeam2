package com.iamk.weTeam.common.utils;

import com.iamk.weTeam.model.entity.GameTag;

import java.util.ArrayList;
import java.util.List;


public class LoginUtil {

    public static void main(String[] args) throws InterruptedException {
        // Date d1 = DateUtil.formatDate("2020-3-18 23:50:59");
        // Date d2 = DateUtil.formatDate("2020-3-10 00:00:00");
        // // int i = DateUtil.betweenDays(d1, d2);
        // // System.out.println(i);
        // Date addMinDate = DateUtil.getAddMinDate(d2, 10);
        // System.out.println(addMinDate);
        // DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Calendar c = Calendar.getInstance();
        // c.add(Calendar.MONTH, -3);
        // Date d = c.getTime();
        // System.out.println(df.format(d));



        // Date beforeMonth = DateUtil.getBeforeMonth(-1);
        // System.out.println(beforeMonth);
        // Date beforeDay = DateUtil.getBeforeDay(-18);
        // System.out.println(beforeDay);
        // Date beforeYear = DateUtil.getBeforeYear(-1);
        // System.out.println(beforeYear);


        GameTag g = new GameTag();
        g.setId(1);
        GameTag b = new GameTag();
        b.setId(2);
        List<GameTag> list = new ArrayList<>();
        list.add(g);
        list.add(b);
        System.out.println(list.toString());
        if(list.contains(g)){
            list.remove(0);
        }
        System.out.println(list.toString());


        // for (int i = 0; i <list.size() ; i++) {
        //     if(list.get(i) == g) {
        //         System.out.println("123");
        //         list.remove(i);
        //     }
        // }



    }
}
