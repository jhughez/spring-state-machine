package ie.joe.statemachine.service;

import ie.joe.statemachine.DefaultStateMachineAdapter;
import ie.joe.statemachine.model.OrderEntity;
import ie.joe.statemachine.domain.OrderEvent;
import ie.joe.statemachine.domain.OrderState;
import ie.joe.statemachine.repository.OrderRepository;
import ie.joe.statemachine.repository.ContextEntity;
import java.io.Serializable;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.support.StateMachineInterceptor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Log4j2
public class StateMachineService {

  public static final String PAYMENT_ID_HEADER = "payment_id";

  private OrderRepository orderRepository;
  private StateMachineInterceptor<OrderState, OrderEvent> stateMachineInterceptor;
  final DefaultStateMachineAdapter<OrderState, OrderEvent, ContextEntity<OrderState, OrderEvent, ? extends Serializable>> orderStateMachineAdapter;

  public OrderEntity startStateMachine(OrderEntity order) {
    StateMachine<OrderState, OrderEvent> stateMachine = orderStateMachineAdapter.create();
    stateMachine.getExtendedState().getVariables().put("order", order);
    orderStateMachineAdapter.persist(stateMachine, order);
    return order;
  }

  @Transactional
  public OrderEntity sendEvent(Long orderId, OrderEvent event) {
    OrderEntity order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
    StateMachine<OrderState, OrderEvent> stateMachine= buildStateMachine(order);
    sendEvent(orderId, stateMachine, event);
    orderStateMachineAdapter.persist(stateMachine, order);
    return order;
  }

  private void sendEvent(Long orderId, StateMachine<OrderState, OrderEvent> stateMachine, OrderEvent event) {
    Message message = MessageBuilder.withPayload(event)
        .setHeader(PAYMENT_ID_HEADER, orderId)
        .build();
    stateMachine.sendEvent(message);
  }

  private StateMachine<OrderState, OrderEvent> buildStateMachine(OrderEntity order){
    StateMachine<OrderState, OrderEvent> stateMachine = orderStateMachineAdapter.restore(order);
    stateMachine.getExtendedState().getVariables().put("order", order);
    stateMachine.getStateMachineAccessor().withRegion().addStateMachineInterceptor(stateMachineInterceptor);
    stateMachine.start();
    return stateMachine;
  }
}
