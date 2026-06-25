package com.student.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoUtil {

    private static final String URL = "mongodb://localhost:27017";

    private static final MongoClient client = MongoClients.create(URL);

    public static MongoDatabase getDatabase() {
        return client.getDatabase("studentdb");
    }

    public static void close() {
        client.close();
    }
}