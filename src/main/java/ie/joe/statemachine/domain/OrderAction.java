package ie.joe.statemachine.domain;

import ie.joe.statemachine.model.OrderEntity;
import ie.joe.statemachine.model.TestEntity;
import ie.joe.statemachine.repository.TestRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;

@Configuration
@Log4j2
public class OrderAction {
  @Bean
  public Action<OrderState, OrderEvent> testAction(TestRepository testRepository){
    return context -> {
      log.debug("testAction was called !!!!");
      OrderEntity order = context.getExtendedState().get("order", OrderEntity.class);
      TestEntity test = TestEntity.builder().state(order.getState().name()).build();
      testRepository.save(test);
    };
  }
}
