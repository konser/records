package io.github.notoday.records.atomic.redis.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author no-today
 * @date 2021/02/07 下午5:23
 */
@Data
@Accessors(chain = true)
public class UserWalletDTO {

    private Long goldCoin;
    private Double goldBean;
}
