package com.mybatisext.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by xiebo on 16/3/14.
 */
public class CollectionUtil {

    private static Logger logger = LoggerFactory.getLogger(CollectionUtil.class);


    public static <T> T uniqueResult(Collection<T> results) {
        int size = (results != null ? results.size() : 0);
        if (size == 0) {
            return null;
        }
        if (size > 1) {
            logger.warn("collection not unique");
        }
        return results.iterator().next();
    }

    public static <K, T> Map<K, List<T>> toListMap(Function<T, K> func, List<T> dataList) {
        Map<K, List<T>> resMap = new HashMap<>();
        dataList.forEach(data -> {
            K key = func.apply(data);
            List<T> list = resMap.getOrDefault(key, new ArrayList<T>());
            if (list.size() == 0) {
                resMap.put(key, list);
            }
            list.add(data);
        });
        return resMap;
    }

    public static <K, V, T> Map<K, List<V>> toListMap(Function<T, K> func, Function<T, V> targetFunc, List<T> dataList) {
        return toListMap(func, targetFunc, dataList, true);
    }

    public static <K, V, T> Map<K, List<V>> toListMap(Function<T, K> func, Function<T, V> targetFunc, List<T> dataList, boolean allowNullValue) {
        Map<K, List<V>> resMap = new HashMap<>();
        dataList.forEach(data -> {
            K key = func.apply(data);
            V value = targetFunc.apply(data);

            if(null!=value || allowNullValue){
                List<V> list = resMap.getOrDefault(key, new ArrayList<V>());
                if (list.size() == 0) {
                    resMap.put(key, list);
                }
                list.add(value);
            }
        });
        return resMap;
    }

    public static <K, V, T> LinkedHashMap<K, V> toLinkedHashMap(Function<T, K> keyFunc, Function<T, V> valueFunc, List<T> dataList){
        if(null == dataList || dataList.size() == 0){
            return new LinkedHashMap<>(0);
        }
        LinkedHashMap<K, V> resMap = new LinkedHashMap<>();
        dataList.forEach(data -> {
            K key = keyFunc.apply(data);
            if(null!=key){
                V value = valueFunc.apply(data);
                resMap.put(key, value);
            }
        });
        return resMap;
    }


    public static <K, T> LinkedHashMap<K, List<T>> toListLinkedHashMap(Function<T, K> func, List<T> dataList) {
        LinkedHashMap<K, List<T>> resMap = new LinkedHashMap<>();
        dataList.forEach(data -> {
            K key = func.apply(data);
            List<T> list = resMap.getOrDefault(key, new ArrayList<T>());
            if (list.size() == 0) {
                resMap.put(key, list);
            }
            list.add(data);
        });
        return resMap;
    }

    public static <T> ArrayList<T> toArrayList(Collection<T> c) {
        ArrayList<T> res = new ArrayList<>(c.size());
        res.addAll(c);
        return res;
    }

    public static <T> List<T> cutList(List<T> srcList, int max) {
        if (null == srcList || srcList.size() <= max) {
            return srcList;
        }
        return srcList.subList(0, max);
    }

    public static <T> List<List<T>> toListGroup(List<T> items, int cols) {

        if (null == items || items.size() == 0) {
            return Collections.emptyList();
        }
        if (cols < 1) {
            return Collections.singletonList(items);
        }

        List<List<T>> resList = new ArrayList<>();
        List<T> itemList = null;

        for (int i = 0; i < items.size(); i++) {
            if (null == itemList) {
                itemList = new ArrayList<>(cols);
            }

            itemList.add(items.get(i));

            if (((i+1) % cols == 0) || i == items.size() - 1) {
                List<T> row = new ArrayList<>(itemList.size());
                row.addAll(itemList.stream().collect(Collectors.toList()));
                resList.add(row);
                itemList = null;
            }
        }

        return resList;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return object -> seen.putIfAbsent(keyExtractor.apply(object), Boolean.TRUE) == null;
    }
//
//    public static void main(String[] args){
//        System.out.println(toListGroup(Arrays.asList(1,2,3,4,5,6,7,8), 3));
//    }

}
