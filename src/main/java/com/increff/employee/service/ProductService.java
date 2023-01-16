package com.increff.employee.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import com.increff.employee.dao.ProductDao;
import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.employee.util.StringUtil;

@Service
public class ProductService {

    @Autowired
    private ProductDao dao;

    @Autowired
    private BrandService brandService;

    @Autowired
    private InventoryService inventoryService;
    @Transactional(rollbackOn = ApiException.class)
    public void addProduct(ProductPojo product, InventoryPojo inventoryPojo, BrandPojo brand) throws ApiException {
        checkBarcode(product.getBarcode());
        dao.add(product);
        inventoryService.add(inventoryPojo);
    }
    public void checkProductExist(ProductPojo product, BrandPojo brand) throws ApiException {
        ProductPojo exist = dao.getProductByProductName(product);
        if(exist!=null){
            if(exist.getBrandId() == brand.getId()){
                throw new ApiException("Product with given name already exist " );
            }
        }

    }

    @Transactional
//    public void delete(String barcode) {
//        dao.delete(barcode);
//    }


//    @Transactional(rollbackOn = ApiException.class)
//    public ProductPojo get(int id) throws ApiException {
//        return getCheck(id);
//    }

    public List<BrandPojo> getBrandsByProducts(List<ProductPojo> products) throws ApiException {
        return brandService.getBrandsByProducts(products);
    }
    public BrandPojo getBrandByProduct(ProductPojo product) throws ApiException {
        return brandService.getAndCheckBrandById(product.getBrandId());
    }

    public List<ProductPojo> getAllProduct() throws ApiException {
        return dao.getAllProduct();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(ProductPojo product, BrandPojo brand, String barcode) throws ApiException {
        ProductPojo exist = getAndCheckProductByBarcode(product.getBarcode());
        exist.setName(product.getName());
        exist.setPrice(product.getPrice());
    }

    public List<ProductPojo> getProductPojo(List<String> barcode) throws ApiException {
        List<ProductPojo> productPojoList = new ArrayList<>();
        for (String orderItemBarcode : barcode) {
            ProductPojo productPojo = getProductByBarcode(orderItemBarcode);
            productPojoList.add(productPojo);
        }
        return productPojoList;
    }


    public List<ProductPojo> getProductByBarcode (List<InventoryPojo> list ) {
        System.out.println("anknanana");
        List<ProductPojo> list2 = new ArrayList<ProductPojo>();
        list.forEach((temp) -> {
            System.out.println("ppppppppppppp");
            System.out.println(temp.getBarcode());
                    ProductPojo p =dao.getProductByBarcode(temp.getBarcode());
                    list2.add(p);
                });

        return list2;
    }

    @Transactional
    public ProductPojo getProductByBarcode(String barcode) throws ApiException {
        ProductPojo p = dao.getProductByBarcode(barcode);
        if (p == null) {
            throw new ApiException("Product with given barcode does not exit, id: " + barcode);
        }
        return p;
    }

    public ProductPojo getAndCheckProductByBarcode(String barcode) throws ApiException {
        ProductPojo p = dao.getProductByBarcode(barcode);
        if (p == null) {
            throw new ApiException("Product with given barcode does not exit, barcode: " + barcode);
        }
        return p;
    }

    public Boolean checkProductExists(String barcode) throws ApiException {
        ProductPojo p = dao.getProductByBarcode(barcode);
        if(p == null) {
            throw new ApiException(("barcode doesn't exist in products"));
        }
        return true;

    }

    public ProductPojo checkBarcode(String barcode) throws ApiException {

        ProductPojo p = dao.getProductByBarcode(barcode);
        if(p != null){
            throw new ApiException(("barcode already exist "));
        }

        return p;
    }


    public BrandPojo checkBrandNameAndCategory(ProductPojo product, String brandName, String brandCategory) throws ApiException {
        BrandPojo brand = brandService.getAndCheckBrandById(product.getBrandId());
        if(brand.getName().equals(brandName) && brand.getCategory().equals(brandCategory)){
            return brand;
        }
        else{
            throw new ApiException("brand name and category can't be changed");
        }
    }

    public ProductPojo checkProductByBrandName(
            String name,
            String brandName,
            String brandCategory) throws ApiException {

        ProductPojo p = dao.getProductByBrandName(name, brandName, brandCategory);
        if(p != null){
            throw new ApiException(("product already exist "));
        }

        return p;
    }

    public Map<String, ProductPojo> getProductsBybarcodes(List<String> barcodes ) {
        Map<String, ProductPojo> mapping = new HashMap<>();
        for(String barcode: barcodes){
            mapping.put(barcode,dao.getProductByBarcode(barcode));
        }
        return mapping;
    }

    public List<ProductPojo> getProducts(List<String> barcodes ) {
        List<ProductPojo> products = new ArrayList<>();
        for(String barcode: barcodes){
            products.add(dao.getProductByBarcode(barcode));
        }
        return products;
    }


    public  BrandPojo getAndCheckBrandId(String brandName, String brandCategory) throws ApiException {

        return brandService.getBrandId(brandName, brandCategory);
    }


    protected static void normalize(ProductPojo p) {
        p.setName(StringUtil.toLowerCase(p.getName()));
    }



}
