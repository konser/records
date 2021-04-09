package io.github.notoday.plus.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

/**
 * @author no-today
 * @date 2021/03/31 下午6:24
 */
@Getter
@Setter
@Accessors(chain = true)
public class BaseEntity implements Serializable {

    protected Instant createdDate;

    protected Instant lastModifiedDate;

    @TableField(typeHandler = JacksonTypeHandler.class)
    protected Map<String, Object> features;
}
