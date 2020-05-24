package ie.joe.statemachine.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ie.joe.statemachine.domain.OrderEvent;
import ie.joe.statemachine.domain.OrderState;
import ie.joe.statemachine.model.OrderEntity;
import ie.joe.statemachine.repository.OrderRepository;
import java.math.BigDecimal;
import javax.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
class OrderServiceImplTest {

  @Autowired
  private StateMachineService orderService;

  @Autowired
  private OrderRepository orderRepository;

  private OrderEntity newOrder;
  private long orderId;

  @BeforeEach
  void setUp() {
    newOrder = OrderEntity.builder().amount(new BigDecimal("12.99")).build();
  }

  @Transactional
  @Test
  void fullStateMachineTest() {
    {
      OrderEntity order = orderService.startStateMachine(newOrder);
      orderId = newOrder.getId();
      assertEquals(OrderState.OPENED, order.getState());
      log.info("Order Opened: " + order.getState() + ", Sub states: " + order.getSubStates());

    }
    {
      OrderEntity order = orderService.sendEvent(orderId, OrderEvent.START_ORDER);
      assertEquals(OrderState.IN_PROGRESS, order.getState());
      log.info("Order In Progress: " + order.getState() + ", Sub states: " + order.getSubStates());
    }
    {
      OrderEntity order = orderService.sendEvent(orderId, OrderEvent.SUBMIT_ORDER);
      assertEquals(OrderState.PROCESSING_ORDER, order.getState());
      assertEquals(OrderState.AWAIT_FILLING, order.getSubStates().get(0));
      assertEquals(OrderState.READY_FOR_INVOICE, order.getSubStates().get(1));
      log.info("Order Processing: " + order.getState() + "Sub states: " + order.getSubStates());
    }
    {
      OrderEntity order = orderService.sendEvent(orderId, OrderEvent.SEND_INVOICE);
      assertEquals(OrderState.PROCESSING_ORDER, order.getState());
      assertEquals(OrderState.AWAIT_FILLING, order.getSubStates().get(0));
      assertEquals(OrderState.AWAITING_PAYMENT, order.getSubStates().get(1));
      log.info("Order Processing state: " + order.getState() + ", Sub states: " + order.getSubStates());
    }

    {
      OrderEntity order = orderService.sendEvent(orderId, OrderEvent.RECEIVE_PAYMENT);
      assertEquals(OrderState.PROCESSING_ORDER, order.getState());
      assertEquals(OrderState.AWAIT_FILLING, order.getSubStates().get(0));
      assertEquals(OrderState.PAYMENT_RECEIVED, order.getSubStates().get(1));
      log.info("Order Processing state: " + order.getState() + ", Sub states: " + order.getSubStates());
    }

    {
      OrderEntity order = orderService.sendEvent(orderId, OrderEvent.FILL_ORDER);
      assertEquals(OrderState.PROCESSING_ORDER, order.getState());
      assertEquals(OrderState.READY_FOR_SHIPMENT, order.getSubStates().get(0));
      assertEquals(OrderState.PAYMENT_RECEIVED, order.getSubStates().get(1));
      log.info("Order Processingstate: " + order.getState() + ", Sub states: " + order.getSubStates());
    }

    {
      OrderEntity order = orderService.sendEvent(orderId, OrderEvent.SEND_TO_DISPATCH);
      assertEquals(OrderState.AWAITING_DISPATCH_APPROVAL, order.getState());
      log.info("Awaiting Dispatch Approval state: " + order.getState() + ", Sub states: " + order.getSubStates());
    }

    {
      OrderEntity order = orderService.sendEvent(orderId, OrderEvent.APPROVE_DISPATCH);
      assertEquals(OrderState.COMPLETE, order.getState());
      log.info("Completed state: " + order.getState() + ", Sub states: " + order.getSubStates());
    }
  }
}
