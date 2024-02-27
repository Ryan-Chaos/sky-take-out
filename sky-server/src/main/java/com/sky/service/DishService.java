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
     *根据id查询菜品
     * @param id
     * @return
     */
    DishVO searchById(Long id);


    /**
     * 修改菜品
     * @param dishDTO
     */
    void edit(DishDTO dishDTO);


    /**
     * 修改status状态
     * @param status
     * @param id
     */
    void editStatusById(Integer status, Long id);
}
