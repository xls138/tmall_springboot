
package com.xls.tmall.es;

import com.xls.tmall.pojo.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductESDAO extends ElasticsearchRepository<Product, Integer> {

}

