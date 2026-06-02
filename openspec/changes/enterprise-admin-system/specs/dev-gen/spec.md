## ADDED Requirements

### Requirement: 代码生成-表导入
系统 SHALL 支持从数据库导入表结构。

#### Scenario: 查询可导入表
- **WHEN** 管理员访问代码生成页面
- **THEN** 系统展示数据库中尚未导入的表列表

#### Scenario: 导入表
- **WHEN** 管理员选择一张或多张表并点击导入
- **THEN** 系统读取表结构信息，生成 gen_table 和 gen_table_column 记录

### Requirement: 代码生成-配置
系统 SHALL 支持配置生成参数。

#### Scenario: 编辑生成配置
- **WHEN** 管理员编辑表的生成配置
- **THEN** 可修改类名、包名、模块名、生成功能（增删改查）、前端组件类型

#### Scenario: 编辑列配置
- **WHEN** 管理员编辑列的生成配置
- **THEN** 可修改 Java 类型、Java 字段名、是否查询、查询方式、显示类型、字典类型

### Requirement: 代码生成-预览与下载
系统 SHALL 支持代码预览和批量下载。

#### Scenario: 预览代码
- **WHEN** 管理员点击预览按钮
- **THEN** 系统展示生成的各文件代码内容

#### Scenario: 批量生成下载
- **WHEN** 管理员选择多张表并点击生成
- **THEN** 系统生成代码并打包为 ZIP 下载
