package com.vedha.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class LocalFileSyncService {

    @Value("${app.file.local.src}")
    private String sourceDirectory;

    @Value("${app.file.local.dest}")
    private String destinationDirectory;

    @Value("${app.file.local.fileFilter}")
    private String fileFilter;

    @Bean
    @InboundChannelAdapter(channel = "fileInputChannel", poller = @Poller(fixedDelay = "10000"))
    FileReadingMessageSource fileReader() {

        FileReadingMessageSource fileReader = new FileReadingMessageSource();
        fileReader.setDirectory(new File(sourceDirectory));
        fileReader.setAutoCreateDirectory(true);
        fileReader.setFilter(new SimplePatternFileListFilter(fileFilter)); // allow all files
        return fileReader;
    }

    @Bean
    @ServiceActivator(inputChannel = "fileInputChannel")
    FileWritingMessageHandler fileWriter() {

        FileWritingMessageHandler writer = new FileWritingMessageHandler(new File(destinationDirectory));
        writer.setAutoCreateDirectory(true);
        writer.setExpectReply(false);
        writer.setFileExistsMode(FileExistsMode.REPLACE); // overwrite existing files
        return writer;
    }

}
