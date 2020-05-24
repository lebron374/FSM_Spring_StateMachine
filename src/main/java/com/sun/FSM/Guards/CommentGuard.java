package com.sun.FSM.Guards;

import com.alibaba.fastjson.JSON;
import com.sun.FSM.enums.ChangeEvent;
import com.sun.FSM.enums.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lebron374
 */
@Component
public class CommentGuard implements Guard<OrderStatus, ChangeEvent> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Random random = new Random();
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public boolean evaluate(StateContext<OrderStatus, ChangeEvent> context) {

        Object payLoad = context.getMessage().getPayload();
        Object messageHeader = context.getMessageHeader("order");

        boolean result;
        if (1 == atomicInteger.incrementAndGet()) {
            result = false;
        } else {
            result = true;
        }

        logger.info("CommentGuard payLoad {} messageHeader{} flag {}",
                JSON.toJSONString(payLoad), JSON.toJSONString(messageHeader), atomicInteger.get());

        return result;
    }
}
