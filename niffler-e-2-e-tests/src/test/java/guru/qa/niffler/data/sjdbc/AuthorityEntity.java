package guru.qa.niffler.data.sjdbc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityEntity {
    private UUID id;
    @JsonProperty("user_id")
    private UUID userId;
    private String authority;
}