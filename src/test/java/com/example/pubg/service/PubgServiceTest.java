package com.example.pubg.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class PubgServiceTest {

    @Autowired
    private PubgService pubgService;

    @Test
    void testGetPlayerByNickname() {
        String nickname = "Kim_Zoey";
        String accountId = pubgService.getPlayerByNickname(nickname);
        System.out.println("Account ID: " + accountId);
    }

    @Test
    void testGetMatchByAccountId() {
        String accountId = "account.f3d0e0ba8f86409a9660901a110e8e43"; // 위에서 얻은 accountId
        List<String> matches = pubgService.getMatchByAccountId(accountId);
        System.out.println("Matches: " + matches);
    }
}
