package com.tsa.userdataaggregatorservice.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class User {
    private String id;
    private String username;
    private String name;
    private String surname;
}
