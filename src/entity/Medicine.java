package entity;

import java.io.Serializable;

public class Medicine implements Serializable {
    private String id;
    private String name;
    private int quantity;
    private double price;
    private String description;

    public Medicine(String id, String name, int quantity, double price, String description) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.description = description;
    }

    public boolean reduceQuantity(int qty){
        if (quantity >= qty){
            quantity -= qty;
            return true;
        }
        return false;
    }

    public void restock(int qty){
        quantity += qty;
    }

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public int getQuantity(){
        return quantity;
    }

    public double getPrice(){
        return price;
    }

    public String getDescription() {
        return description;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        String desc = description;
        if (desc.length() > 28) {
            desc = desc.substring(0, 25) + "...";
        }
        return String.format(" %-8s | %-14s | %-8d | %-9.2f | %-26s │",
                id, name, quantity, price, desc);
    }
}
