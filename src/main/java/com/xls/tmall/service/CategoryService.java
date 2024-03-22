
package com.xls.tmall.service;

import java.util.List;

import com.xls.tmall.dao.CategoryDAO;
import com.xls.tmall.pojo.Category;
import com.xls.tmall.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.xls.tmall.util.Page4Navigator;

@Service
@CacheConfig(cacheNames = "categories")
public class CategoryService {
    @Autowired
    CategoryDAO categoryDAO;

    //这里使用了缓存，所以在增删改的时候，都要清除缓存
    @CacheEvict(allEntries = true)
    public void add(Category bean) {
        categoryDAO.save(bean);
    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        categoryDAO.delete(id);
    }

    //第一次访问的时候， redis 是不会有数据的，所以就会通过 jpa 到数据库里去取出来，一旦取出来之后，就会放在 redis里
    @Cacheable(key = "'categories-one-'+ #p0")
    public Category get(int id) {
        Category c = categoryDAO.findOne(id);
        return c;
    }

    @CacheEvict(allEntries = true)
    public void update(Category bean) {
        categoryDAO.save(bean);
    }

    //就是key不一样，数据不再是一个对象，而是一个集合。 （保存在 redis 里是一个 json 数组）
    //如图所示 categories-page-0-5 就是第一页数据
    @Cacheable(key = "'categories-page-'+#p0+ '-' + #p1")
    public Page4Navigator<Category> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page pageFromJPA = categoryDAO.findAll(pageable);

        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

    @Cacheable(key = "'categories-all'")
    public List<Category> list() {
        //首先创建一个 Sort 对象，表示通过 id 倒排序， 然后通过 categoryDAO进行查询
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return categoryDAO.findAll(sort);
    }

    //这个方法的用处是删除Product对象上的分类。在对分类做序列还转换为 json 的时候，会遍历里面的 products, 然后遍历出来的产品上，又会有分类，
    //而在这里去掉，就没事了。 只要在前端业务上，没有通过产品获取分类的业务，去掉也没有关系

    //此方法接收一个类别列表，并对每个类别调用 removeCategoryFromProduct(Category category) 方法
    public void removeCategoryFromProduct(List<Category> cs) {
        for (Category category : cs) {
            removeCategoryFromProduct(category);
        }
    }

    //此方法接收一个类别对象，然后遍历与此类别关联的所有产品，将它们的类别设为 null。这里有两个不同的产品列表：products 和 productsByRow
    public void removeCategoryFromProduct(Category category) {
        List<Product> products = category.getProducts();
        if (null != products) {
            for (Product product : products) {
                product.setCategory(null);
            }
        }

        List<List<Product>> productsByRow = category.getProductsByRow();
        if (null != productsByRow) {
            for (List<Product> ps : productsByRow) {
                for (Product p : ps) {
                    p.setCategory(null);
                }
            }
        }
    }
}

