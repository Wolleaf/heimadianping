package com.hmdp.mapper;

import com.hmdp.entity.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface BlogMapper extends BaseMapper<Blog> {

    /**
     * 更新点赞数
     *
     * @param id
     * @param i
     * @return
     */
    @Update("update tb_blog set liked = liked + #{i} where id = #{id}")
    int incrLikedById(@Param("id") Long id, @Param("i") int i);

    /**
     * 根据id批量查询，按照id顺序返回
     * @param ids
     * @return
     */
    List<Blog> selectWithOrderByField(List<Long> ids);
}
