package io.github.notoday.plus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.notoday.plus.domain.User;
import io.github.notoday.plus.repository.UserRepository;
import io.github.notoday.plus.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author no-today
 * @date 2021/04/01 下午12:45
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserRepository, User> implements UserService {

    @Override
    public Optional<User> findByUsername(String username) {
        return baseMapper.findByUsername(username);
    }
}
