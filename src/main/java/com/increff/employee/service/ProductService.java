package com.increff.employee.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.increff.employee.dao.ProductDao;
import com.increff.employee.model.InventoryData;
import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.employee.dao.EmployeeDao;
import com.increff.employee.pojo.EmployeePojo;
import com.increff.employee.util.StringUtil;

@Service
public class ProductService {

    @Autowired
    private ProductDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public void add(ProductPojo p) throws ApiException {
        normalize(p);

        System.out.println("ankur jainnnnnnnnnnn");
        ProductPojo check = dao.checkProductExists(p.getBarcode());
        if(check == null){
            dao.insert(p);
        }
        else{
            throw new ApiException(" barcode already exists");
        }

    }

    @Transactional
    public void delete(String barcode) {
        dao.delete(barcode);
    }


//    @Transactional(rollbackOn = ApiException.class)
//    public ProductPojo get(int id) throws ApiException {
//        return getCheck(id);
//    }

    @Transactional
    public List<ProductPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(String barcode, ProductPojo p) throws ApiException {
        normalize(p);
        ProductPojo ex = getCheck(barcode);
            System.out.println(p.getName());
            ex.setName(p.getName());
            ex.setPrice(p.getPrice());
//        else{
//            throw new ApiException("product name + category already exists");
//        }

//        dao.update(ex);
    }


    public List<ProductPojo> getProductByBarcode (List<InventoryPojo> list ) {
        System.out.println("anknanana");
        List<ProductPojo> list2 = new ArrayList<ProductPojo>();
        list.forEach((temp) -> {
            System.out.println("ppppppppppppp");
            System.out.println(temp.getBarcode());
                    ProductPojo p =dao.select(temp.getBarcode());
                    list2.add(p);
                });

        return list2;
    }



    @Transactional
    public ProductPojo getCheck(String barcode) throws ApiException {
        ProductPojo p = dao.select(barcode);
        if (p == null) {
            throw new ApiException("Product with given ID does not exit, id: " + barcode);
        }
        return p;
    }

    public Boolean checkProductExists(String barcode) throws ApiException {
        ProductPojo p = dao.select(barcode);
        if(p == null) {
            throw new ApiException(("barcode doesn't exist in products"));
        }
        return true;

    }

    @Transactional
    public ProductPojo getPrice(String barcode) throws ApiException {
        ProductPojo p = dao.select(barcode);
        return p;
    }

    public Boolean nameExist(String name) throws ApiException {
        ProductPojo p = dao.checkName(name);
        if(p == null) {
            return false;
        }
        else {
            throw new ApiException(("brand name+brand category +product name already exist"));
        }
    }




    public Boolean     checkBarcodeSameOrNot(String updatedBarcode,int id) throws ApiException {
        ProductPojo p = dao.select(id);
        String currentBarcode = p.getBarcode();

        if(updatedBarcode == currentBarcode) {
            return true;
        }
        return false;

    }



    protected static void normalize(ProductPojo p) {
        p.setName(StringUtil.toLowerCase(p.getName()));
    }



}
