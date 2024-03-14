package models;

import lombok.Data;

@Data
public class ProductModel {
    private String name;
    private String category;
    private double price;
    private int discount;
}
