package ie.joe.statemachine.domain;

import ie.joe.statemachine.model.OrderEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.guard.Guard;

@Configuration
@Log4j2
public class OrderGuard {
  @Bean
  public Guard<OrderState, OrderEvent> testGuard(){
    return context -> {
        boolean returnVal = false;
        OrderEntity order = context.getExtendedState().get("order", OrderEntity.class);
        if (order == null){
          log.debug("*************************** null ********************");
          returnVal = false;
        } else if (order.getId() == 1) {
          log.debug("*************************** success ********************");
          returnVal = true;
        } else {
          log.debug("*************************** else ********************");
          returnVal = false;
        }
      return returnVal;
    };
  }
}
