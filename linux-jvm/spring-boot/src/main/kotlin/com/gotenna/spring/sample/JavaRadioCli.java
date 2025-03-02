package com.gotenna.spring.sample;


import com.gotenna.radio.sdk.GotennaClient;
import com.gotenna.radio.sdk.GotennaClientJvmExtKt;
import com.gotenna.radio.sdk.common.models.CommandMetaData;
import com.gotenna.radio.sdk.common.models.ConnectionType;
import com.gotenna.radio.sdk.common.models.DeliveryResult;
import com.gotenna.radio.sdk.common.models.EncryptionParameters;
import com.gotenna.radio.sdk.common.models.GTMessagePriority;
import com.gotenna.radio.sdk.common.models.GTMessageType;
import com.gotenna.radio.sdk.common.models.GotennaHeaderWrapper;
import com.gotenna.radio.sdk.common.models.GripResult;
import com.gotenna.radio.sdk.common.models.MessageTypeWrapper;
import com.gotenna.radio.sdk.common.models.RadioCommand;
import com.gotenna.radio.sdk.common.models.RadioModel;
import com.gotenna.radio.sdk.common.models.RadioModelJvmExtKt;
import com.gotenna.radio.sdk.common.models.RadioState;
import com.gotenna.radio.sdk.common.models.SendToNetwork;
import com.gotenna.radio.sdk.utils.RadioResult;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import kotlin.Unit;
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
            "appId",
            preProcessAction,
            postProcessAction,
            false,
            callback
        );
    }

    /**
     * Java example showing how to initialize the Radio SDK, scan, connect, and send a model.
     */
    void initScanConnectSend() throws ExecutionException, InterruptedException {
        GotennaClientJvmExtKt.initializeAsync(
                GotennaClient.INSTANCE,
                "sdkToken",
                "appId",
                null,
                null,
                false
        );

        final CompletableFuture<List<RadioModel>> scanFuture = GotennaClientJvmExtKt.scanAsync(
                GotennaClient.INSTANCE,
                ConnectionType.USB,
                null
        );
        final RadioModel radioModel = scanFuture.get().get(0);

        // Used to listen for radio connection changes
        final JavaAdapter javaAdapter = new JavaAdapter(radioModel);
        javaAdapter.collectRadioState(new StateUpdateCallback() {
            @Override
            public void onStateUpdated(@NotNull RadioState state) {
                System.out.println("New radio state: " + state);
            }
        });

         javaAdapter.collectRadioEvents(new EventsCallback() {
            @Override
            public void onEvent(@NotNull RadioResult<RadioCommand> result) {
                System.out.println("New radio event " + result);
                if(result instanceof RadioResult.Success<RadioCommand>) {
                    final RadioCommand executed = ((RadioResult.Success<RadioCommand>) result).getExecuted();
                    if(executed instanceof SendToNetwork.Location location) {
                        final double lat = location.getLat();
                        final double lon = location.getLong();
                    } else if(executed instanceof SendToNetwork.AnyNetworkMessage anyNetworkMessage) {
                        
                    }
                }
            }
        });

        final CompletableFuture<RadioResult<Unit>> connectFuture = RadioModelJvmExtKt.connectAsync(radioModel);
        final RadioResult<Unit> result = connectFuture.get();
        if (result instanceof RadioResult.Failure<?>) {
            System.out.println("Connection failed");
            return;
        }

        final CompletableFuture<RadioResult<DeliveryResult>> sendResult = RadioModelJvmExtKt.sendToNetworkAsync(
                radioModel,
                new SendToNetwork.Location(
                        "h-e",
                        60,
                        35.291802,
                        80.846604,
                        237.21325546763973,
                        "CYAN",
                        11,
                        1745135755,
                        0,
                        new CommandMetaData(
                                GTMessageType.BROADCAST,
                                0,
                                false,
                                GTMessagePriority.NORMAL,
                                90461489
                        ),
                        new GotennaHeaderWrapper(
                                System.currentTimeMillis(),
                                MessageTypeWrapper.LOCATION,
                                "recipientUUID",
                                0,
                                90461489,
                                "senderUUID",
                                "senderCallsign",
                                null,
                                "uuid"
                        ),
                        GripResult.Unknown.INSTANCE,
                        null,
                        -1
                )
        );
    }

}
