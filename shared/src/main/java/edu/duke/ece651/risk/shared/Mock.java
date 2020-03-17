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

    /**
     * Send the data to the output stream
     * @param out output stream
     * @param object data to be sent
     * @throws IOException probably because the stream is already closed
     */
    public static void send(OutputStream out, Object object) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
    }

    /**
     * This function will receive an object from target stream.
     * @param in input stream
     * @return received data
     * @throws IOException probably because the stream is already closed
     * @throws ClassNotFoundException probably because receive some illegal data
     */
    public static Object recv(InputStream in) throws IOException, ClassNotFoundException {
        return new ObjectInputStream(in).readObject();
    }
}
