package com.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.admin.common.exception.BusinessException;
import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.entity.GenTable;
import com.admin.entity.GenTableColumn;
import com.admin.mapper.GenTableColumnMapper;
import com.admin.mapper.GenTableMapper;
import com.admin.service.GenTableService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class GenTableServiceImpl implements GenTableService {

    private final GenTableMapper genTableMapper;
    private final GenTableColumnMapper genTableColumnMapper;

    public GenTableServiceImpl(GenTableMapper genTableMapper, GenTableColumnMapper genTableColumnMapper) {
        this.genTableMapper = genTableMapper;
        this.genTableColumnMapper = genTableColumnMapper;
    }

    @Override
    public PageResult<GenTable> selectGenTableList(PageParam pageParam, String tableName, String tableComment) {
        LambdaQueryWrapper<GenTable> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(tableName), GenTable::getTableName, tableName)
               .like(StrUtil.isNotBlank(tableComment), GenTable::getTableComment, tableComment)
               .orderByDesc(GenTable::getCreateTime);

        Page<GenTable> page = genTableMapper.selectPage(new Page<>(pageParam.getPageNum(), pageParam.getPageSize()), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public GenTable selectGenTableById(Long id) {
        GenTable table = genTableMapper.selectById(id);
        if (table != null) {
            table.setColumns(genTableColumnMapper.selectByTableId(id));
        }
        return table;
    }

    @Override
    @Transactional
    public void importTable(List<String> tableNames) {
        for (String tableName : tableNames) {
            GenTable genTable = new GenTable();
            genTable.setTableName(tableName);
            genTable.setTableComment(tableName);
            genTable.setClassName(convertClassName(tableName));
            genTable.setPackageName("com.admin");
            genTable.setModuleName("system");
            genTable.setBusinessName(convertBusinessName(tableName));
            genTable.setFunctionName(tableName);
            genTable.setGenType("0");
            genTable.setGenPath("/");
            genTable.setTplCategory("crud");
            genTable.setCreateBy("system");
            genTable.setCreateTime(LocalDateTime.now());
            genTableMapper.insert(genTable);
        }
    }

    @Override
    @Transactional
    public void updateGenTable(GenTable genTable) {
        genTable.setUpdateBy("system");
        genTable.setUpdateTime(LocalDateTime.now());
        genTableMapper.updateById(genTable);

        if (genTable.getColumns() != null) {
            for (GenTableColumn column : genTable.getColumns()) {
                genTableColumnMapper.updateById(column);
            }
        }
    }

    @Override
    @Transactional
    public void deleteGenTable(Long id) {
        genTableMapper.deleteById(id);
        genTableColumnMapper.delete(new LambdaQueryWrapper<GenTableColumn>().eq(GenTableColumn::getTableId, id));
    }

    @Override
    public Map<String, String> previewCode(Long id) {
        GenTable table = selectGenTableById(id);
        if (table == null) {
            throw new BusinessException("表不存在");
        }

        Map<String, String> dataMap = new LinkedHashMap<>();
        VelocityContext context = buildContext(table);

        String[] templates = {"Entity.java", "Mapper.java", "Service.java", "ServiceImpl.java", "Controller.java", "Mapper.xml"};
        for (String tpl : templates) {
            StringWriter writer = new StringWriter();
            Template template = Velocity.getTemplate("vm/" + tpl + ".vm", "UTF-8");
            template.merge(context, writer);
            dataMap.put(tpl, writer.toString());
        }
        return dataMap;
    }

    @Override
    public byte[] generateCode(Long id) {
        GenTable table = selectGenTableById(id);
        if (table == null) {
            throw new BusinessException("表不存在");
        }
        return generateCodeBytes(table);
    }

    @Override
    public byte[] batchGenerateCode(Long[] tableIds) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(outputStream)) {
            for (Long id : tableIds) {
                GenTable table = selectGenTableById(id);
                if (table != null) {
                    byte[] codeBytes = generateCodeBytes(table);
                    zip.write(codeBytes);
                }
            }
        } catch (Exception e) {
            throw new BusinessException("生成代码失败: " + e.getMessage());
        }
        return outputStream.toByteArray();
    }

    private byte[] generateCodeBytes(GenTable table) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(outputStream)) {
            VelocityContext context = buildContext(table);
            String[] templates = {"Entity.java", "Mapper.java", "Service.java", "ServiceImpl.java", "Controller.java", "Mapper.xml"};
            String packageName = table.getPackageName().replace(".", "/");

            for (String tpl : templates) {
                StringWriter writer = new StringWriter();
                Template template = Velocity.getTemplate("vm/" + tpl + ".vm", "UTF-8");
                template.merge(context, writer);

                String path;
                if (tpl.endsWith(".xml")) {
                    path = "resources/mapper/" + table.getBusinessName() + tpl.replace("Mapper.xml", "Mapper.xml");
                } else {
                    path = packageName + "/" + tpl.replace(".java", "/" + table.getClassName() + tpl.replace("Entity.java", ".java").replace("Mapper.java", "Mapper.java").replace("Service.java", "Service.java").replace("ServiceImpl.java", "ServiceImpl.java").replace("Controller.java", "Controller.java"));
                }

                zip.putNextEntry(new ZipEntry(path));
                zip.write(writer.toString().getBytes());
                zip.closeEntry();
            }
        } catch (Exception e) {
            throw new BusinessException("生成代码失败: " + e.getMessage());
        }
        return outputStream.toByteArray();
    }

    private VelocityContext buildContext(GenTable table) {
        VelocityContext context = new VelocityContext();
        context.put("tableName", table.getTableName());
        context.put("tableComment", table.getTableComment());
        context.put("className", table.getClassName());
        context.put("classname", StrUtil.lowerFirst(table.getClassName()));
        context.put("packageName", table.getPackageName());
        context.put("moduleName", table.getModuleName());
        context.put("businessName", table.getBusinessName());
        context.put("functionName", table.getFunctionName());
        context.put("columns", table.getColumns());
        context.put("importList", getImportList(table.getColumns()));
        context.put("date", LocalDateTime.now().toString());
        return context;
    }

    private Set<String> getImportList(List<GenTableColumn> columns) {
        Set<String> imports = new HashSet<>();
        for (GenTableColumn column : columns) {
            if ("BigDecimal".equals(column.getJavaType())) {
                imports.add("java.math.BigDecimal");
            } else if ("LocalDateTime".equals(column.getJavaType())) {
                imports.add("java.time.LocalDateTime");
            }
        }
        return imports;
    }

    private String convertClassName(String tableName) {
        String[] parts = tableName.replace("sys_", "").replace("gen_", "").split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
        }
        return sb.toString();
    }

    private String convertBusinessName(String tableName) {
        String[] parts = tableName.replace("sys_", "").replace("gen_", "").split("_");
        return parts[parts.length - 1];
    }
}
