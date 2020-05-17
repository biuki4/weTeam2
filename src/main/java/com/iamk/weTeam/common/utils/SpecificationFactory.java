package com.iamk.weTeam.common.utils;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 * jpa Specification 工具类
 * https://www.cnblogs.com/lxy061654/p/11386013.html
 * **/
@SuppressWarnings("unchecked")      // 清除屎黄色背景
public final class SpecificationFactory {

    /**
     * % %
     * @param attribute 实体类中的属性
     * @param value 模糊查询的值
     * @return
     */
    public static Specification containsLike(String attribute, String value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(attribute), "%" + value + "%");
    }


    /**
     * =
     * @param attribute
     * @param value
     * @return
     */
    public static Specification equal(String attribute, String value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(attribute), value);
    }

    public static Specification equal(String attribute, Integer value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(attribute), value);
    }

    /**
     * between
     * @param attribute
     * @param min
     * @param max
     * @return
     */
    public static Specification isBetween(String attribute, int min, int max) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(attribute), min, max);
    }

    public static Specification isBetween(String attribute, double min, double max) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(attribute), min, max);
    }

    public static Specification isBetween(String attribute, Date min, Date max) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(attribute), min, max);
    }

    /**
     * in
     * @param attribute
     * @param c
     * @return
     */
    public static Specification in(String attribute, Collection c) {
        return (root, query, criteriaBuilder) -> root.get(attribute).in(c);
    }

    public static Specification in(String attribute, String[] c) {
        return (root, query, criteriaBuilder) -> root.get(attribute).in(c);
    }

    public static Specification in(String attribute, Integer[] c) {
        return (root, query, criteriaBuilder) -> root.get(attribute).in(c);
    }

    /**
     * >
     * @param attribute
     * @param value
     * @return
     */
    public static Specification greaterThan(String attribute, BigDecimal value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(attribute), value);
    }

    public static Specification greaterThan(String attribute, Long value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(attribute), value);
    }

    public static Specification greaterThan(String attribute, Date value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(attribute), value);
    }


    /**
     * >=
     * @param attribute
     * @param value
     * @return
     */
    public static Specification greaterEqualThan(String attribute, BigDecimal value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(attribute), value);
    }

    public static Specification greaterEqualThan(String attribute, Long value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(attribute), value);
    }

    public static Specification greaterEqualThan(String attribute, Date value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(attribute), value);
    }

    /**
     * <=
     * @param attribute
     * @param value
     * @return
     */
    public static Specification lessThanOrEqualTo(String attribute, Date value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(attribute), value);
    }

    public static Specification lessThan(String attribute, Date value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get(attribute), value);
    }

    /**
     * 联表
     * @param tableName
     * @param attribute
     * @param value
     * @return
     */
    public static Specification join_equal(String tableName, String attribute, Integer value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join(tableName).get(attribute), value);
    }


    // public static Specification test() {
    //     return  (root, query, criteriaBuilder) -> root.join(Game.gameTags)
    // }

    // public static Specification<Game> equals(String attribute, String value) {
    //     return (root, query, criteriaBuilder) -> criteriaBuilder.equal(Game.c), value);
    // }

}
