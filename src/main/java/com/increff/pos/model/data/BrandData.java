package com.increff.pos.model.data;

import com.increff.pos.model.form.BrandForm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandData extends BrandForm {

    private int id;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InventoryData {
        private Integer id;
        private String barcode;
        private String productName;
        private Integer quantity;


    }
}
