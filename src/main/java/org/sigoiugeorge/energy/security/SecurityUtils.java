package org.sigoiugeorge.energy.security;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SecurityUtils {
    @Contract(value = " -> new", pure = true)
    public static String @NotNull [] adminAllowedGetLinks() {
        return new String[]{
                "/get/users",
                "/get/credentials",
                "/get/credentials-id/user-id={userId}",
                "/get/devices",
                "/get/devices/no-owner",
                "/get/user-id={userId}",
        };
    }

    @Contract(value = " -> new", pure = true)
    public static String @NotNull [] adminAllowedPostLinks() {
        return new String[]{
                "/add/credentials",
                "/add/user",
                "/add/device",
        };
    }

    @Contract(pure = true)
    public static String @Nullable [] adminAllowedDeleteLinks() {
        return new String[]{
                "/delete/user-id={userId}",
                "/delete/credentials-id={credentialsId}",
                "/delete/device-id={deviceId}",
        };
    }

    @Contract(pure = true)
    public static String @Nullable [] adminAllowedPutLinks() {
        return new String[]{
                "/update/user-id={userId}",
                "/update/credentials-id={credentialsId}",
                "/update/device-id={deviceId}",
                "/add/device={deviceId}-to-user={userId}",
                "/update/devices/to-user={userId}",
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

    public static String[] commonAllowedGetLinks() {
        return new String[]{
                "/get/devices-for-user/user-id={userId}",
                "/get/user-username={username}",
        };
    }
}
