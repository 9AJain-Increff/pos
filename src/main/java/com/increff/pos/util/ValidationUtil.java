package com.increff.pos.util;

import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.exception.ApiException;

import static com.increff.pos.util.Normalization.normalize;

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

        if (isNegative(form.getQuantity())) {
            throw new ApiException("quantity cannot be negative");
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

}
