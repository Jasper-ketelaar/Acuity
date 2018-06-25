package com.acuitybotting.path_finding.xtea;

import com.acuitybotting.data.flow.messaging.services.client.MessagingClientService;
import com.acuitybotting.data.flow.messaging.services.client.message.Message;
import com.acuitybotting.db.arango.path_finding.domain.xtea.Xtea;
import com.acuitybotting.db.arango.path_finding.repositories.xtea.XteaRepository;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Zachary Herridge on 6/22/2018.
 */
@Service
@Slf4j
public class XteaService {

    private final XteaRepository xteaRepository;
    private final MessagingClientService clientService;
    private Gson gson = new Gson();

    private static final String QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/604080725100/acuitybotting-xtea-dump.fifo";

    @Autowired
    public XteaService(XteaRepository xteaRepository, MessagingClientService clientService) {
        this.xteaRepository = xteaRepository;
        this.clientService = clientService;
    }

    public int worldToRegionId(int worldX, int worldY){
        worldX >>>= 6;
        worldY >>>= 6;
        return (worldX << 8) | worldY;
    }

    public void consumeQueue(){
        int[] emptyKeys = {0, 0, 0, 0};

        clientService.setDeleteMessageOnConsume(false).consumeQueue(QUEUE_URL, message -> {
            try {
                Xtea[] xteas = gson.fromJson(message.getBody(), Xtea[].class);
                for (Xtea xtea : xteas) {
                    if (xtea.getKeys() == null || Arrays.equals(xtea.getKeys(), emptyKeys)) continue;
                    xteaRepository.save(xtea);
                    log.info("Saved Xtea Key {}.", xtea);
                }
                clientService.deleteMessage(QUEUE_URL, message);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });
    }
}
