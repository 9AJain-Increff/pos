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

    private static String select_inventory_by_id = "select p from InventoryPojo p where id=:id";
    private static String select_all_inventory = "select p from InventoryPojo p";


    @Transactional
    // TODO: 29/01/23 unnecessary throwing of an exception
    public void insert(InventoryPojo p) {
        em().persist(p);
    }

    public InventoryPojo selectInventoryByProductId(Integer productId) {
        TypedQuery<InventoryPojo> query = getQuery(select_inventory_by_id, InventoryPojo.class);
        query.setParameter("id", productId);
        return getSingleBrand(query);
    }

    public List<InventoryPojo> selectAll() {
        TypedQuery<InventoryPojo> query = getQuery(select_all_inventory, InventoryPojo.class);

        return query.getResultList();
    }
}
