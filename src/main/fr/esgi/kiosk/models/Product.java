package main.fr.esgi.kiosk.models;

import javafx.scene.image.Image;

public class Product extends RessourceElementProduct{

    private Ingredients ingredients;


    public Product(){

    }

    public Product(String uuid,String name, double price, Ingredients ingredients) {
        this.uuid = uuid;
        this.name = name;
        this.price = price;
        this.ingredients = ingredients;
    }

    public Product(String uuid, String name, long quantity, double price){

        this.uuid = uuid;
        this.name = name;
        this.quantity = quantity;
        this.price = price;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Ingredients getIngredients() {
        return ingredients;
    }

    public void setIngredients(Ingredients ingredients) {
        this.ingredients = ingredients;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
}
