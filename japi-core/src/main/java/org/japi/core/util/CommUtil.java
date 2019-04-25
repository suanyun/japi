package org.japi.core.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Random;

@Slf4j
public class CommUtil {

    /**
     * 判断一个String是否为null或者空串?
     *
     * @param string
     * @return
     */
    public static boolean isNull(String string) {
        if (string == null || "".equals(string)) {
            return true;
        }
        return false;
    }

    /**
     * Description:判断是否有空值或者null，只要有一个是空串或者null，就返回true
     * 因为在有些场景，不允许任何一个为空串或者null。
     *
     * @param [mutiStr]
     * @return boolean
     * @author dbdu
     * @date 18-5-3 上午9:39
     */
    public static boolean isMutiHasNull(String... mutiStr) {
        //是否全是空串或者null
        boolean hasNull = false;
        if (mutiStr.length > 0) {
            //说明有元素，不是空的
            for (String str : mutiStr) {
                if (isNull(str)) {
                    hasNull = true;
                    break;
                }
            }
        }

        return hasNull;
    }

    public static boolean isEmptyList(Collection<?> list) {
        if (null == list || list.isEmpty()) {
            return true;
        } else {
            boolean allNull = true;
            for (Object object : list) {
                if (null != object) {
                    allNull = false;
                }
            }
            return allNull;
        }
    }

    public static boolean isEmptyString(String str) {
        return (str == null || "".equals(str.trim()));
    }

    public static boolean isEmptyLong(String l) {
        return (isEmptyString(l) || "-1".equals(l.trim()));
    }

    public static boolean isEmptyLong(Long l) {
        return (null == l || -1 == l);
    }

    public static boolean isEmptyInt(Integer i) {
        return (null == i || -1 == i);
    }

    public static boolean isEmptyBoolean(Boolean i) {
        return (null == i);
    }

    public static String arrayJoin(char separate, Object[] arr, int size) {
        StringBuilder sb = new StringBuilder();
        if (arr != null) {
            Object obj = null;
            for (int i = 0; i < size && i < arr.length; i++) {
                obj = arr[i];
                if (obj == null)
                    continue;
                sb.append(obj.toString()).append(separate);
            }
        }
        if (sb.length() > 0 && separate == sb.charAt(sb.length() - 1)) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String arrayJoin(char separate, List<?> arr, int size) {
        StringBuilder sb = new StringBuilder();
        if (arr != null) {
            Object obj = null;
            for (int i = 0; i < size && i < arr.size(); i++) {
                obj = arr.get(i);
                if (obj == null)
                    continue;
                sb.append(obj.toString()).append(separate);
            }
        }
        if (sb.length() > 0 && separate == sb.charAt(sb.length() - 1)) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String getDayHourTime(long time) {
        if (time <= 0)
            return "";
        StringBuilder sb = new StringBuilder();
        long days = time / 24 * 60 * 60;
        long hours = (time % (24 * 60 * 60)) / (60 * 60);
        long mins = ((time % (24 * 60 * 60)) % (60 * 60)) / 60;
        long secs = ((time % (24 * 60 * 60)) % (60 * 60)) % 60;
        sb.append(days).append(" days ").append(hours).append(" hours ").append(mins).append(" minutes ").append(secs)
                .append(" seconds");
        return sb.toString();

    }

    public static boolean isEmpty(String value) {
        int strLen;
        if ((value == null) || ((strLen = value.length()) == 0)) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric(Object obj) {
        if (obj == null) {
            return false;
        }
        char[] chars = obj.toString().toCharArray();
        int length = chars.length;
        if (length < 1) {
            return false;
        }
        int i = 0;
        if ((length > 1) && (chars[0] == '-')) {
        }
        for (i = 1; i < length; i++) {
            if (!Character.isDigit(chars[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean areNotEmpty(String... values) {
        boolean result = true;
        if ((values == null) || (values.length == 0)) {
            result = false;
        } else {
            for (String value : values) {
                result &= !isEmpty(value);
            }
        }
        return result;
    }

    /**
     * Description: 将元素集合转换为字符串
     *
     * @param [ids]
     * @return java.lang.String
     * @author dbdu
     * @date 19-4-20 下午2:44
     */
    public static String idListToString(Collection<?> ids) {
        if (CommUtil.isEmptyList(ids)) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append(" (");
            for (Object id : ids) {
                sb.append("'");
                sb.append(id.toString().trim());
                sb.append("',");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(") ");
            return sb.toString();
        }
    }

    /**
     * 分割驼峰字段
     *
     * @param name
     * @param separator
     * @return
     */
    private static String separateCamelCase(String name, String separator) {
        StringBuilder translation = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char character = name.charAt(i);
            if (Character.isUpperCase(character) && translation.length() != 0) {
                translation.append(separator);
            }
            translation.append(character);
        }
        return translation.toString();
    }

    /**
     * 下划线转换为驼峰
     */
    public static String underscore2CamelCase(String name) {
        StringBuilder translation = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char character = name.charAt(i);
            if (character == '_')
                continue;
            if (translation.length() != 0 && name.charAt(i - 1) == '_') {
                translation.append(Character.toUpperCase(character));
            } else {
                translation.append(character);
            }
        }
        return translation.toString();
    }

    /**
     * 驼峰转换为下划线
     *
     * @param name
     * @return
     */
    public static String camelCase2Underscore(String name) {
        return separateCamelCase(name, "_").toLowerCase();
    }


    /**
     * Description:生成随机字符创,由数字大小写字母组成
     * length指定生成字符串的长度
     *
     * @param [length]
     * @return java.lang.String
     * @author dbdu
     * @date 19-4-11 上午11:41
     */
    public static String randomString(int length) {
        // 允许的字符
        String ku = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        // 定义一个StringBuilder用以保存生成的字符串，这里不选用String和StringBuffer（String长度不可变，StringBuffer没有StringBuilder快）
        StringBuilder sb = new StringBuilder();
        // 创建一个Random用以生成伪随机数，也可采用Math.random()来实现
        Random r = new Random();
        for (int i = 0; i < length; i++) {
            // 得到随机字符
            int r2 = r.nextInt(ku.length());
            sb.append(ku.charAt(r2));
        }

        return sb.toString();
    }

}
