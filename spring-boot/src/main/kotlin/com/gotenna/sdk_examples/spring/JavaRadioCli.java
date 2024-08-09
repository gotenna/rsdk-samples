package com.gotenna.sdk_examples.spring;


import com.gotenna.common.models.EncryptionParameters;
import com.gotenna.common.models.GotennaHeaderWrapper;
import com.gotenna.radio.sdk.GotennaClient;
import org.jetbrains.annotations.NotNull;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;


public class JavaRadioCli {

    public JavaRadioCli() {
        Function3<byte[], EncryptionParameters, Long, byte[]> preProcessAction = (bytes, encryptionParams, senderGid) -> {
            // encrypt outgoing messages
            /*
                encryptionParams?.keyUuid
                encryptionParams?.iv
            */
            return bytes; // return the processed bytes
        };

        Function2<byte[], GotennaHeaderWrapper, byte[]> postProcessAction = (bytes, header) -> {
            // decrypt incoming messages
            /*
                header.encryptionParameters?.keyUuid
                header.encryptionParameters?.iv
            */
            return bytes; // return the processed bytes
        };

        Continuation<Boolean> callback = new Continuation<>() {
            final CoroutineContext context = EmptyCoroutineContext.INSTANCE;

            @NotNull
            @Override
            public CoroutineContext getContext() {
                return context;
            }

            @Override
            public void resumeWith(@NotNull Object o) {
                // handle the result
            }
        };

        GotennaClient.INSTANCE.initialize(
            "sdkToken",
            preProcessAction,
            postProcessAction,
            false,
            callback
        );
    }

}