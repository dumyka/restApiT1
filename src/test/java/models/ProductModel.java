package models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force=true)
public class ProductModel {
    private int id;
    private String name;
    private String category;
    private double price;
    private int discount;
}
