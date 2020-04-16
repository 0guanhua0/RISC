package edu.duke.ece651.risk.server;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoClients;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class Mongo {
    static MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://risc:risc@vcm-12835.vm.duke.edu/risc"));
    /*
    public static void main() {
        final Datastore datastore = Morphia.createDatastore(MongoClients.create(), "morphia_example");
// tell Morphia where to find your classes
// can be called multiple times with different packages or classes
        datastore.getMapper().mapPackage("dev.morphia.example");

        datastore.ensureIndexes();

    }

     */
}
