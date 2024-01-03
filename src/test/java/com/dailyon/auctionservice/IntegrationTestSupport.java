package com.dailyon.auctionservice;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(
        properties = {
                "cloud.aws.dynamodb.endpoint=http://localhost:8000",
                "cloud.aws.credentials.ACCESS_KEY_ID=testkey",
                "cloud.aws.credentials.SECRET_ACCESS_KEY=testkey"
        })
public class IntegrationTestSupport {

}
