package com.ty.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.linecorp.bot.client.LineMessagingClient;

@Configuration
public class LineConfig{

    @Value("${line.bot.channelToken}")
    private String channelAccessToken;

    @Bean
    public LineMessagingClient lineMessagingClient(){
        LineMessagingClient client = LineMessagingClient.builder(channelAccessToken).build();
        return client;
    }
}
