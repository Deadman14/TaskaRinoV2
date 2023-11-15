package models;

public class GeItem {
    private String name;
    private int amount;

    private int price;

    public GeItem(String name, int amount, int price) {
        this.name = name;
        this.amount = amount;
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public int getPrice() {
        return price;
    }
}
