package com.dumas.pedestal.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;

/**
 * @author xph
 * @Date 2020-04-25
 */
public class BeanUtil {

    private static final ConcurrentHashMap<String, BeanCopier> CACHE_COPIER_MAP = new ConcurrentHashMap<>();


    /**
     * 复制对象属性
     *
     * @param srcObj    源对象
     * @param targetObj 目标对象
     * @param converter converter转换器
     */
    public static <T> T copyObject(Object srcObj, T targetObj, Converter converter) {

        if (null == srcObj) {
            return null;
        }

        if (null == targetObj) {
            return null;
        }
        BeanCopier bc = getBeanCopierInstance(srcObj.getClass(), targetObj.getClass(), converter);
        bc.copy(srcObj, targetObj, converter);
        return targetObj;
    }

    @Deprecated
    public static <T> T copy(Object srcObj, T targetObj, Converter converter) {
        if (null == srcObj) {
            return null;
        }

        if (null == targetObj) {
            return null;
        }
        BeanCopier bc = BeanCopier.create(srcObj.getClass(), targetObj.getClass(), converter != null);
        bc.copy(srcObj, targetObj, converter);
        return targetObj;
    }

    /**
     * 复制对象属性
     *
     * @param srcObj    源对象
     * @param targetObj 目标对象
     */
    public static <T> T copyObject(Object srcObj, T targetObj) {
        return copyObject(srcObj, targetObj, null);
    }

    /**
     * 复制对象属性
     *
     * @param srcObj 源对象
     */
    public static <T> T copyObject(Object srcObj, Class<T> targetElementClass) {
        if (srcObj == null || targetElementClass == null) {
            return null;
        }
        try {
            T targetObj = targetElementClass.newInstance();
            return copyObject(srcObj, targetObj, null);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Cannot instantiate an object of %s.", targetElementClass));
        }
    }

    /**
     * 复制列表中所有元素到新列表中.
     *
     * @param srcList            源列表
     * @param targetElementClass 目标列表元素class
     * @param <S>                源列表元素类型
     * @param <T>                目标列表元素类型
     * @return 目标列表
     */
    public static <S, T> List<T> copyList(Collection<S> srcList, Class<T> targetElementClass) {
        List<T> targetList = new ArrayList<T>();
        if (srcList == null || srcList.size() == 0) {
            return targetList;
        }
        for (S src : srcList) {
            try {
                T target = targetElementClass.newInstance();
                target = copyObject(src, target);
                targetList.add(target);
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format("Cannot instantiate an object of %s.", targetElementClass));
            }
        }
        return targetList;
    }

    private static <S, T> BeanCopier getBeanCopierInstance(Class<S> sourceClass, Class<T> targetClass, Converter converter) {
        String key = sourceClass.getName() + "#" + targetClass.getName();
        BeanCopier bc = CACHE_COPIER_MAP.get(key);
        if (null == bc) {
            bc = BeanCopier.create(sourceClass, targetClass, converter != null);
            CACHE_COPIER_MAP.put(key, bc);
        }
        return bc;
    }


    /**
     * 拷贝
     *
     * @param src
     * @param <T>
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static <T> List<T> deepCopy(Collection<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        in.setObjectInputFilter(filterInfo -> {
            Class<?> serialClass = filterInfo.serialClass();
            if (serialClass != null) {
                // Allow only java.base, java.util, and java.lang types
                String className = serialClass.getName();
                if (className.startsWith("java.util.") || className.startsWith("java.lang.")
                    || className.startsWith("[Ljava.util.") || className.startsWith("java.base/")) {
                    return ObjectInputFilter.Status.ALLOWED;
                }
                return ObjectInputFilter.Status.REJECTED;
            }
            return ObjectInputFilter.Status.UNDECIDED;
        });
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }
}


