package io.github.notoday.plus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.notoday.plus.domain.User;

import java.util.Optional;

/**
 * @author no-today
 * @date 2021/03/31 下午8:57
 */
public interface UserService extends IService<User> {

    Optional<User> findByUsername(String username);
}
