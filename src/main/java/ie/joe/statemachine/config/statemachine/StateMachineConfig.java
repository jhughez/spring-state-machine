package ie.joe.statemachine.config.statemachine;

import ie.joe.statemachine.domain.OrderEvent;
import ie.joe.statemachine.domain.OrderState;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Log4j2
@EnableStateMachineFactory
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<OrderState, OrderEvent> {

  private final Action<OrderState, OrderEvent> testAction;
  private final Guard<OrderState, OrderEvent> testGuard;

  public StateMachineConfig(
      Action<OrderState, OrderEvent> testAction,
      Guard<OrderState, OrderEvent> testGuard) {
    this.testAction = testAction;
    this.testGuard = testGuard;
  }

  @Override
  public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states) throws Exception {
    states.withStates().initial(OrderState.OPENED)
        .region("MAIN")
        .state(OrderState.IN_PROGRESS)
          .fork(OrderState.PROCESSING_ORDER_FORK)
        .join(OrderState.PROCESSING_ORDER_JOIN)
          .state(OrderState.PROCESSING_ORDER)
        .state(OrderState.PROCESSING_ORDER_JOIN)
          .state(OrderState.AWAITING_DISPATCH_APPROVAL)
          .end(OrderState.COMPLETE)
        .and()
        .withStates().parent(OrderState.PROCESSING_ORDER)
        .region("SHIPPING")
        .initial(OrderState.AWAIT_FILLING)
        .state(OrderState.READY_FOR_SHIPMENT)
        .end(OrderState.SENT_TO_DISPATCH)
        .and()
        .withStates().parent(OrderState.PROCESSING_ORDER)
        .region("INVOICING")
        .initial(OrderState.READY_FOR_INVOICE)
          .state(OrderState.AWAITING_PAYMENT)
        .end(OrderState.PAYMENT_RECEIVED);
  }

  @Override
  public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
    transitions.withExternal()
        .source(OrderState.OPENED)
        .target(OrderState.IN_PROGRESS)
          .event(OrderEvent.START_ORDER).guard(testGuard).action(testAction)
        .and()
        .withExternal()
        .source(OrderState.IN_PROGRESS)
          .target(OrderState.PROCESSING_ORDER_FORK)
          .event(OrderEvent.SUBMIT_ORDER).action(testAction)
        .and().withFork()
          .source(OrderState.PROCESSING_ORDER_FORK)
        .target(OrderState.PROCESSING_ORDER)
        .and().withExternal()
        .source(OrderState.AWAIT_FILLING)
        .target(OrderState.READY_FOR_SHIPMENT)
        .event(OrderEvent.FILL_ORDER)
        .and()
        .withExternal()
        .source(OrderState.READY_FOR_SHIPMENT)
        .target(OrderState.SENT_TO_DISPATCH)
        .event(OrderEvent.SEND_TO_DISPATCH)
        .and().withExternal()
        .source(OrderState.READY_FOR_INVOICE)
        .target(OrderState.AWAITING_PAYMENT)
        .event(OrderEvent.SEND_INVOICE)
        .and()
        .withExternal()
          .source(OrderState.AWAITING_PAYMENT)
        .target(OrderState.PAYMENT_RECEIVED)
        .event(OrderEvent.RECEIVE_PAYMENT)
        .and()
        .withJoin()
          .source(OrderState.PROCESSING_ORDER)
        .target(OrderState.PROCESSING_ORDER_JOIN)
        .and()
        .withExternal()
        .source(OrderState.PROCESSING_ORDER_JOIN)
        .target(OrderState.AWAITING_DISPATCH_APPROVAL)
          .event(OrderEvent.MOVE_TO_AWAITING_DISPATCH_APPROVAL)
        .and().withExternal()
          .source(OrderState.AWAITING_DISPATCH_APPROVAL)
          .target(OrderState.COMPLETE)
        .event(OrderEvent.APPROVE_DISPATCH);
  }

  @Override
  public void configure(StateMachineConfigurationConfigurer<OrderState, OrderEvent> config) throws Exception {
    StateMachineListenerAdapter<OrderState, OrderEvent> listenerAdapter = new StateMachineListenerAdapter<>() {

      @Override
      public void stateChanged(State<OrderState, OrderEvent> from, State<OrderState, OrderEvent> to) {
        try {
          log.info(String.format("State changed(from: %s, to: %s)", from, to.getIds()));
        }catch(Exception e){
          log.error("Is there a problem here?", e);
        }
      }
    };

    config.withConfiguration().listener(listenerAdapter);
  }
}
