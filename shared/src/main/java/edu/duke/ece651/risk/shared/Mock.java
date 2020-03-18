package edu.duke.ece651.risk.shared;

import java.io.*;
import java.util.List;

public class Mock {
    public static InputStream setupMockInput(List<Object> inputs) throws IOException {
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(temp);
        for (Object o : inputs){
            objectOutputStream.writeObject(o);
        }
        objectOutputStream.flush();
        return new ByteArrayInputStream(temp.toByteArray());
    }

    public static String readAllStringFromObjectStream(ByteArrayOutputStream out) throws IOException, ClassNotFoundException {
        ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
        StringBuilder stringBuilder = new StringBuilder();
        try {
            while(true){
                // keep reading until throws an exception
                Object object = stream.readObject();
                if (object instanceof String){
                    stringBuilder.append(object);
                }
            }
        }catch (EOFException ignored){

        }
        return stringBuilder.toString();
    }
}
