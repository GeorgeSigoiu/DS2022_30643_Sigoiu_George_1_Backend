package org.sigoiugeorge.energy.security;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class SecurityUtils {
    @Contract(value = " -> new", pure = true)
    public static String @NotNull [] adminAllowedGetLinks() {
        return new String[]{
                "/get/users",
                "/get/credentials",
        };
    }

    @Contract(value = " -> new", pure = true)
    public static String @NotNull [] adminAllowedPostLinks() {
        return new String[]{
                "/add/credentials",
                "/add/user",
        };
    }

    @Contract(value = " -> new", pure = true)
    public static String @NotNull [] clientAllowedGetLinks() {
        return new String[]{

        };
    }

    @Contract(value = " -> new", pure = true)
    public static String @NotNull [] clientAllowedPostLinks() {
        return new String[]{

        };
    }
}
