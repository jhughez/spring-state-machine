package ie.joe.statemachine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ie.joe.statemachine.domain.OrderEvent;
import ie.joe.statemachine.domain.OrderState;
import ie.joe.statemachine.repository.ContextEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.statemachine.StateMachineContext;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OrderEntity implements ContextEntity<OrderState, OrderEvent, Long> {

  @Id
  @GeneratedValue
  private Long id;

  @Getter
  @JsonIgnore
  StateMachineContext<OrderState, OrderEvent> stateMachineContext; // NOSONAR

  @Override
  public void setStateMachineContext(@NonNull StateMachineContext<OrderState, OrderEvent> stateMachineContext) {
    this.subStates = new ArrayList<>();
    this.state = stateMachineContext.getState();
    this.stateMachineContext = stateMachineContext;
    this.stateMachineContext.getChilds().forEach(s -> this.subStates.add(s.getState()));
  }

  @Enumerated(EnumType.STRING)
  private OrderState state;

  @ElementCollection
  @CollectionTable(name = "sub_state",
      joinColumns = {@JoinColumn(name = "order_id", referencedColumnName = "id")})
  @Column(name = "state")
  @Enumerated(EnumType.STRING)
  private List<OrderState> subStates;

  private BigDecimal amount;
}
