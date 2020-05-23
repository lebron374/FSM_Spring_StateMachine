package com.sun.FSM.Guards;

import com.alibaba.fastjson.JSON;
import com.sun.FSM.enums.ChangeEvent;
import com.sun.FSM.enums.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

/**
 * @author lebron374
 */
@Component
public class DeliveryGuard implements Guard<OrderStatus, ChangeEvent> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean evaluate(StateContext<OrderStatus, ChangeEvent> context) {

        Object payLoad = context.getMessage().getPayload();
        Object messageHeader = context.getMessageHeader("order");

        logger.info("DeliveryGuard payLoad {} messageHeader{}", JSON.toJSONString(payLoad), JSON.toJSONString(messageHeader));

        return true;
    }
}
