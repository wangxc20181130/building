package com.sancaijia.building.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 字典表
 * </p>
 *
 * @author Cc
 * @since 2020-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_dict")
public class Dict{

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 类型
     */
    private String type;
    @TableField("'key'")
    private String key;

    private String value;

    private String name;

    /**
     * 备注
     */
    private String remark;

}
