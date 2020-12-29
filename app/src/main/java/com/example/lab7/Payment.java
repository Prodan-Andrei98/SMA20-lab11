package com.example.lab7;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Payment implements Serializable {

    public String timestamp;
    private double cost;
    private String name;
    private String type;

    public Payment() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Payment(double cost, String name, String type) {
        this.cost = cost;
        this.name = name;
        this.type = type;
    }
    public void setTimestamp(String t) {
        this.timestamp = t;
    }
    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public String getType() {
        return type;
    }


    public Payment copy() throws IOException, ClassNotFoundException {
        //Serialization of object
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(this);

        //De-serialization of object
        ByteArrayInputStream bis = new   ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bis);
        Payment copied = (Payment) in.readObject();

        //Verify that object is not corrupt

        //validateNameParts(fName);
        //validateNameParts(lName);

        return copied;
    }

}