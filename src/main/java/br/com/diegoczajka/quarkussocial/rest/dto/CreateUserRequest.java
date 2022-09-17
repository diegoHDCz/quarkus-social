package br.com.diegoczajka.quarkussocial.rest.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Data
public class CreateUserRequest {
    @NotBlank(message = "name is required")
    private String name;
    @NotNull(message = "age is required")
    private Integer age;


}
