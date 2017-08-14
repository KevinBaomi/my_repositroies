package com.shsxt.crm.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shsxt.crm.dto.SaleChanceDto;
import com.shsxt.crm.util.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.miemiedev.mybatis.paginator.domain.Order;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.github.miemiedev.mybatis.paginator.domain.Paginator;
import com.shsxt.crm.dao.SaleChanceDao;
import com.shsxt.crm.dto.SaleChanceQuery;
import com.shsxt.crm.model.SaleChance;

@Service
public class SaleChanceService {

    @Autowired
    private SaleChanceDao saleChanceDao;
    private static Logger logger = LoggerFactory.getLogger(SaleChanceService.class);


    public Map<String, Object> selectForPage(SaleChanceQuery query) {

        // 构建一个分页对象
        Integer page = query.getPage();
        if (page == null) {
            page = 1;
        }
        Integer pageSize = query.getRows();
        if (pageSize == null) {
            pageSize = 10;
        }
        String sort = query.getSort();
        if (StringUtils.isBlank(sort)) {
            sort = "id.desc"; // 数据库字段.desc/asc
        }
        PageBounds pageBounds = new PageBounds(page, pageSize, Order.formString(sort));

        // 查询
        List<SaleChance> saleChances = saleChanceDao.selectForPage(query, pageBounds);
        PageList<SaleChance> result = (PageList<SaleChance>) saleChances;

        // 返回分页结果
        Paginator paginator = result.getPaginator();
        Map<String, Object> map = new HashMap<>();
        map.put("paginator", paginator);
        map.put("rows", result);
        map.put("total", paginator.getTotalCount());
        return map;
    }


    /**
     * 添加
     */
    public void add(SaleChanceDto saleChanceDto, String loginUserName) {
        //参数验证
        checkParams(saleChanceDto.getCustomerId(), saleChanceDto.getCustomerName(), saleChanceDto.getCgjl());
        //判断分配状态  根据是否有指定人判断
        String assignMan = saleChanceDto.getAssignMan();
        int state = 0; //未分配状态
        Date assignTime = null;// 分配时间
        if (StringUtils.isNoneBlank(assignMan)) {
            state = 1;//已经分配状态
            assignTime = new Date();
        }
        SaleChance saleChance = new SaleChance();
        BeanUtils.copyProperties(saleChanceDto, saleChance);
        saleChance.setAssignTime(assignTime);
        saleChance.setState(state);
        saleChance.setCreateMan(loginUserName);
        int count = saleChanceDao.insert(saleChance);
        logger.debug("插入的记录数为：{}, 主键为：", count, saleChance.getId());
    }

    /**
     * 更新
     *
     * @param saleChane
     */
    public void update(SaleChance saleChane) {
        // 基本参数验证
        int id = saleChane.getId();
        AssertUtil.intIsNotEmpty(id, "请选择记录进行更新");
        checkParams(saleChane.getCustomerId(), saleChane.getCustomerName(), saleChane.getCgjl());
        checkState(saleChane);
        saleChanceDao.update(saleChane);

    }

    /**
     * 验证分配状态
     *
     * @param saleChane
     */
    private void checkState(SaleChance saleChane) {
        SaleChance saleChanceFormDB = saleChanceDao.findById(saleChane.getId());
        AssertUtil.notNull(saleChanceFormDB, "记录不存在，请重新选择");
        int state = saleChanceFormDB.getState();
        Date assignTime = null;
        String assignMan = saleChanceFormDB.getAssignMan();
        if (state == 0) {
            if (StringUtils.isNoneBlank(saleChane.getAssignMan())) {
                state = 1;
                assignTime = new Date();
            }
        } else {
            if (!saleChanceFormDB.getAssignMan().equals(saleChane.getAssignMan())) {
                if (StringUtils.isNoneBlank(saleChane.getAssignMan())) {
                    state = 0;
                    assignTime = null;
                    assignMan = saleChane.getAssignMan();
                    assignTime = new Date();
                }
            }
        }
        saleChane.setAssignMan(assignMan);
        saleChane.setAssignTime(assignTime);
        saleChane.setState(state);

    }

    private void checkParams(Integer customerId, String customerName, Integer cgjl) {
        AssertUtil.intIsNotEmpty(customerId, "选择用户");
        AssertUtil.isNotEmpty(customerName, "请选择用户");
        AssertUtil.intIsNotEmpty(cgjl, "请输入成功率");

    }

    public void delete(String ids) {
        // 参数验证
        AssertUtil.isNotEmpty(ids, "请选择记录进行删除");
        // 执行sql
        saleChanceDao.delete(ids);
    }


}
