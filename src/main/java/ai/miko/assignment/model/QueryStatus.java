package ai.miko.assignment.model;

import ai.miko.assignment.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash("QueryStatus")
@Builder
@NoArgsConstructor
@Data
public class QueryStatus implements Serializable {
    @Id
    String query;
    Status status;
    LocalDateTime ranOn;

    public QueryStatus(String query, Status status, LocalDateTime ranOn) {
        this.query = query;
        this.status = status;
        if(ranOn == null)
            this.ranOn = LocalDateTime.now();
        else
            this.ranOn = ranOn;
    }
}
