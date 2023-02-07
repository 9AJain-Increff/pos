package com.increff.pos.util;

import com.increff.pos.model.form.*;
import com.increff.pos.exception.ApiException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static com.increff.pos.util.DateTimeUtil.formatEndDate;
import static com.increff.pos.util.DateTimeUtil.formatStartDate;

public class ValidationUtil {
    private static final String EMAIL_PATTERN = "[a-z\\d]+@[a-z]+\\.[a-z]{2,3}";

    public static boolean isBlank(String input) {
        return input == null || input.trim().isEmpty();
    }


    public static boolean isNegative(Float number) {
        return number == null || number <= 0;
    }

    public static boolean isNegative(Integer number) {
        return number == null || number < 0;
    }

    public static boolean isValidEmail(String email) {
        return email.matches(EMAIL_PATTERN);
    }

    public static void validateBrandForm(BrandForm form) throws ApiException {
        if (isBlank(form.getName())) {
            throw new ApiException("name cannot be empty");
        }
        if (isBlank(form.getCategory())) {
            throw new ApiException("category cannot be empty");
        }
    }

    public static void validateInventoryForm(InventoryForm form) throws ApiException {
        if (isBlank(form.getBarcode())) {
            throw new ApiException("barcode cannot be empty");
        }


    }

    public static void validateOrderForm(OrderItemForm form) throws ApiException {

        if (isBlank(form.getBarcode())) {
            // FIXED: 29/01/23 brand?
            throw new ApiException("barcode cannot be empty");
        }

        if (isNegative(form.getSellingPrice())) {
            throw new ApiException("enter a valid price");
        }
        if (isNegative(form.getQuantity())) {
            throw new ApiException("enter a valid quantity");
        }

    }

    public static void validateProductForm(ProductForm form) throws ApiException {
        if (isBlank(form.getName())) {
            throw new ApiException("name cannot be empty");
        }
        if (isBlank(form.getBrandName())) {
            throw new ApiException("brand cannot be empty");
        }
        if (isBlank(form.getBrandCategory())) {
            throw new ApiException("category cannot be empty");
        }
        if (isNegative(form.getPrice())) {
            throw new ApiException("Invalid input: price can only be a positive number!");
        }
        if (isBlank(form.getBarcode())) {
            throw new ApiException("barcode cannot be empty");
        }
    }
    public static LocalDateTime getStartTime(LocalDateTime start){
        LocalDateTime startTime ;
        if(start== null){
            startTime = LocalDateTime.MIN;;
        }
        else {
            startTime = formatStartDate(start);
        }
        return startTime;
    }

    public static LocalDateTime getEndTime(LocalDateTime end){
        LocalDateTime endTime;
        if(end== null){
            endTime = LocalDateTime.MAX;;
        }
        else {
            endTime = formatEndDate(end);
        }
        return endTime;
    }



    public static void validateSalesReport(SalesForm form){
        isBlank(form.getBrandName());
        isBlank(form.getBrandCategory());
        form.setStartTime(getStartTime(form.getStartTime()));

    }

}
