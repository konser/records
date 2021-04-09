package io.github.notoday.plus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.notoday.plus.domain.Article;
import io.github.notoday.plus.repository.ArticleRepository;
import io.github.notoday.plus.service.ArticleService;
import org.springframework.stereotype.Service;

/**
 * @author no-today
 * @date 2021/04/01 下午12:48
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleRepository, Article> implements ArticleService {
}
