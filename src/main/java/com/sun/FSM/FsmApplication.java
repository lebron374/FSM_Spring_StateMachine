package com.sun.FSM;

import com.sun.FSM.enums.OrderStatus;
import com.sun.FSM.enums.OrderStatusChangeEvent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;

import javax.annotation.Resource;

@SpringBootApplication
public class FsmApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(FsmApplication.class, args);
	}

	@Resource
	private StateMachine<OrderStatus, OrderStatusChangeEvent> stateMachine;

	@Override
	public void run(String... args) throws Exception {
		stateMachine.start();
		stateMachine.sendEvent(OrderStatusChangeEvent.PAYED);
		stateMachine.sendEvent(OrderStatusChangeEvent.DELIVERY);
		stateMachine.sendEvent(OrderStatusChangeEvent.RECEIVED);
	}
}
