package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Api(tags = "套餐相关接口")
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result add(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐：{}",setmealDTO);

        setmealService.insert(setmealDTO);

        return Result.success();
    }


    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> search(@PathVariable Long id){
        log.info("根据id查询套餐:{}",id);

        SetmealVO setmealVO = setmealService.getById(id);

        return Result.success(setmealVO);
    }


    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询");

        PageResult pageResult = setmealService.page(setmealPageQueryDTO);

        return Result.success(pageResult);
    }


    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    public Result deleteBatch(@RequestParam List<Long> ids){
        log.info("批量删除套餐:{}",ids);

        setmealService.deleteBatch(ids);

        return Result.success();
    }
}
