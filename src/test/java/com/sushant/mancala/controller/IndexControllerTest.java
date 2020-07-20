package com.sushant.mancala.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Base64Utils;

import javax.inject.Inject;

import static com.sushant.mancala.constant.GameConstants.PLAYER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IndexController.class)
public class IndexControllerTest {
    @Inject
    private MockMvc mockMvc;

    @Test
    public void index()  throws Exception{
        this.mockMvc
                .perform(
                        get("/")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Basic " + Base64Utils.encodeToString((PLAYER_1 + ":test").getBytes()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMovedTemporarily());

    }
}
