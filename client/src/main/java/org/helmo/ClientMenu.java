package org.helmo;

import java.util.Scanner;

public class ClientMenu {

    public static void afficherMenu() {
        System.out.println("Menu:\n");
        System.out.println("1. Afficher l'état des services supervisés");
        System.out.println("2. Ajouter un nouveau service à superviser");
        System.out.println("3. Quitter \n");
    }

    public static void afficherEtatServices() {
        System.out.println("Affichage de l'état des services supervisés");
        // Implémentez ici la logique pour récupérer et afficher l'état des services supervisés
    }

    public static void ajouterSondeHTTPS() {
        //la logique pour créer une sonde HTTPS avec les informations saisies
    }

    public static void ajouterSondeSNMP() {
        //la logique pour créer une sonde SNMP avec les informations saisies
    }

    public static void ajouterService() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ajout d'un nouveau service à superviser");
        System.out.println("Veuillez choisir le type de sonde à ajouter :");
        System.out.println("1. Sonde HTTPS");
        System.out.println("2. Sonde SNMP");
        System.out.print("Votre choix : ");
        String typeSonde = scanner.nextLine();

        switch (typeSonde) {
            case "1":
                ajouterSondeHTTPS();
                break;
            case "2":
                ajouterSondeSNMP();
                break;
            default:
                System.out.println("Type de sonde invalide.");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            afficherMenu();
            System.out.print("Veuillez choisir une option : ");
            String choix = scanner.nextLine();

            switch (choix) {
                case "1":
                    afficherEtatServices();
                    break;
                case "2":
                    ajouterService();
                    break;
                case "3":
                    System.out.println("Au revoir !");
                    scanner.close();
                    return;
                default:
                    System.out.println("Option invalide. Veuillez choisir une option valide.");
            }
        }
    }
}
