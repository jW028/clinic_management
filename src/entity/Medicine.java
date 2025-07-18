package entity;

public class Medicine {
    private String id;
    private String name;
    private double price;
    private int stock;
    private String description;

    public Medicine(String id, String name, double price, int stock, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getDescription() {
        return description;
    }



    @Override
    public String toString() {
        return String.format("Medication ID: %s, Name: %s, Price: RM%.2f, Stock: %d", id, name, price, stock);
    }
}
