package com.dumas.pedestal.common.model.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.Objects;

/**
 * 分页参数：默认从1开始
 *
 * @author dumas
 * @date 2021/12/06 10:46 AM
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageParam {
    @ApiModelProperty(value = "分页参数：当前页[pageNo必须为正整数]", required = true)
    @PositiveOrZero(message = "分页参数错误：pageNo必须为正整数")
    Integer pageNo;

    @ApiModelProperty(value = "分页参数：一页大小[最多每页 100 条数据]", required = true)
    @Max(value = 100, message = "每页参数错误：最多每页 100 条数据")
    Integer pageSize;

    public Integer getPageNo() {
        if (Objects.isNull(pageNo)) {
            pageNo = 1;
        }
        return pageNo;
    }
}
