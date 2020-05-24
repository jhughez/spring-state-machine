package ie.joe.statemachine.config.statemachine;

import ie.joe.statemachine.DefaultStateMachineAdapter;
import ie.joe.statemachine.model.OrderEntity;
import ie.joe.statemachine.domain.OrderEvent;
import ie.joe.statemachine.domain.OrderState;
import ie.joe.statemachine.repository.OrderRepository;
import ie.joe.statemachine.repository.ContextEntity;
import java.io.Serializable;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

@Configuration
public class StateMachinePersistConfig {

  private final OrderRepository orderRepository;

  public StateMachinePersistConfig(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Bean
  public StateMachinePersister<OrderState, OrderEvent, ContextEntity<OrderState, OrderEvent, Serializable>> persister(
      StateMachinePersist<OrderState, OrderEvent, ContextEntity<OrderState, OrderEvent, Serializable>> persist) {
    return new DefaultStateMachinePersister<>(persist);
  }

  @Bean
  public StateMachinePersist<OrderState, OrderEvent, ContextEntity<OrderState, OrderEvent, Serializable>> persist() {
    return new StateMachinePersist<>() {

      @Override
      public StateMachineContext<OrderState, OrderEvent> read(
          ContextEntity<OrderState, OrderEvent, Serializable> order) throws Exception {
        return order.getStateMachineContext();
      }

      @Override
      public void write(StateMachineContext<OrderState, OrderEvent> context,
          ContextEntity<OrderState, OrderEvent, Serializable> order) throws Exception {
        order.setStateMachineContext(context);
        orderRepository.save(OrderEntity.class.cast(order));
      }
    };
  }

  @Bean
  public DefaultStateMachineAdapter<OrderState, OrderEvent, ContextEntity<OrderState, OrderEvent, Serializable>> orderStateMachineAdapter(
      StateMachineFactory<OrderState, OrderEvent> stateMachineFactory,
      StateMachinePersister<OrderState, OrderEvent, ContextEntity<OrderState, OrderEvent, Serializable>> persister) {
    return new DefaultStateMachineAdapter<>(stateMachineFactory, persister);
  }

}
