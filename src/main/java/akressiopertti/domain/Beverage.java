package akressiopertti.domain;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Beverage extends BaseModel{
    
    private String name;
    private float price;
    private String acquiredFrom;
    private String comment;
    private float alcohol;
    private String alkoCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getAcquiredFrom() {
        return acquiredFrom;
    }

    public void setAcquiredFrom(String acquiredFrom) {
        this.acquiredFrom = acquiredFrom;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(float alcohol) {
        this.alcohol = alcohol;
    }

    public String getAlkoCode() {
        return alkoCode;
    }

    public void setAlkoCode(String alkoCode) {
        this.alkoCode = alkoCode;
    }
    
}
