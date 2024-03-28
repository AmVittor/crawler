package org.crawler.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

public class PinterestCrawlerService {
    public int downloadImages(String searchTerm, int numImagesPerPage) throws IOException {
        String directoryPath = "your directory here" + searchTerm;
        createDirectoryIfNotExists(directoryPath);
        int numDownloadedImages = searchAndDownloadImages(searchTerm, directoryPath, numImagesPerPage);
        System.out.println("Images downloaded successfully to: " + directoryPath);
        return numDownloadedImages;
    }

    private void createDirectoryIfNotExists(String directoryPath) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                throw new IOException("Failed to create directory: " + directoryPath);
            }
        }
    }

    private int searchAndDownloadImages(String searchTerm, String directoryPath, int numImagesPerPage) throws IOException {
        String encodedSearchTerm = URLEncoder.encode(searchTerm, "UTF-8");
        String apiUrl = "https://www.pinterest.com/resource/BaseSearchResource/get/?source_url=/search/pins/?q=" + encodedSearchTerm + "&data=%7B%22options%22%3A%7B%22query%22%3A%22" + encodedSearchTerm + "%22%2C%22page_size%22%3A" + numImagesPerPage + "%7D%2C%22context%22%3A%7B%7D%7D";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(apiUrl).build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Failed to get response: " + response);
        }
        assert response.body() != null;
        JSONObject jsonObject = new JSONObject(response.body().string());
        JSONArray results = jsonObject.getJSONObject("resource_response").getJSONObject("data").getJSONArray("results");
        int numDownloadedImages = 0;
        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            String imageUrl = extractImageUrl(result);
            if (imageUrl != null) {
                downloadImage(imageUrl, directoryPath);
                numDownloadedImages++;
            }
        }
        return numDownloadedImages;
    }

    private String extractImageUrl(JSONObject jsonObject) {
        try {
            JSONObject images = jsonObject.getJSONObject("images");
            JSONObject mediumImage = images.getJSONObject("236x");
            return mediumImage.getString("url");
        } catch (Exception e) {
            return null;
        }
    }

    private void downloadImage(String imageUrl, String directoryPath) throws IOException {
        URL url = new URL(imageUrl);
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        File file = new File(directoryPath + File.separator + fileName);
        try (BufferedInputStream in = new BufferedInputStream(url.openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
    }
}
