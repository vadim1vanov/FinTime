package com.fintime.fintime.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    @NonNull
    Long id;
    @NonNull
    @JsonProperty("first_name")
    String firstName;
    @NonNull
    @JsonProperty("last_name")
    String lastName;
    @NonNull
    String username;
    @NonNull
    String password;
    Long countAccounts;
}
