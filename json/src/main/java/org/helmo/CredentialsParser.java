package org.helmo;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class CredentialsParser {

    public static final String CREDENTIALS_PATH = "/credentials.json";

    public static String credentialsToBase64() {
        try {
            // Lire le fichier credentials.json en tant que byte array
            byte[] credentialsBytes = Files.readAllBytes(Paths.get(CredentialsParser.class.getResource(CREDENTIALS_PATH).toURI()));

            // Convertir le byte array en Base64
            return Base64.getEncoder().encodeToString(credentialsBytes);
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture du fichier credentials.json: " + e.getMessage());
            return null; // Ou gérer l'erreur de manière plus appropriée
        }
    }

    public static String base64ToCredentials(String base64) {
        try {
            // Convertir le Base64 en byte array
            byte[] credentialsBytes = Base64.getDecoder().decode(base64);

            return new String(credentialsBytes);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'écriture du fichier credentials.json: " + e.getMessage());
            return null; // Ou gérer l'erreur de manière plus appropriée
        }
    }

    public static void main(String[] args) {
        String base64 = credentialsToBase64();
        System.out.println(base64);
        String credentials = base64ToCredentials(base64);
        System.out.println(credentials);
    }

}
