package com.sun.FSM;

import com.sun.FSM.Guards.CommentGuard;
import com.sun.FSM.Guards.DeliveryGuard;
import com.sun.FSM.Guards.PayedGuard;
import com.sun.FSM.Guards.ReceivedGuard;
import com.sun.FSM.actions.CommentedAction;
import com.sun.FSM.actions.DeliveryAction;
import com.sun.FSM.actions.PayedAction;
import com.sun.FSM.actions.ReceivedAction;
import com.sun.FSM.actions.UncommentedAction;
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
@EnableStateMachine(name = "stateMachine")
public class OrderStateMachineConfig extends EnumStateMachineConfigurerAdapter<OrderStatus, ChangeEvent> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private OrderStateListenerImpl orderStateListener;

    @Resource
    private PayedAction payedAction;

    @Resource
    private DeliveryAction deliveryAction;

    @Resource
    private ReceivedAction receivedAction;

    @Resource
    private CommentedAction commentedAction;

    @Resource
    private UncommentedAction uncommentedAction;

    @Resource
    private DeliveryGuard deliveryGuard;

    @Resource
    private PayedGuard payedGuard;

    @Resource
    private ReceivedGuard receivedGuard;

    @Resource
    private CommentGuard commentGuard;


    @Override
    public void configure(StateMachineStateConfigurer<OrderStatus, ChangeEvent> states) throws Exception {

        states.withStates()
                // 设置初始化状态
                .initial(OrderStatus.WAIT_PAYMENT)
                .choice(OrderStatus.FINISH)
                // 全部状态
                .states(EnumSet.allOf(OrderStatus.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatus, ChangeEvent> transitions) throws Exception {

        // 1、withExternal 是当source和target不同时的写法
        // 2、withInternal 当source和target相同时的串联写法，比如付款失败时，付款前及付款后都是待付款状态
        // 3、withChoice 当执行一个动作，可能导致多种结果时，可以选择使用choice+guard来跳转
        transitions
                // 通过PAYED 实现由 WAIT_PAYMENT => WAIT_DELIVER 状态转移
                .withExternal()
                    .source(OrderStatus.WAIT_PAYMENT)
                    .target(OrderStatus.WAIT_DELIVER)
                    .event(ChangeEvent.PAYED)
                    .guard(payedGuard)
                    .action(payedAction)
                .and()
                // 通过DELIVERY 实现由 WAIT_DELIVER => WAIT_RECEIVE 状态转移
                .withExternal()
                    .source(OrderStatus.WAIT_DELIVER)
                    .target(OrderStatus.WAIT_RECEIVE)
                    .event(ChangeEvent.DELIVERY)
                    .guard(deliveryGuard)
                    .action(deliveryAction)
                .and()
                // 通过RECEIVED 实现由 WAIT_RECEIVE => FINISH 状态转移
                .withExternal()
                    .source(OrderStatus.WAIT_RECEIVE)
                    .target(OrderStatus.FINISH)
                    .event(ChangeEvent.RECEIVED)
                    .guard(receivedGuard)
                    .action(receivedAction)
                .and()
                // 通过COMMENT 实现由 FINISH => COMMENTED状态转移
                .withChoice()
                    .source(OrderStatus.FINISH)
                    .first(OrderStatus.COMMENTED, commentGuard, commentedAction)
                    .then(OrderStatus.UNCOMMENTED, commentGuard, uncommentedAction)
                    .last(OrderStatus.WAIT_COMMENT);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStatus, ChangeEvent> config) throws Exception {
        config.withConfiguration().listener(orderStateListener);
    }
}
