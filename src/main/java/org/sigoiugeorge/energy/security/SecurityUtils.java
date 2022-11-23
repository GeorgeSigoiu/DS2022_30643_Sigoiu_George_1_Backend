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
                "/verify/unique/username",
        };
    }

    @Contract(value = " -> new", pure = true)
    public static String @NotNull [] adminAllowedPostLinks() {
        return new String[]{
                "/add/credentials",
                "/add/user",
                "/add/device",
                "/verify/unique/device-address",
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

    @Contract(value = " -> new", pure = true)
    public static String @NotNull [] commonAllowedGetLinks() {
        return new String[]{
                "/get/devices-for-user/user-id={userId}",
                "/get/user-username={username}",
                "/get/consumption/for-date={date}/device-id={deviceId}",
                "/get/devices/consumption-exceeded-limit/username={username}",
        };
    }

    @Contract(value = " -> new", pure = true)
    public static String @NotNull [] commonAllowedPostLinks() {
        return new String[]{
                "/update/credentials-id={credentialsId}",
        };
    }
}
