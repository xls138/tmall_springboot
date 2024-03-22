
package com.xls.tmall.service;

import com.xls.tmall.dao.ProductDAO;
import com.xls.tmall.es.ProductESDAO;
import com.xls.tmall.pojo.Category;
import com.xls.tmall.pojo.Product;
import com.xls.tmall.util.Page4Navigator;
import com.xls.tmall.util.SpringContextUtil;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames = "products")
public class ProductService {

    @Autowired
    ProductDAO productDAO;
    @Autowired
    ProductESDAO productESDAO;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;

    @CacheEvict(allEntries = true)
    public void add(Product bean) {
        productDAO.save(bean);
        productESDAO.save(bean);
    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        productDAO.delete(id);
        productESDAO.delete(id);
    }

    @Cacheable(key = "'products-one-'+ #p0")
    public Product get(int id) {
        return productDAO.findOne(id);
    }

    @CacheEvict(allEntries = true)
    public void update(Product bean) {
        productDAO.save(bean);
        productESDAO.save(bean);
    }

    @Cacheable(key = "'products-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Product> list(int cid, int start, int size, int navigatePages) {
        Category category = categoryService.get(cid);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page<Product> pageFromJPA = productDAO.findByCategory(category, pageable);
        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

    //为多个分类填充产品集合
    public void fill(List<Category> categorys) {
        for (Category category : categorys) {
            fill(category);
        }
    }

    //查询某个分类下的所有产品
    @Cacheable(key = "'products-cid-'+ #p0.id")
    public List<Product> listByCategory(Category category) {
        return productDAO.findByCategoryOrderById(category);
    }

    //为分类填充产品集合
    public void fill(Category category) {
        ProductService productService = SpringContextUtil.getBean(ProductService.class);
        //获取产品列表
        List<Product> products = productService.listByCategory(category);
        //设置产品的第一张图片
        productImageService.setFirstProductImages(products);
        //为分类填充产品集合
        category.setProducts(products);
    }

    //为多个分类填充推荐产品集合，即把分类下的产品集合，按照8个为一行，拆成多行，以利于后续页面上进行显示
    public void fillByRow(List<Category> categorys) {
        int productNumberEachRow = 8;
        for (Category category : categorys) {
            List<Product> products = category.getProducts();
            List<List<Product>> productsByRow = new ArrayList<>();
            for (int i = 0; i < products.size(); i += productNumberEachRow) {
                int size = i + productNumberEachRow;
                size = size > products.size() ? products.size() : size;
                List<Product> productsOfEachRow = products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }

    //为产品设置销量和评价数量
    public void setSaleAndReviewNumber(Product product) {
        int saleCount = orderItemService.getSaleCount(product);
        product.setSaleCount(saleCount);


        int reviewCount = reviewService.getCount(product);
        product.setReviewCount(reviewCount);

    }


    public void setSaleAndReviewNumber(List<Product> products) {
        for (Product product : products)
            setSaleAndReviewNumber(product);
    }

    //通过 ProductESDAO 到 elasticsearch 中进行查询了，查询的结果是分页的，所以返回的是 Page 对象。
    public List<Product> search(String keyword, int start, int size) {
        initDatabase2ES();
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
                .add(QueryBuilders.matchPhraseQuery("name", keyword),
                        ScoreFunctionBuilders.weightFactorFunction(100))
                .scoreMode("sum")
                .setMinScore(10);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(functionScoreQueryBuilder).build();

        Page<Product> page = productESDAO.search(searchQuery);
        return page.getContent();
    }

    //初始化数据到es. 因为数据刚开始都在数据库中，不在es中，所以刚开始查询，先看看es有没有数据，如果没有，就把数据从数据库同步到es中
    private void initDatabase2ES() {
        Pageable pageable = new PageRequest(0, 5);
        Page<Product> page = productESDAO.findAll(pageable);
        if (page.getContent().isEmpty()) {
            List<Product> products = productDAO.findAll();
            for (Product product : products) {
                productESDAO.save(product);
            }
        }
    }

}

