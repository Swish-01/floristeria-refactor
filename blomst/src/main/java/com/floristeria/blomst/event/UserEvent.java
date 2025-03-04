package com.floristeria.blomst.event;

import com.floristeria.blomst.entity.user.UserEntity;
import com.floristeria.blomst.enumeration.user.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class UserEvent {
    private UserEntity user;
    private EventType type;
    private Map<?, ?> data;
}
