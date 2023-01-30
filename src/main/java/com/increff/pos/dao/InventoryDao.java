package com.increff.pos.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import com.increff.pos.pojo.InventoryPojo;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryDao extends AbstractDao {

    private static final String SELECT_INVENTORY_BY_ID = "select p from InventoryPojo p where id=:id";
    private static final String SELECT_ALL_INVENTORY = "select p from InventoryPojo p";


    // FIXED: 29/01/23 unnecessary throwing of an exception
    public void insert(InventoryPojo p) {
        em().persist(p);
    }

    public InventoryPojo selectInventoryByProductId(Integer productId) {
        TypedQuery<InventoryPojo> query = getQuery(SELECT_INVENTORY_BY_ID, InventoryPojo.class);
        query.setParameter("id", productId);
        return getSingle(query);
    }

    public List<InventoryPojo> selectAll() {
        TypedQuery<InventoryPojo> query = getQuery(SELECT_ALL_INVENTORY, InventoryPojo.class);
        return query.getResultList();
    }
}
