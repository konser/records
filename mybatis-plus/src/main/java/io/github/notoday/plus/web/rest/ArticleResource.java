package io.github.notoday.plus.web.rest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.notoday.plus.domain.Article;
import io.github.notoday.plus.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author no-today
 * @date 2021/04/01 下午12:30
 */
@RestController
@RequestMapping("/api")
public class ArticleResource {

    private final ArticleService articleService;

    public ArticleResource(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping("/articles")
    public ResponseEntity<Boolean> save(@RequestBody Article article) {
        return ResponseEntity.ok(articleService.save(article));
    }

    @DeleteMapping("/articles/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable("id") String id) {
        return ResponseEntity.ok(articleService.removeById(id));
    }

    @PutMapping("/articles")
    public ResponseEntity<Boolean> update(@RequestBody Article article) {
        return ResponseEntity.ok(articleService.updateById(article));
    }

    @GetMapping("/articles/{id}")
    public ResponseEntity<Article> find(@PathVariable("id") String id) {
        return ResponseEntity.ok(articleService.getById(id));
    }

    @GetMapping("/articles/page/{page}/{size}")
    public ResponseEntity<IPage<Article>> page(@PathVariable("page") Integer page, @PathVariable("size") Integer size) {
        return ResponseEntity.ok(articleService.page(new Page<>(page, size)));
    }
}
