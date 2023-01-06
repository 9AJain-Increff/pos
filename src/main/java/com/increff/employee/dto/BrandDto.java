package com.increff.employee.dto;


import com.increff.employee.model.BrandData;
import com.increff.employee.model.BrandForm;
import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.pojo.EmployeePojo;
import com.increff.employee.service.ApiException;
import com.increff.employee.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

import static com.increff.employee.util.ConversionUtil.*;

@Component
public class BrandDto {


    @Autowired
    private BrandService service;


    public BrandData get(int id) throws ApiException {
         BrandPojo b = service.get(id);
         return convertToBrandData(b);
    }
    public void addingBrand(BrandForm form) throws ApiException {
        BrandPojo p = convertToBrandPojo(form);
        service.add(p);
    }

    public void deleting(int id) throws ApiException  {
        service.delete(id);
    }


    public void updating(int id, BrandForm form) throws ApiException  {
        BrandPojo p = convertToBrandPojo(form);
        service.update(id,p);
    }

    public List<BrandData> gettingAllBrand() {
        List<BrandPojo> list = service.getAll();
        System.out.println(list);
        List<BrandData> list2 = new ArrayList<BrandData>();
        for (BrandPojo p : list) {
            list2.add(convertToBrandData(p));
        }
        return list2;
    }
//
//    private static BrandData convert(BrandPojo p) {
//        BrandData d = new BrandData();
//        d.setName(p.getName());
//        d.setCategory(p.getCategory());
//        d.setId(p.getId());
//        return d;
//    }
//
//    private static  BrandPojo convert(BrandForm f) {
//        BrandPojo p = new BrandPojo();
//        p.setName(f.getName());
//        p.setCategory(f.getCategory());
//        return p;
//    }

}
