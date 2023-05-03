package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    @NotBlank
    @Size(min = 1)
    private String name;

    private String description;

    @NotNull
    private Long taskStatusId;

    private Set<Long> labelIds;

    private Long executorId;
}
