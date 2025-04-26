package com.zw.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PermissionTreeVO {
    private Long id;
    private String code;
    private String name;
    private Long parentId;
    private String type;
    private String path;
    private String icon;
    private Integer sort;
    private Integer status;
    private List<PermissionTreeVO> children = new ArrayList<>();
} 