package yiyi.example.dubbo.common.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;
import java.math.BigDecimal;
import java.util.Date;

/**
* 用户对象 test_user
*
* @author bwang018 2020-12-19
*/
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@TableName("test_product")
public class Product extends Model<Product> {

    private static final long serialVersionUID = 1L;

    /** id */
    /*
     * 非常重要：使用mybatis-plus的时候
     * 对于主键自增，mybatis-plus里最直接的有两种方法，一种是INPUT ,一种是AUTO.
     * 如果设置的是在数据库自增，就需要在pojo类中标注。@TableId(type=IdType.AUTO)，或者 在配置文件中的sessionFactory将INPUT改成AUTO
     * 如果不做更改会出现主键数据很大的情况。例如：org.apache.ibatis.reflection.ReflectionException: Could not set property 'id' of 'class com.pojo.sallerPojo.TbBrand' with value '1077177904745537538' Cause: java.lang.IllegalArgumentException: argument type mismatch
     */
    /**
     * 雪花算法：snowflake是Twitter开源的分布式ID生成算法，结果是一个long型的ID
     * 如果不指定主键生成策略，例如 Redis、数据库自增，那么默认使用雪花算法+UUID(不含中划线)，生成一个很大的值。
     */
	/**
	 * IdType:
	 * AUTO : 数据库自增
	 * INPUT: 用户自行输入
	 * ID_WORKER: 分布式全局唯一ID， 长整型
	 * UUID: 32位UUID字符串
	 * NONE: 无状态
	 * ID_WORKER_STR: 分布式全局唯一ID 字符串类型
	 * 
	 * NOTE： 当IdType的类型为ID_WORKER、ID_WORKER_STR或者UUID时，主键由MyBatis Plus的IdWorker类生成，
	 * IdWorker中调用了分布式唯一 ID 生成器 - Sequence
	 */
    @TableId(type=IdType.AUTO)
//    @TableId(type=IdType.ID_WORKER)
    private Long id;
    /** 姓名 */
    @Version // 乐观锁的Version注解
    private String name;
    /** 重量 */
    private String weight;
    /** 数量*/
    private String size;
    /** 高度*/
    private String height;
    /** 厂商 */
    private String vendor;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    public Product() {
    	
    }

}
