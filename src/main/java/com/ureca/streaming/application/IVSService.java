package com.ureca.streaming.application;

import com.ureca.streaming.infrastructure.StreamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ivs.IvsClient;

@Service
@RequiredArgsConstructor
public class StreamingService {

    private final IvsClient ivsClient; // Injected via IVSConfig
    private final StreamRepository streamRepository;

    public static void createIVSChannel(String channelName) {


    }
}
