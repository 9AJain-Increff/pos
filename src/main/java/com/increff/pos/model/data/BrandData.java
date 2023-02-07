package com.increff.pos.model.data;

import com.increff.pos.model.form.BrandForm;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandData extends BrandForm {

    private Integer id;


}
