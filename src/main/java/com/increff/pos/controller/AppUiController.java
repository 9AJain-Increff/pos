package com.increff.pos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import static com.increff.pos.util.SecurityUtil.isAuthenticated;


@Controller
public class AppUiController extends AbstractUiController {

    @RequestMapping(value = "/ui/home")
    public ModelAndView home() {
        String page = (isAuthenticated())?"home.html": "redirect:/site/login";
        return mav(page);
    }

    @RequestMapping(value = "/ui/employee")
    public ModelAndView employee() {
        return mav("employee.html");
    }

    @RequestMapping(value = "/ui/admin")
    public ModelAndView admin() {
        return mav("user.html");
    }

    @RequestMapping(value = "/ui/brand")
    public ModelAndView brand() {
        return mav("brand.html");
    }

    @RequestMapping(value = "/ui/product")
    public ModelAndView product() {
        return mav("product.html");
    }

    @RequestMapping(value = "/ui/inventory")
    public ModelAndView inventory() {
        return mav("inventory.html");
    }


    @RequestMapping(value = "/ui/order")
    public ModelAndView order() {
        return mav("order.html");
    }

    @RequestMapping(value = "/ui/report")
    public ModelAndView report() {
        return mav("report.html");
    }

    @RequestMapping(value = "/ui/report/sales")
    public ModelAndView sales() {
        return mav("sales.html");
    }

    @RequestMapping(value = "/ui/report/inventory-report")
    public ModelAndView inventoryReport() {
        return mav("inventoryReport.html");
    }

    @RequestMapping(value = "/ui/report/brand")
    public ModelAndView brandReport() {
        return mav("brandReport.html");
    }

    @RequestMapping(value = "/ui/report/daily")
    public ModelAndView dailyReport() {
        return mav("dailyReport.html");
    }

}
