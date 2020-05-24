package ie.joe.statemachine.repository;

import ie.joe.statemachine.model.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<TestEntity, Long> {

}
