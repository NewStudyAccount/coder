## ADDED Requirements

### Requirement: Feature-first 目录结构
项目 SHALL 采用 Feature-first 目录组织方式，每个功能模块包含 data/domain/presentation 三层子目录。核心基础设施放置在 `core/` 目录，共享 UI 组件放置在 `shared/` 目录。

#### Scenario: 功能模块目录结构验证
- **WHEN** 创建新功能模块
- **THEN** 模块目录下 MUST 包含 `data/`、`domain/`、`presentation/` 三个子目录

#### Scenario: 核心基础设施目录验证
- **WHEN** 查看项目 `lib/core/` 目录
- **THEN** MUST 包含 `network/`、`router/`、`storage/`、`theme/`、`constants/`、`extensions/`、`utils/` 子目录

### Requirement: Clean Architecture 三层分离
每个功能模块 SHALL 严格遵循 Clean Architecture 三层分离原则：Domain 层不依赖 Data 层和 Presentation 层，Data 层依赖 Domain 层，Presentation 层依赖 Domain 层。

#### Scenario: Domain 层无外部依赖
- **WHEN** 检查 `domain/` 目录下的代码
- **THEN** 不得 import `data/` 或 `presentation/` 目录下的任何文件

#### Scenario: Repository 接口定义在 Domain 层
- **WHEN** 定义数据仓库抽象
- **THEN** Repository 接口 MUST 定义在 `domain/repositories/` 目录下，实现在 `data/repositories/` 目录下

### Requirement: 项目入口配置
项目入口 `main.dart` SHALL 初始化所有基础设施（Hive 存储、Dio 客户端、GoRouter），并使用 ProviderScope 包裹根组件。

#### Scenario: 应用启动初始化
- **WHEN** 应用启动
- **THEN** MUST 按顺序完成 Hive 初始化、Dio 客户端配置、GoRouter 初始化，最后渲染 UI

### Requirement: pubspec.yaml 依赖管理
项目 SHALL 声明以下核心依赖：go_router、flutter_riverpod、riverpod_annotation、dio、hive、hive_flutter、freezed、freezed_annotation、json_annotation、flutter_hooks、hooks_riverpod。开发依赖 SHALL 包含：build_runner、freezed、json_serializable、riverpod_generator、hive_generator。

#### Scenario: 核心依赖完整性
- **WHEN** 执行 `flutter pub get`
- **THEN** 所有核心依赖和开发依赖 MUST 成功解析且无冲突
