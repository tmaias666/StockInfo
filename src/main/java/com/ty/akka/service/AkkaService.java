package com.ty.akka.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

@Service
public class AkkaService{

    private static final Logger logger = LoggerFactory.getLogger(AkkaService.class);

    private static final SecureRandom random = new SecureRandom();

    private static List<ActorRef> replyMessageMasters = new ArrayList<>();
    //private static ActorRef taskRouter;

    public AkkaService(){
        logger.info("[Initialize] Akka Service");
        new AkkaSystemFactory<ReplyMessageMaster>(replyMessageMasters, ReplyMessageMaster.class, this.getClass().getSimpleName(), ReplyMessageMaster.class.getSimpleName());
        //taskRouter = new AkkaRouterFactory<MasterOne>(getContext(), MasterOne.class, true).getRouterActor();
    }

    public void handleReplyMessage(Object event){
        logger.debug("handleTask event: {}", event);
        ActorRef taskRouter = this.randomMaster(replyMessageMasters);
        taskRouter.tell(event, ActorRef.noSender());
    }

    private ActorRef randomMaster(List<ActorRef> masters){
        random.setSeed(new Date().getTime());
        int index = random.nextInt(masters.size());
        return masters.get(index);
    }
}
