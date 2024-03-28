
package com.xls.tmall.es;

import com.xls.tmall.pojo.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//ElasticsearchRepository<Product, Integer>是Spring Data Elasticsearch提供的一个接口，用于简化对Elasticsearch中数据的访问和操作。
//泛型参数Product指定了这个仓库将要处理的实体类型，而Integer指定了实体的ID类型。在这个例子中，Product实体的ID字段是整型。
//作为一个Elasticsearch仓库接口，ProductESDAO使得你可以非常方便地对Elasticsearch中的Product文档执行各种操作，如保存、删除、搜索等，而无需编写具体的代码实现。
//Spring Data Elasticsearch将在应用启动时自动实现这个接口，并提供一系列标准的CRUD（创建、读取、更新、删除）操作以及分页和排序支持。
//你还可以在这个接口中定义自定义的查询方法，Spring Data Elasticsearch将根据方法名或使用@Query注解提供的查询字符串来自动实现这些方法。
public interface ProductESDAO extends ElasticsearchRepository<Product, Integer> {

}

