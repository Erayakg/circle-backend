package com.CircleBackend.demo.services;
import com.CircleBackend.demo.dto.TransferTokenReq;
import com.CircleBackend.demo.dto.WalletBalanceResDto;
import com.CircleBackend.demo.dto.WalletResDto;
import com.CircleBackend.demo.entities.User;
import com.CircleBackend.demo.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
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
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CircleAPI  {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final UserRepository userRepository;


    public static final String API_KEY="TEST_API_KEY:ba4ab1d4b7e92f4a370154dd807be0db:48e096ee7ebc82cec0881ea23131ae2b";
    public static final String WALLET_SET_ID = "76357bcc-4ad4-5b0d-9200-a96a3ed5c129";
    public static final String  ENCRYPTED_DATA="pWWimvuB7lVQep18615nIiylhtT5Z7IhRF8EABfJ7njMuWsFLNlXpGzZKWNlecI03e07A7KFiHzs3dNNFGXWqKazJP316a61eEp1nK7e7dqN6io6im+LSCaONEd/Wd0VKyTYuBtrn4qbypXYJM3QPmjJjrgptRV0i/sd26EDbtGxP/LcX5BDBBc6xEKeLqvQuSWmEebd0LcwnikM/Eyu9sFa7ATQWT9PjqAfRrd3OF5B/vxr9b8L5bSMmxqj09D+mYv4BNHZkSL5APl3xfseI7DDmcPTMkJ3fOuC5xRrjoL/5jX2S6z54whytkRTvt1Iz59k9Gxj41WFvCluj5HWxFY5WwummvlOA7BI/letSJTcPwpulysWwOXmUHgoHbeb7gFeA1JIKANG7xTQRrqVhwoghIkxR2VYEwUVhSUWk0LcvnoibVCEGVgDWjTtEpn0iXoYHiTFvp0NY+pudqqAi+VAMRgB4kP5JqcPmE0kJ/4JjJ3R8Q0tQVnvWVnlLMP3FgfdZ8LB4xPsZnh8Bsd3SSv/RPCYbc0jQL7VfFqmOxYrtkKTTgz0XUFKH8BMc+ThPEUm0/ORcVxLUVqkDz1qbcEWtl82rk9QNbr8zBdGKL29FaUPooi++ROFHm1JheRjJUGFRwcAql8tBpCZMePDH9nX8mxxCneCqKO6raFIFXA=";
    static String ENTITY_SECRET="f25dffbd11f9425b985570f15ab1d0cbd08055cdd492a613471dbe7b6a13129c";
    @Autowired
    public CircleAPI(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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

        byte[] entitySecret = hexToBytes(secret);

        String publicKeyPEM = fetchPublicKey(API_KEY);
        byte[] decodedKey = Base64.getDecoder().decode(publicKeyPEM);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(spec);

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
        );
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);

        byte[] encryptedData = cipher.doFinal(entitySecret);

        return Base64.getEncoder().encodeToString(encryptedData);
    }

    private  byte[] hexToBytes(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
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
        String chipperText = generateCiphertext(ENTITY_SECRET);
        String idempotencyKey = UUID.randomUUID().toString();


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.circle.com/v1/w3s/developer/wallets"))
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("authorization", "Bearer "+API_KEY)
                .method("POST", HttpRequest.BodyPublishers.ofString("{\"blockchains\":[\"MATIC-AMOY\"],\"entitySecretCiphertext\":\""+chipperText+"\",\"idempotencyKey\":\""+idempotencyKey+"\",\"accountType\":\"SCA\",\"walletSetId\":\""+WALLET_SET_ID+"\",\"count\":1}"))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());


            String responseBody = response.body();

            WalletResDto walletResDto = objectMapper.readTree(responseBody)
                    .get("data")
                    .get("wallets")
                    .get(0)
                    .traverse(objectMapper)
                    .readValueAs(WalletResDto.class);

            return walletResDto;


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

    public  String transferToken(TransferTokenReq transferTokenReq, Long id) throws Exception {

        try {
            String chipperText = generateCiphertext(ENTITY_SECRET);
            String idempotencyKey = UUID.randomUUID().toString();

            List<User> all = userRepository.findAll();

            Optional<User> user12=userRepository.findById(id);

            if (user12.isEmpty()){
                throw  new RuntimeException("user not found");
            }
            if (user12.get().getWallet() == null) {
                throw new RuntimeException("User's wallet is null");
            }
            String walletId= user12.get().getWallet().getUUIDId();
            System.out.println(walletId);
            String destinationWalletAdress =null;
            for (User user1 : all){
                if (user1.getRole().getType() == 3 ){
                    destinationWalletAdress=user1.getWallet().getAddress();
                    break;
                }
            }
            System.out.println(destinationWalletAdress);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.circle.com/v1/w3s/developer/transactions/transfer"))
                    .header("accept", "application/json")
                    .header("content-type", "application/json")
                    .header("authorization", "Bearer TEST_API_KEY:ba4ab1d4b7e92f4a370154dd807be0db:48e096ee7ebc82cec0881ea23131ae2b")
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\"amounts\":[\""+transferTokenReq.getAmounts()+"\"],\"feeLevel\":\"LOW\",\"blockchain\":\"MATIC-AMOY\",\"walletId\":\""+walletId+"\",\"destinationAddress\":\""+destinationWalletAdress+"\",\"entitySecretCiphertext\":\""+chipperText+"\",\"idempotencyKey\":\""+idempotencyKey+"\"}"))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());
            if (response.statusCode()==200){
                return "sucesess";
            }
        }
        catch (Exception e){
            throw new RuntimeException("error " + e.getMessage());
        }
    return null;
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

    public WalletBalanceResDto getWalletBalance(String id) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.circle.com/v1/w3s/wallets/445519e6-14af-5123-8776-aa820b34cc05/balances?pageSize=10"))
                .header("accept", "application/json")
                .header("authorization", "Bearer TEST_API_KEY:ba4ab1d4b7e92f4a370154dd807be0db:48e096ee7ebc82cec0881ea23131ae2b")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();
        WalletBalanceResDto walletBalance = objectMapper.readValue(response.body(), WalletBalanceResDto.class);

        System.out.println(response.body());

        return walletBalance;
    }
}
