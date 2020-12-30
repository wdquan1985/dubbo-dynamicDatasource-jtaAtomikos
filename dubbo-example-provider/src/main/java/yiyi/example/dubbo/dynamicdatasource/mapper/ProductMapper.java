package yiyi.example.dubbo.dynamicdatasource.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import yiyi.example.dubbo.common.entity.Product;

import java.util.List;

/**
 * 用户 Mapper
 *
 * @author bwang018 2020-12-19
 * @date 
 */
@Repository
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 自定义查询
     * @param wrapper 条件构造器
     * @return
     */
    List<Product> selectAll(@Param(Constants.WRAPPER) Wrapper<Product> wrapper);

}
