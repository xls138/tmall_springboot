
package com.xls.tmall.web;

import com.xls.tmall.pojo.Product;
import com.xls.tmall.service.CategoryService;
import com.xls.tmall.service.ProductImageService;
import com.xls.tmall.service.ProductService;
import com.xls.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
 
@RestController
public class ProductController {
    //通过自动装配（@Autowired），它注入了 ProductService 和 CategoryService 以便在请求处理方法中使用。
	@Autowired ProductService productService;
	@Autowired CategoryService categoryService;
	@Autowired ProductImageService productImageService;

    @GetMapping("/categories/{cid}/products")
    public Page4Navigator<Product> list(@PathVariable("cid") int cid, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
    	start = start<0?0:start;
    	Page4Navigator<Product> page =productService.list(cid, start, size,5 );
    	//为每个产品设置首张图片
        productImageService.setFirstProductImages(page.getContent());

    	return page;
    }
    
    @GetMapping("/products/{id}")
    public Product get(@PathVariable("id") int id) throws Exception {
    	Product bean=productService.get(id);
    	return bean;
    }
    
    
    @PostMapping("/products")
    public Object add(@RequestBody Product bean) throws Exception {
    	bean.setCreateDate(new Date());
        productService.add(bean);
        return bean;
    }
    
    
    
    @DeleteMapping("/products/{id}")
    public String delete(@PathVariable("id") int id, HttpServletRequest request)  throws Exception {
        productService.delete(id);
        return null;
    }
    
    @PutMapping("/products")
    public Object update(@RequestBody Product bean) throws Exception {
        productService.update(bean);
        return bean;
    }
}




