package com.stream.four.mapper;

import com.stream.four.dto.CreateUserRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    // Uses MapStruct generated implementation via Mappers in the interface.
    private final UserMapper mapper = UserMapper.INSTANCE;

    @Test
    void toEntity_setsDefaults_andIgnoresId() {
        var req = new CreateUserRequest();
        req.setName("n");
        req.setEmail("e@e.com");
        req.setPassword("pw");

        var entity = mapper.toEntity(req);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(0, entity.getFailedLoginAttempts());
        assertFalse(entity.isAccountLocked());
        assertEquals("n", entity.getName());
        assertEquals("e@e.com", entity.getEmail());
        assertEquals("pw", entity.getPassword());
    }
}

