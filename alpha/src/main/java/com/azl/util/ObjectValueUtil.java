package com.azl.util;

import android.support.annotation.NonNull;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhongq on 2016/10/12.
 * 对嵌套层次较深的对象快速安全取值
 */
public class ObjectValueUtil {

    private static Object mLock = new Object();
    private static ObjectValueUtil instance;

    private ObjectValueUtil() {
    }

    public static ObjectValueUtil getInstance() {

        if (instance == null) {
            synchronized (mLock) {
                if (instance == null) {
                    instance = new ObjectValueUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 获得指定路径的对象
     *
     * @param pObj
     * @param instructions 指令格式 data/items/appraise/list/total
     * @return
     */
    public Object getValueObject(Object pObj, String instructions) {
        String[] arr = instructions.split("/");
        return getValueObject(pObj, (Object[]) arr);
    }

    /**
     * 获得指定路径的对象
     *
     * @param pObj
     * @param keyArr
     * @return
     */
    public Object getValueObject(Object pObj, Object... keyArr) {
        return resolveObject(pObj, keyArr);
    }

    /**
     * 获得指定路径的对象
     *
     * @param pObj
     * @param c      具体类型
     * @param keyArr
     * @param <T>
     * @return
     */
    public <T> T getSpecificTypeObject(Object pObj, Class<T> c, String keyArr) {
        Object obj = getValueObject(pObj, keyArr);
        if (obj != null) {
            return (T) obj;
        }
        return null;
    }

    /**
     * 获得指定路径的对象
     *
     * @param pObj
     * @param c      具体类型
     * @param keyArr
     * @param <T>
     * @return
     */
    public <T> T getSpecificTypeObject(Object pObj, Class<T> c, Object... keyArr) {
        Object obj = getValueObject(pObj, keyArr);
        if (obj != null) {
            return (T) obj;
        }
        return null;
    }

    /**
     * 判断指定数据是否为null
     *
     * @param pObj
     * @param keyArr
     * @return
     */
    public boolean isNull(@NonNull Object pObj, @NonNull Object... keyArr) {
        Object obj = getValueObject(pObj, keyArr);
        return obj == null;
    }

    /**
     * 判断指定数据是否为null
     *
     * @param pObj
     * @param instructions 指令格式 data/items/appraise/list/total
     * @return
     */
    public boolean isNull(@NonNull Object pObj, @NonNull String instructions) {
        String[] arr = instructions.split("/");
        return isNull(pObj, (Object[]) arr);
    }

    /**
     * 获取数组，集合，map的长度 没有找到指定数据默认为0
     *
     * @param pObj
     * @param instructions 指令格式 data/items/appraise/list/total
     * @return
     */
    public int getCollectionSize(@NonNull Object pObj, @NonNull String instructions) {
        String[] arr = instructions.split("/");
        return getCollectionSize(pObj, (Object[]) arr);
    }

    /**
     * 获取数组，集合，map的长度 没有找到指定数据默认为0
     *
     * @param pObj
     * @return
     */
    public int getCollectionSize(@NonNull Object pObj, @NonNull Object... keyArr) {
        Object object = getValueObject(pObj, keyArr);
        int length = 0;
        if (object == null) return length;
        if (object instanceof Map) {
            Map map = (Map) object;
            length = map.size();
        } else if (object instanceof List) {
            List list = (List) object;
            length = list.size();
        } else if (object.getClass().isArray()) {
            Object[] arr = (Object[]) object;
            length = arr.length;
        }
        return length;
    }

    private Object resolveObject(Object pObj, Object[] keyArr) {
        try {
            if (pObj == null) {
                throw new IllegalArgumentException("Value Object cannot be empty");
            }
            if (keyArr == null) {
                throw new IllegalArgumentException("An array of keys cannot be empty");
            }
            if (keyArr.length == 0) {
                return pObj;
            }
            Object obj = pObj;
            for (int i = 0; i < keyArr.length; i++) {
                if (obj == null) return null;
                Object key = keyArr[i];
                Type objectType = judgeObjectType(obj);
                KeyType keyType = judgeKeyType(key);
                if (objectType == Type.ARRAY) {
                    obj = getObjectArray(obj, key);
                } else if (objectType == Type.LIST) {
                    obj = getListObject(obj, key);
                } else if (objectType == Type.MAP) {
                    obj = getMapObject(obj, key);

                } else {
                    if (keyType == KeyType.MAP) {
                        obj = getMapObject(obj, key);
                    } else {
                        obj = getObject(obj, key, keyType);
                    }
                }
            }
            return obj;
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }

    private Object getObject(Object obj, Object key, KeyType keyType) {
        try {
            Class cl = obj.getClass();
            if (keyType != KeyType.STRING) {
                throw new IllegalArgumentException("You cannot pass such key:" + key + "  type:" + key.getClass());
            }
            Field field = cl.getDeclaredField(String.valueOf(key));
            field.setAccessible(true);
            obj = field.get(obj);
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
        return obj;
    }

    private Object getMapObject(Object obj, Object key) {
        Map map = (Map) obj;
        if (key instanceof MapKey) {
            return map.get(((MapKey) key).mapKey);
        }
        Set keySet = map.keySet();
        Iterator inter = keySet.iterator();
        if (inter == null || !inter.hasNext()) {
            return null;
        }
        Object o = inter.next();
        if (o instanceof Integer) {
            int iKey = Integer.valueOf((String) key);
            return map.get(iKey);
        } else if (o instanceof Float) {
            float fKey = Float.valueOf((String) key);
            return map.get(fKey);
        } else if (o instanceof Double) {
            double dKey = Double.valueOf((String) key);
            return map.get(dKey);
        } else if (o instanceof Byte) {
            byte dKey = Byte.valueOf((String) key);
            return map.get(dKey);
        } else {
            return map.get(key);
        }
    }

    private Object getListObject(Object obj, Object key) {
        List list = (List) obj;
        int index;
        try {
            index = Integer.valueOf((String) key);
        } catch (Exception e) {
            Exception m = new IllegalArgumentException("You cannot pass such key:" + key + "   keyType:" + key.getClass());
//            m.printStackTrace();
            return null;
        }

        if (index >= list.size()) {
            return null;
        }
        obj = list.get(index);
        return obj;
    }

    private Object getObjectArray(Object obj, Object key) {
        int index;
        try {
            index = Integer.valueOf((String) key);
        } catch (Exception e) {
//            Exception m = new IllegalArgumentException("You cannot pass such key:" + key + "   keyType:" + key.getClass());
//            m.printStackTrace();
            return null;
        }
        obj = Array.get(obj, index);
        return obj;
    }

    private Type judgeObjectType(Object object) {
        if (object instanceof Map) {
            return Type.MAP;
        } else if (object instanceof List) {
            return Type.LIST;
        } else if (object.getClass().isArray()) {
            return Type.ARRAY;
        } else {
            return Type.OBJECT;
        }
    }

    private KeyType judgeKeyType(Object key) {
        if (key instanceof MapKey) {
            return KeyType.MAP;
        } else if (key instanceof Integer) {
            return KeyType.INTEGER;
        } else if (key instanceof String) {
            return KeyType.STRING;
        } else {
            throw new IllegalArgumentException("You cannot pass such key:" + key.getClass());
        }
    }

    public static class MapKey {
        public MapKey(Object key) {
            this.mapKey = key;
        }

        Object mapKey;
    }

    enum Type {
        OBJECT, MAP, LIST, ARRAY
    }

    enum KeyType {
        MAP, STRING, INTEGER
    }
}
