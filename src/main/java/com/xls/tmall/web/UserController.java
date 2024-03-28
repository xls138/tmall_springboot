
package com.xls.tmall.web;
import com.xls.tmall.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xls.tmall.service.UserService;
import com.xls.tmall.util.Page4Navigator;
 
@RestController
public class UserController {
	@Autowired UserService userService;

	//1. admin_user_list 导致 listUser.html 传递给浏览器
    //2. html 里的js 通过 axios.js 访问 users路径
    //3. UserController 映射 users, 返回 user 集合对应的json 数组
    //4. vue拿到json数组，通过v-for 遍历到视图上
    @GetMapping("/users")
    public Page4Navigator<User> list(@RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
    	start = start<0?0:start;
    	Page4Navigator<User> page = userService.list(start,size,5); 
        return page;
    }
	    

        
}


