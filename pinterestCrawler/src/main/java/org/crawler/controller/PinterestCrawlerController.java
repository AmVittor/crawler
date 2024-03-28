package org.crawler.controller;

import org.crawler.service.PinterestCrawlerService;

import java.io.IOException;
import java.util.Scanner;

public class PinterestCrawlerController {
    public void searchPinterest(){
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter the search term: ");
            String searchTerm = scanner.nextLine();

            System.out.print("Enter the number of images to download: ");
            int numImages = scanner.nextInt();
            scanner.nextLine();
            PinterestCrawlerService pinterestCrawlerService = new PinterestCrawlerService();
            try {
                int numDownloadedImages = pinterestCrawlerService.downloadImages(searchTerm, numImages);
                System.out.println("Total images downloaded: " + numDownloadedImages);
                System.out.print("Do you want to search for another term? (Y/N): ");
                String response = scanner.nextLine();
                if (!response.equalsIgnoreCase("Y")) {
                    break;
                }
            } catch (IOException e) {
                System.err.println("Error downloading images: " + e.getMessage());
                System.out.print("Do you want to try again? (Y/N): ");
                String response = scanner.nextLine();
                if (!response.equalsIgnoreCase("Y")) {
                    break;
                }
            } catch (Exception e) {
                System.err.println("An unexpected error occurred: " + e.getMessage());
                break;
            }
        }
        scanner.close();
    }


}
