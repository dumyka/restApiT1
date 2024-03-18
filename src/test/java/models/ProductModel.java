package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force=true)
public class ProductModel {
    private int id;
    private String name;
    private String category;
    private Double price;
    private int discount;
    @JsonIgnore
    private int quantity;
}
