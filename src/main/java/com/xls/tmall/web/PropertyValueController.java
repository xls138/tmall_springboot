
package com.xls.tmall.web;
import java.util.List;

import com.xls.tmall.pojo.Product;
import com.xls.tmall.pojo.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.xls.tmall.service.ProductService;
import com.xls.tmall.service.PropertyValueService;
 
@RestController
public class PropertyValueController {
	@Autowired PropertyValueService propertyValueService;
	@Autowired ProductService productService;

	//1. 通过产品管理界面的设置属性，跳到编辑页面：admin_propertyValue_edit
    //2. 获取 editPropertyValue.html 页面
    //3. html中的js代码获取到 pid
    //4. axios 通过 products/pid/propertyValues 提交ajax 请求
    //5. PropertyValueController 的 list方法相应这个请求
    //6. list 方法中 首先进行初始化：propertyValueService.init(product); ， 然后把这个产品对应的属性值都取出来返回的浏览器
    //7. 浏览器拿到 json 数组， 遍历到视图上
    @GetMapping("/products/{pid}/propertyValues")
    public List<PropertyValue> list(@PathVariable("pid") int pid) throws Exception {
    	Product product = productService.get(pid);
    	propertyValueService.init(product);
    	List<PropertyValue> propertyValues = propertyValueService.list(product);
    	return propertyValues;
    }

    //1. 监听输入框上的keyup事件，调用Vue的update函数
    //2. 把边框的颜色修改为黄色，表示正在修改的意思
    //3. 通过axios.js 把修改后的数据提交到 PropertyValueController 的update 方法
    //4. 修改成功后返回被修改后的propertyValue json对象
    //5. 判断返回值的id是否一致，如果一致就表示修改成功了，即边框修改为绿色，否则修改为红色，表示没有修改成功。
    @PutMapping("/propertyValues")
    public Object update(@RequestBody PropertyValue bean) throws Exception {
    	propertyValueService.update(bean);
        return bean;
    }
    
  
}


