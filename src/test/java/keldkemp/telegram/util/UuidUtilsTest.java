package keldkemp.telegram.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.UUID;

@SpringJUnitConfig
@ContextConfiguration(classes = {UuidUtils.class})
public class UuidUtilsTest {

    @Test
    public void testRandomUuid() {
        UUID uuid1 = UuidUtils.notSecureRandomUuid();
        UUID uuid2 = UuidUtils.notSecureRandomUuid();
        Assertions.assertNotEquals(uuid1, uuid2);
    }
}
