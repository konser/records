package io.github.notoday.plus.web.rest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.notoday.plus.domain.User;
import io.github.notoday.plus.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author no-today
 * @date 2021/04/01 下午12:30
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<User> save(@RequestBody User user) {
        userService.save(user);
        return ResponseEntity.ok(userService.getById(user.getId()));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> remove(@PathVariable("id") String id) {
        userService.removeById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users")
    public ResponseEntity<User> update(@RequestBody User user) {
        userService.updateById(user);
        return ResponseEntity.ok(userService.getById(user.getId()));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> find(@PathVariable("id") String id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping("/users/page")
    public ResponseEntity<IPage<User>> page(@RequestParam Integer page, @RequestParam Integer size) {
        return ResponseEntity.ok(userService.page(new Page<>(page, size)));
    }

    @GetMapping("/users/findByUsername")
    public ResponseEntity<User> findByUsername(@RequestParam String username) {
        return ResponseEntity.ok(userService.findByUsername(username).orElse(new User()));
    }
}
