package com.sun.FSM;

import com.sun.FSM.enums.OrderStatus;
import com.sun.FSM.enums.ChangeEvent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;

import javax.annotation.Resource;

/**
 * @author lebron374
 */
@SpringBootApplication
public class FsmApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(FsmApplication.class, args);
	}

	@Resource
	private StateMachine<OrderStatus, ChangeEvent> stateMachine;

	@Override
	public void run(String... args) throws Exception {
        stateMachine.start();

		// 测试状态机消息变更
        messageTransfer();

        stateMachine.stop();
	}

	private void messageTransfer() {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOid(123456789L);
        orderInfo.setDesc("test order");

        Message<ChangeEvent> message = null;

        // spring message 的payload设置为消息事件、header为额外需要带的参数
        message = MessageBuilder.withPayload(ChangeEvent.PAYED).setHeader("order", orderInfo).build();
        stateMachine.sendEvent(message);

        message = MessageBuilder.withPayload(ChangeEvent.DELIVERY).setHeader("order", orderInfo).build();
        stateMachine.sendEvent(message);

        message = MessageBuilder.withPayload(ChangeEvent.RECEIVED).setHeader("order", orderInfo).build();
        stateMachine.sendEvent(message);

        message = MessageBuilder.withPayload(ChangeEvent.COMMENT).setHeader("order", orderInfo).build();
        stateMachine.sendEvent(message);

    }
}
