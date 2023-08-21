package ai.miko.assignment.repository;

import ai.miko.assignment.model.QueryStatus;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisRepository extends ListCrudRepository<QueryStatus, String> {
}
