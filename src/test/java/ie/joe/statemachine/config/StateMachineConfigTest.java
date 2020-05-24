package ie.joe.statemachine.config;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ie.joe.statemachine.domain.OrderEvent;
import ie.joe.statemachine.domain.OrderState;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

@SpringBootTest
@Log4j2
class StateMachineConfigTest {

  @Autowired
  private StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;

  @Test
  public void testNewPaymentState(){
    StateMachine<OrderState, OrderEvent> sm = stateMachineFactory.getStateMachine();
    sm.start();
    assertEquals(OrderState.OPENED,sm.getState().getId());
  }

}
