package yiyi.example.dubbo.dynamicdatasource.context;

import java.util.ArrayList;
import java.util.List;

import yiyi.example.dubbo.dynamicdatasource.constants.DataSourceConstants;

/**
 * 动态数据源名称上下文处理
 *
 * @author bwang018 2020/12/19
 **/
public class DynamicDataSourceContextHolder {

    /**
     * 动态数据源名称上下文
     */
    private static final ThreadLocal<String> DATASOURCE_CONTEXT_KEY_HOLDER = new ThreadLocal<>();
    
    /**
     * 也就是所谓的数据库别名
     * 管理所有的数据源id;
     * 主要是为了判断数据源是否存在;
     */
    public static List<String> dataSourceIds = new ArrayList<String>();

    /**
     * 设置数据源
     * @param key
     */
    public static void setContextKey(String key){
        System.out.println("切换数据源"+key);
        DATASOURCE_CONTEXT_KEY_HOLDER.set(key);
    }

    /**
     * 获取数据源名称
     * @return
     */
    public static String getContextKey(){
        String key = DATASOURCE_CONTEXT_KEY_HOLDER.get();
        return key == null?DataSourceConstants.DS_KEY_MASTER:key;
    }

    /**
     * 删除当前数据源名称
     */
    public static void removeContextKey(){
        DATASOURCE_CONTEXT_KEY_HOLDER.remove();
    }
    
    public static boolean containsDataSource(String dataSourceId){
        return dataSourceIds.contains(dataSourceId);
    }
}
