package com.sun.FSM.actions;

import com.sun.FSM.enums.ChangeEvent;
import com.sun.FSM.enums.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

/**
 * @author lebron374
 */

@Component
public class PayedAction implements Action<OrderStatus, ChangeEvent> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(StateContext<OrderStatus, ChangeEvent> context) {

        logger.info("PayedAction execute");
    }
}
