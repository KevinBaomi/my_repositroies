package com.shsxt.crm.service;

import com.shsxt.crm.controller.CustomerController;
import com.shsxt.crm.dao.CustomerDao;
import com.shsxt.crm.vo.CustomerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 23125 on 2017/8/13.
 */
@Service
public class CustomerService {

        @Autowired
    private CustomerDao customerDao;

        public List<CustomerVO>findAll(){
            List<CustomerVO> customers = customerDao.findAll();
            return customers;
        }



}
