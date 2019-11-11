package com.jike.es.core.study;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EsCoreStudyApplicationTests {

    @Test
    void contextLoads() {
    }

    public static void main(String[] args) {
        String recommendForceNum = "5.0";
        int lastIndexOf = recommendForceNum.lastIndexOf(".");
        String substring = recommendForceNum.substring(0, lastIndexOf);
        System.out.println(substring);
       Double d = 5.7d;
        Integer forceNumInt = (int) Math.floor(d);
        System.out.println(forceNumInt);

    }

}
