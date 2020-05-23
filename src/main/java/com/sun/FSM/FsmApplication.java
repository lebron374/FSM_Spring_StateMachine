package com.sun.FSM;

import com.sun.FSM.enums.OrderStatus;
import com.sun.FSM.enums.ChangeEvent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;

import javax.annotation.Resource;

@SpringBootApplication
public class FsmApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(FsmApplication.class, args);
	}

	@Resource
	private StateMachine<OrderStatus, ChangeEvent> stateMachineWithoutChoice;

	@Resource
	private StateMachine<OrderStatus, ChangeEvent> stateMachineWithChoice;

	@Override
	public void run(String... args) throws Exception {
        stateMachineWithoutChoice.start();
        stateMachineWithChoice.start();

		// 测试状态机消息变更
        // messageTransferV1();

        // 测试状态机choice变更
        messageTransferV2();

        stateMachineWithoutChoice.stop();
        stateMachineWithChoice.stop();
	}

	private void messageTransferV1() {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOid(123456789L);
        orderInfo.setDesc("test order");

        Message<ChangeEvent> message = null;

        // spring message 的payload设置为消息事件、header为额外需要带的参数
        message = MessageBuilder.withPayload(ChangeEvent.PAYED).setHeader("order", orderInfo).build();
        stateMachineWithoutChoice.sendEvent(message);

        message = MessageBuilder.withPayload(ChangeEvent.DELIVERY).setHeader("order", orderInfo).build();
        stateMachineWithoutChoice.sendEvent(message);

        message = MessageBuilder.withPayload(ChangeEvent.RECEIVED).setHeader("order", orderInfo).build();
        stateMachineWithoutChoice.sendEvent(message);
    }

    private void messageTransferV2() {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOid(987654321L);
        orderInfo.setDesc("choice order");

        Message<ChangeEvent> message = null;

        // spring message 的payload设置为消息事件、header为额外需要带的参数
        message = MessageBuilder.withPayload(ChangeEvent.PAYED).setHeader("order", orderInfo).build();
        stateMachineWithChoice.sendEvent(message);

        message = MessageBuilder.withPayload(ChangeEvent.DELIVERY).setHeader("order", orderInfo).build();
        stateMachineWithChoice.sendEvent(message);

        message = MessageBuilder.withPayload(ChangeEvent.RECEIVED).setHeader("order", orderInfo).build();
        stateMachineWithChoice.sendEvent(message);
    }
}
