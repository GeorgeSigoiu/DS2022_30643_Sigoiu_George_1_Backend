package org.sigoiugeorge.energy.utils;

import com.auth0.jwt.algorithms.Algorithm;

public class Utils {

    public static Algorithm getCreationTokenAlgorithm(){
        return Algorithm.HMAC256("secret".getBytes());
    }

}
