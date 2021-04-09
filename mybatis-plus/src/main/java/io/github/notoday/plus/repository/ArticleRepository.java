package io.github.notoday.plus.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.notoday.plus.domain.Article;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author no-today
 * @date 2021/03/31 下午8:56
 */
@Mapper
public interface ArticleRepository extends BaseMapper<Article> {
}
