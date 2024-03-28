
package com.xls.tmall.web;

import com.xls.tmall.pojo.Category;
import com.xls.tmall.service.CategoryService;
import com.xls.tmall.util.ImageUtil;
import com.xls.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

//表示这是一个控制器，并且对每个方法的返回值都会直接转换为 json 数据格式。
@RestController
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/categories")
    public Page4Navigator<Category> list(@RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        //start表示从第几页开始，size表示每页显示的数量。默认值分别是0和5。
        start = start < 0 ? 0 : start;
        Page4Navigator<Category> page = categoryService.list(start, size, 5);  //5表示导航分页最多有5个，像 [1,2,3,4,5] 这样
        return page;
    }

    @PostMapping("/categories")
    public Object add(Category bean, MultipartFile image, HttpServletRequest request) throws Exception {
        categoryService.add(bean);
        saveOrUpdateImageFile(bean, image, request);
        return bean;
    }

    public void saveOrUpdateImageFile(Category bean, MultipartFile image, HttpServletRequest request)
            throws IOException {
        //这一行代码创建了一个File对象，它代表了服务器上用于存储分类图片的文件夹
        File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
        //加上.jpg后缀
        File file = new File(imageFolder, bean.getId() + ".jpg");
        if (!file.getParentFile().exists())
            //如果父文件夹不存在，就创建父文件夹
            file.getParentFile().mkdirs();
        //通过transferTo()方法，把浏览器传递过来的图片保存在上述指定的位置
        image.transferTo(file);
        //通过ImageUtil.change2jpg(file)确保图片格式一定是jpg，而不仅仅是后缀名是.jpg
        BufferedImage img = ImageUtil.change2jpg(file);
        //写入图片
        ImageIO.write(img, "jpg", file);
    }

    @DeleteMapping("/categories/{id}")
    public String delete(@PathVariable("id") int id, HttpServletRequest request) throws Exception {
        categoryService.delete(id);
        File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, id + ".jpg");
        file.delete();
        return null;
    }

    @GetMapping("/categories/{id}")
    public Category get(@PathVariable("id") int id) throws Exception {
        //bean是一个局部变量，用来存储获取到的Category对象
        Category bean = categoryService.get(id);
        return bean;
    }

    @PutMapping("/categories/{id}")
    //分类信息、图片文件和请求信息
    public Object update(Category bean, MultipartFile image, HttpServletRequest request) throws Exception {
        String name = request.getParameter("name");
        bean.setName(name);
        categoryService.update(bean);

        if (image != null) {
            saveOrUpdateImageFile(bean, image, request);
        }
        return bean;
    }

}


