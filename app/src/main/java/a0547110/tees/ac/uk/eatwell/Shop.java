package a0547110.tees.ac.uk.eatwell;

import java.io.Serializable;

public class Shop implements Serializable {
    private String name;
    private String address;
    private String photoUrl;
    private Double rate;

    public Shop(String name, String address,String photoUrl,Double  rate) {
        this.name=name;
        this.address=address;
        this.photoUrl=photoUrl;
        this.rate=rate;
    }

    public String getName(){
        return name;
    };

    public String getAddress(){
        return address;
    };

    public String getPhotoUrl(){
        return photoUrl;
    };

    public Double getRate(){
        return rate;
    };
};
