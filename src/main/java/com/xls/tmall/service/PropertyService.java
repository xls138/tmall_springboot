
package com.xls.tmall.service;

import java.util.List;

import com.xls.tmall.dao.PropertyDAO;
import com.xls.tmall.pojo.Category;
import com.xls.tmall.pojo.Property;
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
@CacheConfig(cacheNames = "properties")
public class PropertyService {

    @Autowired
    PropertyDAO propertyDAO;
    @Autowired
    CategoryService categoryService;

    @CacheEvict(allEntries = true)
    public void add(Property bean) {

        propertyDAO.save(bean);
    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        propertyDAO.delete(id);
    }

    @Cacheable(key = "'properties-one-'+ #p0")
    public Property get(int id) {
        return propertyDAO.findOne(id);
    }

    @CacheEvict(allEntries = true)
    public void update(Property bean) {
        propertyDAO.save(bean);
    }

    @Cacheable(key = "'properties-cid-'+ #p0.id")
    public List<Property> listByCategory(Category category) {
        return propertyDAO.findByCategory(category);
    }


    @Cacheable(key = "'properties-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Property> list(int cid, int start, int size, int navigatePages) {
        //获取到对应的Category对象
        Category category = categoryService.get(cid);
        //创建一个Sort对象，表示通过id倒排序
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        //这个对象包含了分页的信息，比如当前页码start和每页显示的数量size
        Pageable pageable = new PageRequest(start, size, sort);
        //这个方法会根据分类和分页信息从数据库中查询属性列表
        Page<Property> pageFromJPA = propertyDAO.findByCategory(category, pageable);
        //这个封装类提供了一些额外的分页导航功能
        return new Page4Navigator<>(pageFromJPA, navigatePages);


    }

}

