package jdk11.collection;

import java.util.HashMap;

/**
 * @author no-today
 * @date 2021/04/13 下午2:53
 */
public class HashMapTest {

    public static void main(String[] args) {
        var map = new HashMap<Integer, Integer>(2, 1);
        map.put(1, 0);
        map.put(2, 0);
        System.out.println();
    }
}
