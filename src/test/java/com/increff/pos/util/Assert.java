package com.increff.pos.util;

import com.increff.pos.model.BrandData;
import com.increff.pos.pojo.BrandPojo;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class Assert {

    public static <T> void assertEqualList(
            List<T> expectedList,
            List<T> actualList,
            AssertEqual<T> assertEqual) {

        assertEquals(expectedList.size(), actualList.size());

        for (int i = 0; i < expectedList.size(); i++) {
            T expected = expectedList.get(i);
            T actual = actualList.get(i);
            assertEqual.invoke(expected, actual);
        }
    }

    public static void assertEqualBrandData(BrandData expected, BrandData actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCategory(), actual.getCategory());
        assertEquals(expected.getName(), actual.getName());
    }
    public static void assertEqualBrands(BrandPojo expected, BrandPojo actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getCategory(), actual.getCategory());
    }

    public interface AssertEqual<T> {

        void invoke(T expected, T actual);

    }
}
