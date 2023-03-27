package a0547110.tees.ac.uk.eatwell;

import java.io.Serializable;

public class Shop implements Serializable {
    private String name;
    public Shop(String name) {
        this.name=name;
    }

    public String getName(){
        return name;
    };
};
