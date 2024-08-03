package com.CircleBackend.demo.services;
import com.CircleBackend.demo.dto.WalletResDto;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Component
public class CircleAPI  {

    public static final String API_KEY="TEST_API_KEY:ba4ab1d4b7e92f4a370154dd807be0db:48e096ee7ebc82cec0881ea23131ae2b";
    public static final String WALLET_SET_ID = "76357bcc-4ad4-5b0d-9200-a96a3ed5c129";
    public static final String  ENCRYPTED_DATA="pWWimvuB7lVQep18615nIiylhtT5Z7IhRF8EABfJ7njMuWsFLNlXpGzZKWNlecI03e07A7KFiHzs3dNNFGXWqKazJP316a61eEp1nK7e7dqN6io6im+LSCaONEd/Wd0VKyTYuBtrn4qbypXYJM3QPmjJjrgptRV0i/sd26EDbtGxP/LcX5BDBBc6xEKeLqvQuSWmEebd0LcwnikM/Eyu9sFa7ATQWT9PjqAfRrd3OF5B/vxr9b8L5bSMmxqj09D+mYv4BNHZkSL5APl3xfseI7DDmcPTMkJ3fOuC5xRrjoL/5jX2S6z54whytkRTvt1Iz59k9Gxj41WFvCluj5HWxFY5WwummvlOA7BI/letSJTcPwpulysWwOXmUHgoHbeb7gFeA1JIKANG7xTQRrqVhwoghIkxR2VYEwUVhSUWk0LcvnoibVCEGVgDWjTtEpn0iXoYHiTFvp0NY+pudqqAi+VAMRgB4kP5JqcPmE0kJ/4JjJ3R8Q0tQVnvWVnlLMP3FgfdZ8LB4xPsZnh8Bsd3SSv/RPCYbc0jQL7VfFqmOxYrtkKTTgz0XUFKH8BMc+ThPEUm0/ORcVxLUVqkDz1qbcEWtl82rk9QNbr8zBdGKL29FaUPooi++ROFHm1JheRjJUGFRwcAql8tBpCZMePDH9nX8mxxCneCqKO6raFIFXA=";
   static String ENTITY_SECRET="f25dffbd11f9425b985570f15ab1d0cbd08055cdd492a613471dbe7b6a13129c";

    public  String generateSecret() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public  String fetchPublicKey(String apiKey) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.circle.com/v1/w3s/config/entity/publicKey"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+API_KEY )
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            String responseBody = response.body();
            JSONObject jsonResponse = new JSONObject(responseBody);
            String publicKey = jsonResponse.getJSONObject("data").getString("publicKey");

            // Remove the BEGIN and END lines
            publicKey = publicKey.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", ""); // Remove all whitespace characters

            return publicKey;
        } else {
            throw new RuntimeException("Failed to fetch public key: " + response.statusCode() + " " + response.body());
        }

    }

    public  String generateCiphertext(String secret) throws Exception {
        byte[] entitySecret = Base64.getDecoder().decode(secret);
        String publicKeyPEM = fetchPublicKey(secret);
        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(spec);

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedData = cipher.doFinal(entitySecret);
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public  void createWalletSet() throws Exception {
        URL url = new URL("https://api-sandbox.circle.com/v1/wallets/sets");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setDoOutput(true);

        String jsonInputString = new JSONObject().put("name", "Set 1").toString();
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("response: " + response.toString());
            }
        } else {
            System.out.println("POST request failed with response code: " + responseCode);
        }
    }


    public WalletResDto createWallet() throws Exception {

        String secret = generateSecret();
        String chipperText = generateCiphertext(secret);
        String idempotencyKey = UUID.randomUUID().toString();


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.circle.com/v1/w3s/developer/wallets"))
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\"accountType\":\"SCA\",\"idempotencyKey\":\""+idempotencyKey+"\",\"entitySecretCiphertext\":\""+ENCRYPTED_DATA+"\",\"walletSetId\":\""+WALLET_SET_ID+"\"}"))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());

        if (response.statusCode() == 200) {
             response.body();
             return new WalletResDto();
        } else {
            throw new RuntimeException("Failed to create wallet: " + response.statusCode() + " " + response.body());
        }
    }


    public  void getWallets() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.circle.com/v1/w3s/wallets?pageSize=10"))
                .header("accept", "application/json")
                .header("authorization", "Bearer "+API_KEY)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

    public  void getWallet(String WALLET_ID_1) throws Exception {
        URL url = new URL("https://api-sandbox.circle.com/v1/wallets/" + WALLET_ID_1);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("response: " + response.toString());
            }
        } else {
            System.out.println("GET request failed with response code: " + responseCode);
        }
    }

    public  void walletTransactions(String WALLET_ID_1) throws Exception {
        URL url = new URL("https://api-sandbox.circle.com/v1/wallets/" + WALLET_ID_1 + "/transactions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("response: " + response.toString());
            }
        } else {
            System.out.println("GET request failed with response code: " + responseCode);
        }
    }

    public  void getBalance(String WALLET_ID_2) throws Exception {
        URL url = new URL("https://api-sandbox.circle.com/v1/wallets/" + WALLET_ID_2 + "/balances");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("response: " + response.toString());
            }
        } else {
            System.out.println("GET request failed with response code: " + responseCode);
        }
    }

    public  void transferToken(String WALLET_ID_1,String WALLET_ADDRESS_2) throws Exception {
        URL url = new URL("https://api-sandbox.circle.com/v1/wallets/" + WALLET_ID_1 + "/transfers");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setDoOutput(true);

        String jsonInputString = new JSONObject()
                .put("tokenId", "36b6931a-873a-56a8-8a27-b706b17104ee")
                .put("destinationAddress", WALLET_ADDRESS_2)
                .put("amounts", new JSONArray().put(".01"))
                .put("fee", new JSONObject().put("type", "level").put("config", new JSONObject().put("feeLevel", "MEDIUM")))
                .toString();

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("response: " + response.toString());
            }
        } else {
            System.out.println("POST request failed with response code: " + responseCode);
        }
    }

    public  void checkTransferState(String id) throws Exception {
        URL url = new URL("https://api-sandbox.circle.com/v1/wallets/transfers/" + id);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("response: " + response.toString());
            }
        } else {
            System.out.println("GET request failed with response code: " + responseCode);
        }
    }
}
