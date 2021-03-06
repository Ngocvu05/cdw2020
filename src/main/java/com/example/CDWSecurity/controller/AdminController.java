package com.example.CDWSecurity.controller;

import com.example.CDWSecurity.model.DanhMuc;
import com.example.CDWSecurity.model.Images;
import com.example.CDWSecurity.model.SanPham;
import com.example.CDWSecurity.model.User;
import com.example.CDWSecurity.repository.RoleRepository;
import com.example.CDWSecurity.repository.UserRepository;
import com.example.CDWSecurity.service.DanhMucService;
import com.example.CDWSecurity.service.ImagesService;
import com.example.CDWSecurity.service.SanPhamService;
import com.example.CDWSecurity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("Admin")
public class AdminController {
    SanPham sanPham;
    @Autowired
    DanhMucService danhMucService;
    @Autowired
    SanPhamService sanPhamService;
    @Autowired
    ImagesService imagesService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    RoleRepository roleRepository;
//        Controller Danh Muc
    @GetMapping("/quanlydm")
    public String showDanhMuc(Model model, HttpServletRequest request){
    request.getSession().setAttribute("listdm", null);
    return "redirect:/Admin/qldm/page/1";
    }

//        phan trang danh muc
    @GetMapping("/qldm/page/{pageNumber}")
    public String showEmployeePage(HttpServletRequest request,HttpSession session,
                                   @PathVariable int pageNumber, Model model) {
        PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("listdm");
        int pagesize = 3;
        List<DanhMuc> list =(List<DanhMuc>) danhMucService.findAllDanhMuc();
        System.out.println(list.size());
        if (pages == null) {
            pages = new PagedListHolder<>(list);
            pages.setPageSize(pagesize);
        } else {
            final int goToPage = pageNumber - 1;
            if (goToPage <= pages.getPageCount() && goToPage >= 0) {
                pages.setPage(goToPage);
            }
        }
        request.getSession().setAttribute("listdm", pages);
        int current = pages.getPage() + 1;
        int begin = Math.max(1, current - list.size());
        int end = Math.min(begin + 5, pages.getPageCount());
        int totalPageCount = pages.getPageCount();
        String baseUrl = "/Admin/qldm/page/";
//        session.setAttribute("beginIndex", begin);
//        session.setAttribute("endIndex", end);
//        session.setAttribute("currentIndex", current);
//        session.setAttribute("totalPageCount", totalPageCount);
//        session.setAttribute("baseUrl", baseUrl);
//        session.setAttribute("categorys", pages);
//        session.setAttribute("listdanhmuc",danhMucService.findAllDanhMuc());
        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current);
        model.addAttribute("totalPageCount", totalPageCount);
        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("categorys", pages);
        model.addAttribute("listdanhmuc",danhMucService.findAllDanhMuc());
        return "Admin/admin-danhmuc";
    }

//     delete danh muc
    @GetMapping("/qldm/{id}/delete")
    public String delete(@PathVariable("id") long id) {
        DanhMuc danhMuc = danhMucService.findById(id);
        danhMucService.deleteDanhMuc(danhMuc);
        return "redirect:/Admin/quanlydm";
    }

//    insert danh muc
    @PostMapping(value = "/InsertDanhMuc")
    public String insertDanhMuc(@ModelAttribute("insertDanhMuc") DanhMuc danhMuc) {
        danhMucService.insertDanhMuc(danhMuc);
        return "redirect:/Admin/quanlydm";
    }
//    end controller danh muc

//    Controller San Pham
    @GetMapping("/quanlysp")
    public String quanlySP(Model model, HttpServletRequest request
        ,HttpSession session) {
    request.getSession().setAttribute("listsp", null);
    return "redirect:/Admin/qlsp/page/1";
    }

//        phan trang san pham
    @GetMapping("/Admin/quanlysp")
    public String quanlySP(Model model, HttpServletRequest request
            , RedirectAttributes redirect) {
        request.getSession().setAttribute("listsp", null);
        return "redirect:/Admin/qlsp/page/1";
    }

    //    phan trang
    @GetMapping("/qlsp/page/{pageNumber}")
    public String showEmployeePage(HttpServletRequest request,
                                   @PathVariable int pageNumber, Model model, HttpSession session) {
        PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("listsp");
        int pagesize = 5;
        List<SanPham> list =(List<SanPham>) sanPhamService.findAll();
        System.out.println(list.size());
        if (pages == null) {
            pages = new PagedListHolder<>(list);
            pages.setPageSize(pagesize);
        } else {
            final int goToPage = pageNumber - 1;
            if (goToPage <= pages.getPageCount() && goToPage >= 0) {
                pages.setPage(goToPage);
            }
        }
        request.getSession().setAttribute("listsp", pages);
        int current = pages.getPage() + 1;
        int begin = Math.max(1, current - list.size());
        int end = Math.min(begin + 5, pages.getPageCount());
        int totalPageCount = pages.getPageCount();
        String baseUrl = "/Admin/qlsp/page/";

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current);
        model.addAttribute("totalPageCount", totalPageCount);
        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("product", pages);
        model.addAttribute("listdanhmuc",danhMucService.findAllDanhMuc());

        return "Admin/admin-sanpham";
    }

    //    insert san pham
    @PostMapping(value = "/InsertSanPham")
    public String insertSanPham(@RequestParam("tensanpham")String tensanpham, @RequestParam("giasanpham")String giasanpham,
                            @RequestParam("giamgia")String giamgia, @RequestParam("motasanpham")String motasanpham,
                            @RequestParam("soluong")String soluong, @RequestParam("id_danhmuc")Long id_danhmuc,
                            @RequestParam("hinhsanpham") MultipartFile[] files , Model model) {
        Calendar calendar = Calendar.getInstance();
        DanhMuc danhMuc = danhMucService.findById(id_danhmuc);
        SanPham sp = new SanPham(tensanpham,Float.parseFloat(giasanpham),Float.parseFloat(giamgia),
            motasanpham,calendar.getTime(),Float.parseFloat(soluong),danhMuc);
        sanPhamService.insertSp(sp);
        Long idSpMax = sanPhamService.maxId();
        try {
            for (MultipartFile file : files) {
                byte[] bytes = file.getBytes();
                String rootPath = "D:\\ChuyenDeWeb_2020\\CDWSecurity\\CDWSecurity\\src\\main\\resources\\static";
                File dir = new File(rootPath + File.separator + "images");
                String name = file.getOriginalFilename();
                if (!dir.exists())
                    dir.mkdirs();
                File serverFile = new File(dir.getAbsolutePath() + File.separator + name);
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                stream.write(bytes);
                stream.close();
                SanPham s = sanPhamService.findById(idSpMax);
                Images images = new Images(name,idSpMax);
                imagesService.insertImages(images);
            }
        }catch (Exception e ){
            e.printStackTrace();
        }
        return "redirect:/Admin/quanlysp";
    }

//    delete san pham
    @GetMapping("/qlsp/{id}/delete")
    public String deleteSp(@PathVariable("id") long id) {
        SanPham emp = sanPhamService.findById(id);
        sanPhamService.delete(emp);

        return "redirect:/Admin/quanlysp";
    }

//    list san pham theo dnah muc
    @RequestMapping("/listSpByDmuc/{id}")
    public String listSpByDmuc(@PathVariable("id")long id,Model model){
        DanhMuc dmuc = danhMucService.findById(id);
        List<SanPham> list = dmuc.getSanPhams();
        model.addAttribute("listspbydanhmuc",list);
        model.addAttribute("listdanhmuc",danhMucService.findAllDanhMuc());
        return "Admin/admin-sanphambydanhmuc";
    }
//    chi tiet san pham
    @RequestMapping("/chitietsanpham/{id}")
    public String chiTietSp(@PathVariable("id") long id , Model model){
        SanPham sanPham = sanPhamService.findById(id);
        model.addAttribute("chitietsp",sanPham);
        return  "Admin/admin-detail-product";
    }

//    end controller san pham

//    Controller User
    @GetMapping("/quanlyuser")
    public String quanlyUser(Model model, HttpServletRequest request
            , RedirectAttributes redirect) {
        request.getSession().setAttribute("listUser", null);
        return "redirect:/Admin/qluser/page/1";
    }

    //        phan trang san pham
    @GetMapping("/qluser/page/{pageNumber}")
    public String showUserPage(HttpServletRequest request,
                                   @PathVariable int pageNumber, Model model, HttpSession session) {
        PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("listUser");
        int pagesize = 5;
        List<User> list = userRepository.findAll();
        System.out.println(list.size());
        if (pages == null) {
            pages = new PagedListHolder<>(list);
            pages.setPageSize(pagesize);
        } else {
            final int goToPage = pageNumber - 1;
            if (goToPage <= pages.getPageCount() && goToPage >= 0) {
                pages.setPage(goToPage);
            }
        }
        request.getSession().setAttribute("listuser", pages);
        int current = pages.getPage() + 1;
        int begin = Math.max(1, current - list.size());
        int end = Math.min(begin + 5, pages.getPageCount());
        int totalPageCount = pages.getPageCount();
        String baseUrl = "/Admin/qluser/page/";

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current);
        model.addAttribute("totalPageCount", totalPageCount);
        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("listuser", pages);
        model.addAttribute("listdanhmuc",danhMucService.findAllDanhMuc());
        return "Admin/admin-user";
    }
    //    delete user
    @GetMapping("/qluser/{id}/delete")
    public String deleteUser(@PathVariable("id") long id) {
        User user= userService.findById(id);
        userService.delete(user);

        return "redirect:/Admin/quanlyuser";
    }
//    end controller user

//    controller hoa don
@GetMapping("/quanlyhoadon")
    public String quanlyHD(Model model, HttpServletRequest request
        , RedirectAttributes redirect) {
        request.getSession().setAttribute("listHD", null);
        return "redirect:/Admin/qlhd/page/1";
}

    //        phan trang user
    @GetMapping("/qlhd/page/{pageNumber}")
    public String showHDPage(HttpServletRequest request,
                               @PathVariable int pageNumber, Model model, HttpSession session) {
        PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("listHD");
        int pagesize = 5;
        List<User> list = userRepository.findAll();
        System.out.println(list.size());
        if (pages == null) {
            pages = new PagedListHolder<>(list);
            pages.setPageSize(pagesize);
        } else {
            final int goToPage = pageNumber - 1;
            if (goToPage <= pages.getPageCount() && goToPage >= 0) {
                pages.setPage(goToPage);
            }
        }
        request.getSession().setAttribute("listHD", pages);
        int current = pages.getPage() + 1;
        int begin = Math.max(1, current - list.size());
        int end = Math.min(begin + 5, pages.getPageCount());
        int totalPageCount = pages.getPageCount();
        String baseUrl = "/Admin/qlhd/page/";

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current);
        model.addAttribute("totalPageCount", totalPageCount);
        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("listHD", pages);
        model.addAttribute("listdanhmuc",danhMucService.findAllDanhMuc());
        return "Admin/admin-hoadon";
    }
}
