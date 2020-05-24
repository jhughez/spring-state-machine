package ie.joe.statemachine.converter.web;

import ie.joe.statemachine.domain.OrderEvent;
import org.springframework.core.convert.converter.Converter;

public class StringToOrderEvent implements Converter<String, OrderEvent> {
  @Override
  public OrderEvent convert(String source) {
    return OrderEvent.valueOf(source.toUpperCase());
  }
}
