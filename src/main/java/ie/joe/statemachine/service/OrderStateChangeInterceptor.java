package ie.joe.statemachine.service;

import ie.joe.statemachine.model.OrderEntity;
import ie.joe.statemachine.domain.OrderEvent;
import ie.joe.statemachine.domain.OrderState;
import ie.joe.statemachine.repository.OrderRepository;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class OrderStateChangeInterceptor extends StateMachineInterceptorAdapter<OrderState, OrderEvent> {

  private final OrderRepository orderRepository;

  public OrderStateChangeInterceptor(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Override
  public void postStateChange(State<OrderState, OrderEvent> state, Message<OrderEvent> message,
                              Transition<OrderState, OrderEvent> transition, StateMachine<OrderState, OrderEvent> stateMachine,
                              StateMachine<OrderState, OrderEvent> rootStateMachine) {
    Optional.ofNullable(message).ifPresent(msg ->
        Optional.ofNullable(Long.class.cast(msg.getHeaders().getOrDefault(StateMachineService.PAYMENT_ID_HEADER, -1)))
            .ifPresent(orderId -> {
              OrderEntity order = orderRepository.getOne(orderId);
              if (order != null){
                log.info("Case state: " + order.getState() + ", Sub states: " + order.getSubStates());
              }
              log.info("*************** State :" + rootStateMachine.getStates() + " *********************");
            }));

  }
}
