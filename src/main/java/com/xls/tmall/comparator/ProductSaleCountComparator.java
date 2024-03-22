package com.xls.tmall.comparator;


import java.util.Comparator;

import com.xls.tmall.pojo.Product;
//把 销量高的放前面
public class ProductSaleCountComparator implements Comparator<Product> {

    @Override
    public int compare(Product p1, Product p2) {
        return p2.getSaleCount() - p1.getSaleCount();
    }

}

