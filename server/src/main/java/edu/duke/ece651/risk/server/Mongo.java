package edu.duke.ece651.risk.server;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoClients;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import static edu.duke.ece651.risk.shared.Constant.MONGO_DB_NAME;
import static edu.duke.ece651.risk.shared.Constant.MONGO_URL;

public class Mongo {
    static MongoClient mongoClient = new MongoClient(new MongoClientURI(MONGO_URL));

    //save all value
    void save(Object object) {

        final Morphia morphia = new Morphia();
        morphia.mapPackage("edu.duke.ece651.risk");

        final Datastore datastore = morphia.createDatastore(mongoClient, MONGO_DB_NAME);
        datastore.ensureIndexes();

        datastore.save(object);
    }

}
