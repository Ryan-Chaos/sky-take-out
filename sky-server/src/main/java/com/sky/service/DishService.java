package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品
     * @return
     */
    void saveWithFlavors(DishDTO dishDTO);


    /**
     * 分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     * @return
     */
    void deleteBatch(List<Long> ids);


    /**
     *
     * @param id
     * @return
     */
    DishVO searchById(Long id);
}
