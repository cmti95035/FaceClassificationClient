package com.chinamobile.faceClassification.server;

import com.linkedin.common.callback.FutureCallback;
import com.linkedin.common.util.None;
import com.linkedin.data.ByteString;
import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.r2.transport.common.Client;
import com.linkedin.r2.transport.common.bridge.client.TransportClientAdapter;
import com.linkedin.r2.transport.http.client.HttpClientFactory;
import com.linkedin.restli.client.*;
import com.linkedin.restli.common.CollectionResponse;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.IdResponse;
import com.linkedin.restli.client.Response;
import com.linkedin.restli.client.ResponseFuture;
import com.linkedin.restli.client.ActionRequest;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class FaceClassificationClient {
    // Create an HttpClient and wrap it in an abstraction layer
    private static final HttpClientFactory http = new HttpClientFactory();
    private static final Client r2Client = new TransportClientAdapter(
            http.getClient(Collections.<String, String>emptyMap()));
    private static final String BASE_URL = "";	// needs to be inserted here such as "http://52.53.254.134:6666/"
    private static RestClient restClient = new RestClient(r2Client, BASE_URL);
    private static ActionsRequestBuilders actionsRequestBuilders = new ActionsRequestBuilders();

    public static void main(String[] args) throws Exception {
        classifyImage("filename");	// provide full path to the image file
        restClient.shutdown(new FutureCallback<None>());
        http.shutdown(new FutureCallback<None>());
    }

    private static FaceClassification classifyImage(String fileName){
        FaceImage faceImage = new FaceImage().setImageName(getBaseName(fileName)).setImageContent(ByteString.copy(readFromFile(fileName)));

        ActionRequest<FaceClassification> actionRequest = actionsRequestBuilders.actionClassifyPhoto().faceImageParam(faceImage).build();

        try{
            ResponseFuture<FaceClassification> responseFuture = restClient.sendRequest(actionRequest);
            Response<FaceClassification> response = responseFuture.getResponse();

            FaceClassification faceClassification = response.getEntity();
            System.out.println("\nclassifyImage returns: " + (faceClassification == null ? "null" : faceClassification));

            return faceClassification;
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getBaseName(String fileName){
        if(fileName == null)
            return null;

        String[] parts = fileName.split("/");
        return parts[parts.length - 1];
    }

    private static byte[] readFromFile(String fileName) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(fileName));
            return bytes;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

}

