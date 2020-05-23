package com.sun.FSM;

import com.sun.FSM.enums.OrderStatus;
import com.sun.FSM.enums.ChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

/**
 * @author lebron374
 */

@Component
public class OrderStateListenerImpl extends StateMachineListenerAdapter<OrderStatus, ChangeEvent> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void stateChanged(State<OrderStatus, ChangeEvent> from, State<OrderStatus, ChangeEvent> to) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("stateChanged");
        stringBuilder.append(" from " + (null != from ? from.getId().name() : null));
        stringBuilder.append(" to " + (null != to ? to.getId().name() : null));

        logger.info(stringBuilder.toString());
    }

    @Override
    public void transition(Transition<OrderStatus, ChangeEvent> transition) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("transition");

        stringBuilder.append(" kind " + (null != transition.getKind() ? transition.getKind().name() : null));

        stringBuilder.append(" from " + (null != transition.getSource() ? transition.getSource().getId().name() : null));

        stringBuilder.append(" to " + (null != transition.getTarget() ? transition.getTarget().getId().name() : null));

        stringBuilder.append(" trigger " + (null != transition.getTrigger() ? transition.getTrigger().getEvent().name() : null));

        logger.info(stringBuilder.toString());
    }
}
