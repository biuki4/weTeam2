package com.iamk.weTeam;

import com.iamk.weTeam.common.config.MiniProgramConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class WeTeamApplicationTests {

    @Autowired
    MiniProgramConfig miniProgramConfig;

    @Test
    public void test1() {
        System.out.println(miniProgramConfig.APPID);
        System.out.println(miniProgramConfig.APPSECRET);
    }

}
