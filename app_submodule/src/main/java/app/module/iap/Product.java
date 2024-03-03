package app.module.iap;

import com.google.gson.Gson;

public class Product {
    String productId;
    String type;
    String title;
    String name;
    String price;
    String price_amount_micros;
    String price_currency_code;
    String description;
    String skuDetailsToken;

    public Product(String productId, String type, String title, String name, String price, String price_amount_micros, String price_currency_code, String description, String skuDetailsToken) {
        this.productId = productId;
        this.type = type;
        this.title = title;
        this.name = name;
        this.price = price;
        this.price_amount_micros = price_amount_micros;
        this.price_currency_code = price_currency_code;
        this.description = description;
        this.skuDetailsToken = skuDetailsToken;
    }

    public static Product fromJson(String productJson) {
        return new Gson().fromJson(productJson, Product.class);

    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice_amount_micros() {
        return price_amount_micros;
    }

    public void setPrice_amount_micros(String price_amount_micros) {
        this.price_amount_micros = price_amount_micros;
    }

    public String getPrice_currency_code() {
        return price_currency_code;
    }

    public void setPrice_currency_code(String price_currency_code) {
        this.price_currency_code = price_currency_code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSkuDetailsToken() {
        return skuDetailsToken;
    }

    public void setSkuDetailsToken(String skuDetailsToken) {
        this.skuDetailsToken = skuDetailsToken;
        fromJson("");
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", price_amount_micros='" + price_amount_micros + '\'' +
                ", price_currency_code='" + price_currency_code + '\'' +
                ", description='" + description + '\'' +
                ", skuDetailsToken='" + skuDetailsToken + '\'' +
                '}';
    }
}
