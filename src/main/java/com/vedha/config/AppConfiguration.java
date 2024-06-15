package com.vedha.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class AppConfiguration {

    @Bean("fileInputChannel")
    MessageChannel fileInputChannel() {

        return new DirectChannel();
    }

    @Bean("sftpChannelDownload")
    MessageChannel sftpChannel() {

        return new DirectChannel();
    }

    @Bean("sftpChannelUpload")
    MessageChannel sftpChannelUpload() {

        return new DirectChannel();
    }
}
