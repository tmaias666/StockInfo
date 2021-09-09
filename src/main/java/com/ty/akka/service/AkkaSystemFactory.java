package com.ty.akka.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.japi.Function;
import akka.routing.RoundRobinPool;
import akka.routing.SmallestMailboxPool;
import scala.concurrent.duration.Duration;

public class AkkaSystemFactory<T>{

    private static final Logger logger = LoggerFactory.getLogger(AkkaSystemFactory.class);

    private static final int defaultPoolSize = 5;
    //private static final ActorSystem system = ActorSystem.create("AkkaService");

    public AkkaSystemFactory(List<ActorRef> masters, Class<T> targetClass, String systemName, String masterName){
        Integer divideSystemSize = 3;
        this.createListSystem(divideSystemSize, masters, targetClass, masterName, systemName);
    }

    public AkkaSystemFactory(List<ActorRef> childs, Class<T> targetClass, String childName){
        Integer size = 1;
        this.createChild(size, childs, targetClass, childName);
    }

    private void createListSystem(int size, List<ActorRef> masters, Class<T> targetClass, String masterName, String systemName){
        ActorSystem system = ActorSystem.create(systemName + masterName);
        logger.info("create system name: " + systemName);
        for(int i = 0; i < size; i++){
            masters.add(system.actorOf(new RoundRobinPool(defaultPoolSize).withSupervisorStrategy(new OneForOneStrategy(-1, Duration.Inf(), new Function<Throwable, Directive>(){

                @Override
                public Directive apply(Throwable throwable) throws Exception{
                    logger.error(throwable.getMessage());
                    if (throwable instanceof Error){
                        return OneForOneStrategy.escalate();
                    }else{
                        return OneForOneStrategy.restart();
                    }
                }
            })).props(Props.create(targetClass)), masterName + i));
        }
        logger.info(masters.toString());
    }

    private void createChild(int size, List<ActorRef> childs, Class<T> targetClass, String masterName){
        ActorSystem system = ActorSystem.create("AkkaService");
        for(int i = 0; i < size; i++){
            childs.add(system.actorOf(new RoundRobinPool(2).withSupervisorStrategy(new OneForOneStrategy(-1, Duration.Inf(), new Function<Throwable, Directive>(){

                @Override
                public Directive apply(Throwable throwable) throws Exception{
                    logger.error(throwable.getMessage());
                    if (throwable instanceof Error){
                        return OneForOneStrategy.escalate();
                    }else{
                        return OneForOneStrategy.restart();
                    }
                }
            })).props(Props.create(targetClass)), masterName));
        }
        logger.info(childs.toString());
    }
}
