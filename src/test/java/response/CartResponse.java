package response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.ProductModel;

import java.util.List;

@Data
@NoArgsConstructor(force = true)
public class CartResponse {
    private List<ProductModel> cart;
    @JsonProperty("total_discount")
    private double totalDiscount;
    @JsonProperty("total_price")
    private double totalPrice;
}
