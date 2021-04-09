package io.github.notoday.plus.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author no-today
 * @date 2021/03/31 下午6:17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("article")
public class Article extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String content;

    private String author;
}
