package ie.joe.statemachine.repository;

import java.io.Serializable;
import org.springframework.statemachine.StateMachineContext;

public interface ContextEntity<S, E, ID extends Serializable> {

    StateMachineContext<S, E> getStateMachineContext();

    void setStateMachineContext(StateMachineContext<S, E> context);

}
