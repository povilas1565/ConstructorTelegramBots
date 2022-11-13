package keldkemp.telegram.util;

import java.util.Random;
import java.util.UUID;

public class UuidUtils {

    private static final Random RANDOM_INSTANCE = new Random();

    public static UUID notSecureRandomUuid() {
        return new UUID(RANDOM_INSTANCE.nextLong(), RANDOM_INSTANCE.nextLong());
    }
}
