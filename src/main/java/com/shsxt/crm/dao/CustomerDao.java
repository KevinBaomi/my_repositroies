package com.shsxt.crm.dao;

import com.shsxt.crm.vo.CustomerVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by 23125 on 2017/8/13.
 */
public interface CustomerDao {
    @Select("SELECT id, `name` FROM t_customer where is_valid = 1")
    List<CustomerVO> findAll();
}
