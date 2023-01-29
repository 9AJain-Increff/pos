package com.increff.pos.dao;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryDao extends AbstractDao {

    // TODO: 29/01/23 how is this method working when you dont have barcode in pojo?
    private static String delete_inventory_by_barcode = "delete from InventoryPojo p where barcode=:barcode";
    private static String select_inventory_by_id = "select p from InventoryPojo p where id=:id";
    private static String select_all_inventory = "select p from InventoryPojo p";

    // TODO: 29/01/23 how is this method working when you dont have barcode in pojo?
    private static String select_inventory_by_barcode = "select p from InventoryPojo p where barcode=:barcode";
    // TODO: 29/01/23 what is check?
    private static String check = "select p from InventoryPojo p where barcode=:barcode";


    @Transactional
    // TODO: 29/01/23 unnecessary throwing of an exception
    public void insert(InventoryPojo p) throws ApiException {
        em().persist(p);
    }

    // TODO: 29/01/23 remove
    public int delete(String barcode) {
        Query query = em().createQuery(delete_inventory_by_barcode);
        query.setParameter("barcode", barcode);
        return query.executeUpdate();
    }

    // TODO: 29/01/23 remove if not used
    public Boolean checkInventoryExists(String barcode) {
        TypedQuery<InventoryPojo> query = getQuery(select_inventory_by_barcode, InventoryPojo.class);
        query.setParameter("barcode", barcode);

        InventoryPojo p = getSingleBrand(query);

        if(p==null){return false;}
        else{
            return true;
        }
    }

    // TODO: 29/01/23 why select by barcode?
    public InventoryPojo select(String barcode) {
        TypedQuery<InventoryPojo> query = getQuery(select_inventory_by_barcode, InventoryPojo.class);
        query.setParameter("barcode", barcode);
        return getSingleBrand(query);
    }

    // TODO: 29/01/23 in the pojo id and productId are different. Did you test the methods properly?
    public InventoryPojo selectInventoryByProductId(Integer productId) {
        TypedQuery<InventoryPojo> query = getQuery(select_inventory_by_id, InventoryPojo.class);
        query.setParameter("id", productId);
        return getSingleBrand(query);
    }

    // TODO: 29/01/23 why select by brand and category is there in inventoryDao
    public InventoryPojo select(String name, String category) {
        TypedQuery<InventoryPojo> query = getQuery(select_inventory_by_barcode, InventoryPojo.class);
        query.setParameter("name", name);
        query.setParameter("category", category);
        return getSingleBrand(query);
    }

    public List<InventoryPojo> selectAll() {
        TypedQuery<InventoryPojo> query = getQuery(select_all_inventory, InventoryPojo.class);

        return query.getResultList();
    }


}
