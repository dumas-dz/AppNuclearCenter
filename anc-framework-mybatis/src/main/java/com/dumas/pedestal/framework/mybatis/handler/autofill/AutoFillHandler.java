package com.dumas.pedestal.framework.mybatis.handler.autofill;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Mybatis-plus 自动填充
 *   注意字段匹配
 *
 * @author az.ye
 * @version V1.0
 * @since 2021-06-21 21:57
 */
@Slf4j
public class AutoFillHandler implements MetaObjectHandler {
    @Resource
    private IFillUsernameHandler iFillUsernameHandler;

    @Override
    public void insertFill(MetaObject metaObject) {
        String now = DateUtil.now();
        this.strictInsertFill(metaObject, "delFlag", () -> 0, Integer.class);
        this.strictInsertFill(metaObject, "deleteFlag", () -> 0, Integer.class);
        this.strictInsertFill(metaObject, "gmtCreate", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "gmtCreate", () -> now, String.class);
        this.strictInsertFill(metaObject, "createBy", () -> iFillUsernameHandler.getUsername(), String.class);
        this.strictInsertFill(metaObject, "gmtUpdate", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "gmtUpdate", () -> now, String.class);
        this.strictInsertFill(metaObject, "updateBy",() -> iFillUsernameHandler.getUsername(), String.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String now = DateUtil.now();
        TableInfo tableInfo = TableInfoHelper.getTableInfo(metaObject.getOriginalObject().getClass());

        updateIncludeNull(tableInfo, "gmtUpdate", LocalDateTime.now(), metaObject);
        updateIncludeNull(tableInfo, "gmtUpdate", now, metaObject);
        updateIncludeNull(tableInfo, "updateBy", iFillUsernameHandler.getUsername(), metaObject);

//        this.strictUpdateFill(metaObject, "gmtUpdate", LocalDateTime.class, LocalDateTime.now());
//        this.strictUpdateFill(metaObject, "gmtUpdate", () -> now, String.class);
//        this.strictUpdateFill(metaObject, "updateBy",() -> iFillUsernameHandler.getUsername(), String.class);

    }

    private void updateIncludeNull(TableInfo tableInfo, String fieldName, Object fieldValue, MetaObject metaObject) {
        if (Objects.isNull(fieldValue)) {
            setFieldValByName(fieldName, null, metaObject);
            return;
        }
        tableInfo.getFieldList().stream()
                .filter(j -> j.getProperty().equals(fieldName) && fieldValue.getClass().equals(j.getPropertyType()))
                .findFirst()
                .ifPresent(j -> setFieldValByName(fieldName, fieldValue, metaObject));
    }
}
