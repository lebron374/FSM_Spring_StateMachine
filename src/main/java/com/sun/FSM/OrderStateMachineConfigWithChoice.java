package com.sun.FSM;

import com.sun.FSM.Guards.DeliveryFailedChoiceGuard;
import com.sun.FSM.Guards.DeliveryGuard;
import com.sun.FSM.Guards.DeliverySuccessGuard;
import com.sun.FSM.Guards.PayedFailedChoiceGuard;
import com.sun.FSM.Guards.PayedGuard;
import com.sun.FSM.Guards.PayedSuccessChoiceGuard;
import com.sun.FSM.Guards.ReceivedFailedChoiceGuard;
import com.sun.FSM.Guards.ReceivedGuard;
import com.sun.FSM.Guards.ReceivedSuccessChoiceGuard;
import com.sun.FSM.actions.DeliveryAction;
import com.sun.FSM.actions.DeliveryFailAction;
import com.sun.FSM.actions.DeliverySuccessAction;
import com.sun.FSM.actions.PayedAction;
import com.sun.FSM.actions.PayedFailAction;
import com.sun.FSM.actions.PayedSuccessAction;
import com.sun.FSM.actions.ReceivedAction;
import com.sun.FSM.actions.ReceivedFailAction;
import com.sun.FSM.actions.ReceivedSuccessAction;
import com.sun.FSM.enums.ChangeEvent;
import com.sun.FSM.enums.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import javax.annotation.Resource;
import java.util.EnumSet;

/**
 * @author lebron374
 */

@Configuration
@EnableStateMachine(name = "stateMachineWithChoice")
public class OrderStateMachineConfigWithChoice extends EnumStateMachineConfigurerAdapter<OrderStatus, ChangeEvent> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private OrderStateListenerImpl orderStateListener;

    @Resource
    private PayedSuccessAction payedSuccessAction;

    @Resource
    private PayedFailAction payedFailAction;

    @Resource
    private DeliverySuccessAction deliverySuccessAction;

    @Resource
    private DeliveryFailAction deliveryFailAction;

    @Resource
    private ReceivedSuccessAction receivedSuccessAction;

    @Resource
    private ReceivedFailAction receivedFailAction;

    @Resource
    private PayedSuccessChoiceGuard payedSuccessChoiceGuard;

    @Resource
    private PayedFailedChoiceGuard payedFailedChoiceGuard;

    @Resource
    private DeliveryFailedChoiceGuard deliveryFailedChoiceGuard;

    @Resource
    private DeliverySuccessGuard deliverySuccessGuard;

    @Resource
    private ReceivedFailedChoiceGuard receivedFailedChoiceGuard;

    @Resource
    private ReceivedSuccessChoiceGuard receivedSuccessChoiceGuard;

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatus, ChangeEvent> states) throws Exception {

        states.withStates()
                // 设置初始化状态
                .initial(OrderStatus.WAIT_PAYMENT)
                .choice(OrderStatus.WAIT_PAYMENT)
                .end(OrderStatus.FINISH)
                // 全部状态
                .states(EnumSet.allOf(OrderStatus.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatus, ChangeEvent> transitions) throws Exception {

        // 1、withExternal 是当source和target不同时的写法
        // 2、withInternal 当source和target相同时的串联写法，比如付款失败时，付款前及付款后都是待付款状态
        // 3、withChoice 当执行一个动作，可能导致多种结果时，可以选择使用choice+guard来跳转
        transitions
                .withChoice()
                    .source(OrderStatus.WAIT_PAYMENT)
                    .first(OrderStatus.WAIT_DELIVER, payedSuccessChoiceGuard, payedSuccessAction)
                    .then(OrderStatus.WAIT_PAYMENT, payedFailedChoiceGuard, payedFailAction)
                    .last(OrderStatus.WAIT_DELIVER)
                .and()
                // 通过DELIVERY 实现由 WAIT_DELIVER => WAIT_RECEIVE 状态转移
                .withChoice()
                    .source(OrderStatus.WAIT_DELIVER)
                    .first(OrderStatus.WAIT_RECEIVE, deliverySuccessGuard, deliverySuccessAction)
                    .then(OrderStatus.WAIT_DELIVER, deliveryFailedChoiceGuard, deliveryFailAction)
                    .last(OrderStatus.WAIT_RECEIVE)
                .and()
                // 通过RECEIVED 实现由 WAIT_RECEIVE => FINISH 状态转移
                .withChoice()
                    .source(OrderStatus.WAIT_RECEIVE)
                    .first(OrderStatus.FINISH, receivedSuccessChoiceGuard, receivedSuccessAction)
                    .then(OrderStatus.WAIT_RECEIVE, receivedFailedChoiceGuard, receivedFailAction)
                    .last(OrderStatus.FINISH);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStatus, ChangeEvent> config) throws Exception {
        config.withConfiguration().listener(orderStateListener);
    }


}
