package io.github.notoday.plus.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.notoday.plus.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * @author no-today
 * @date 2021/03/31 下午8:54
 */
@Mapper
public interface UserRepository extends BaseMapper<User> {

    Optional<User> findByUsername(@Param("username") String username);
}
