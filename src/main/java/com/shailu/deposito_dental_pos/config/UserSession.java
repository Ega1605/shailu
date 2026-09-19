package com.shailu.deposito_dental_pos.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class UserSession {
    private String username;
}
