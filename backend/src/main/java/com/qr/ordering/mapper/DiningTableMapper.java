package com.qr.ordering.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qr.ordering.entity.DiningTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 餐桌数据访问层
 */
@Mapper
public interface DiningTableMapper extends BaseMapper<DiningTable> {
    
    /**
     * 根据餐桌号查询餐桌
     */
    @Select("SELECT * FROM dining_table WHERE table_number = #{tableNumber}")
    DiningTable selectByTableNumber(String tableNumber);
}
