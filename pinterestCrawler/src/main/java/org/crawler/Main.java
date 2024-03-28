package org.crawler;

import org.crawler.controller.PinterestCrawlerController;

public class Main {
 static PinterestCrawlerController pinterestCrawlerController = new PinterestCrawlerController();
    public static void main(String[] args) {
        pinterestCrawlerController.searchPinterest();
    }
}