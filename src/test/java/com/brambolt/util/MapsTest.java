package com.brambolt.util;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.brambolt.util.Maps.merge;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MapsTest {

    Map<String, Object> createLeft() {
        Map<String, Object> left = new HashMap<>();
        left.put("a", 1);
        left.put("b", 2);
        Map<String, Object> c = new HashMap<>();
        c.put("ca", 1);
        left.put("c", c);
        return left;
    }

    Map<String, Object> createRight() {
        Map<String, Object> right = new HashMap<>();
        Map<String, Object> d = new HashMap<>();
        d.put("da", 1);
        right.put("c", d);
        return right;
    }

    @Test
    public void testMerge() {
        Map<String, Object> left = createLeft();
        Map<String, Object> right = createRight();
        Map<String, Object> merged = merge(left, right);
        // The left and right do not conflict! (Intersection is alright...)
        assertContains(merged, left);
        assertContains(merged, right);
    }

    @SuppressWarnings("unchecked")
    void assertContains(Map<String, Object> map, Map<String, Object> partial) {
        partial.keySet().forEach((String key) -> {
            assertTrue(map.containsKey(key));
            Object fromPartial = partial.get(key);
            Object fromMap = map.get(key);
            if (fromPartial instanceof Map) {
                assertTrue(fromMap instanceof Map);
                assertContains((Map<String, Object>) fromMap, (Map<String, Object>) fromPartial);
            } else assertEquals(fromMap, fromPartial);
        });
    }
}
