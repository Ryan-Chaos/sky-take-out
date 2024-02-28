package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
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


    /**
     * 根据套餐id删除套餐菜品对应信息
     * @param setmeal_id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmeal_id}")
    void delete(Long setmeal_id);


    /**
     * 根据套餐id获得套餐内菜品id
     * @param setmeal_id
     * @return
     */
    @Select("select dish_id from setmeal_dish where setmeal_id = #{setmeal_id}")
    List<Long> getDishIdsBySetmealId(Long setmeal_id);
}
