package com.ty.akka.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.ty.Util.ApplicationContextUtil;
import com.ty.service.LineMessageService;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

public class ReplyMessageMaster extends AbstractActor{

    private static final Logger logger = LoggerFactory.getLogger(ReplyMessageMaster.class);

    static public Props props(){
        return Props.create(ReplyMessageMaster.class, ReplyMessageMaster::new);
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private LineMessageService lineMessageService = null;

    public ReplyMessageMaster(){
    }

    @Override
    public void preStart(){
        lineMessageService = (LineMessageService) ApplicationContextUtil.getBean(LineMessageService.class);
        log.info(this.self() + "ReplyMessageMaster actor started");
    }

    @Override
    public void postStop(){
        log.info(this.self() + "ReplyMessageMaster actor stopped");
    }

    private void replyMessage(String message){
        try{
            lineMessageService.parseUserInfoAndReply(message);
        }catch(Exception e){
            logger.error("process error: ", e);
        }
    }

    @Override
    public Receive createReceive(){
        ReceiveBuilder builder = ReceiveBuilder.create();
        builder.match(String.class, this::replyMessage);
        //        builder.matchEquals("task1", t -> {
        //            log.info(this.self() + " make slave do task1;" + "time: " + Calendar.getInstance().getTimeInMillis());
        //            if (this.self().path().toString().contains("MulticastMaster1")){
        //                throw new Error("Exception test!!!");
        //            }
        //            log.info("path: " + this.self().path());
        //            //multicastRouter.tell("replyTest", getSelf());
        //        });
        return builder.build();
    }
}
