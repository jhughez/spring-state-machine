package ie.joe.statemachine.controller;

import ie.joe.statemachine.constant.ApiVersion;
import ie.joe.statemachine.model.OrderEntity;
import ie.joe.statemachine.domain.OrderEvent;
import ie.joe.statemachine.service.StateMachineService;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/" + ApiVersion.CURRENT + "/events")
@Log4j2
public class EventController {

  private final StateMachineService orderService;

  public EventController(StateMachineService orderService) {
    this.orderService = orderService;
  }

  @GetMapping
  public ResponseEntity<List<OrderEvent>> findAll(){
    return ResponseEntity.ok(
        Arrays.asList(OrderEvent.values())
    );
  }

  @PostMapping("newOrder")
  public ResponseEntity<OrderEntity> newOrder() {
    OrderEntity savedOrder = orderService.startStateMachine(OrderEntity.builder().amount(new BigDecimal("12.99")).build());
    return ResponseEntity.ok(savedOrder);
  }

  @PostMapping("/{id}/{event}")
  public ResponseEntity sendEvent(@PathVariable Long id, @PathVariable OrderEvent event) {
    return ResponseEntity.ok(orderService.sendEvent(id, event));
  }
}
