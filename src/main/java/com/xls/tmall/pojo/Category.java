
package com.xls.tmall.pojo;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//这个注解表示Category类是一个JPA实体。它将被映射到数据库表
@Entity
//指定这个实体映射到的数据库表名为category
@Table(name = "category")
//用于在JSON序列化/反序列化时忽略指定的属性。这里用于处理JPA代理和延迟加载相关的问题
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})

public class Category {
    //表示是一个主键
    @Id
    //表明自增长方式
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //表明对应的数据库字段名为id
    @Column(name = "id")
    int id;

    String name;

    //定义了一个Product对象的列表，用于存储与此类别相关的产品。由于使用了@Transient，这个属性不会映射到数据库表中
    @Transient
    List<Product> products;
    //也是一个使用@Transient标注的属性，表示产品的列表，但是以行的形式组织
    @Transient
    List<List<Product>> productsByRow;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    //用于获取产品列表，但是以行的形式组织
    public List<List<Product>> getProductsByRow() {
        return productsByRow;
    }

    public void setProductsByRow(List<List<Product>> productsByRow) {
        this.productsByRow = productsByRow;
    }

    //重写toString方法，方便打印输出
    @Override
    public String toString() {
        return "Category [id=" + id + ", name=" + name + "]";
    }
}

