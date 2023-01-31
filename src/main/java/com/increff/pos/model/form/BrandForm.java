package com.increff.pos.model.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//  FIXED: 29/01/23 make sep folders for form and data
public class BrandForm {

    private String name;
    private String category;


}
