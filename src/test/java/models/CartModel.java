package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartModel {

    @JsonProperty("product_id")
    private Integer productId;
    private int quantity;
}
