package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询对应的套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);


    /**
     * 批量新增套餐菜品关系
     * @param setmealDishes
     */

    void insertBatch(List<SetmealDish> setmealDishes);


    /**
     * 根据套餐id获得套餐菜品关系
     * @param setmeal_id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmeal_id}")
    List<SetmealDish> getBySetmealId(Long setmeal_id);
}
