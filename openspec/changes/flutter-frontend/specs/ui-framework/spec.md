## ADDED Requirements

### Requirement: Material 3 主题系统
系统 SHALL 使用 Material 3 主题（`useMaterial3: true`），通过 Seed Color 方式生成完整的 ColorScheme。

#### Scenario: 主题生成
- **WHEN** 应用初始化主题
- **THEN** MUST 使用 `ColorScheme.fromSeed()` 生成亮色和暗色两套 ColorScheme

#### Scenario: Material 3 组件样式
- **WHEN** 使用 Material 组件（Button、Card、AppBar 等）
- **THEN** MUST 遵循 Material 3 设计规范，圆角、阴影、颜色均由 ColorScheme 驱动

### Requirement: 亮/暗模式切换
系统 SHALL 支持亮色、暗色、跟随系统三种主题模式，用户切换后立即生效并持久化。

#### Scenario: 切换到暗色模式
- **WHEN** 用户选择暗色模式
- **THEN** 应用 MUST 立即切换到暗色主题，并将偏好保存到 Hive

#### Scenario: 跟随系统模式
- **WHEN** 用户选择"跟随系统"模式
- **THEN** 应用主题 MUST 与系统当前主题保持一致

#### Scenario: 应用重启恢复主题
- **WHEN** 应用重启
- **THEN** MUST 从 Hive 读取用户上次选择的主题模式并应用

### Requirement: 主题 Provider
系统 SHALL 通过 Riverpod Provider 管理主题状态，提供 `themeModeProvider` 供全局读取和修改。

#### Scenario: 读取当前主题
- **WHEN** 组件需要获取当前主题模式
- **THEN** MUST 通过 `ref.watch(themeModeProvider)` 获取

#### Scenario: 修改主题
- **WHEN** 用户切换主题
- **THEN** MUST 通过 `ref.read(themeModeProvider.notifier).setThemeMode(mode)` 修改

### Requirement: 通用 Loading 组件
系统 SHALL 提供通用 Loading 组件，支持全屏遮罩和内嵌两种模式。

#### Scenario: 全屏 Loading
- **WHEN** 页面数据加载中
- **THEN** MUST 显示全屏半透明遮罩 + 居中 CircularProgressIndicator

#### Scenario: 内嵌 Loading
- **WHEN** 局部区域数据加载中
- **THEN** MUST 在该区域显示小型 CircularProgressIndicator

### Requirement: 通用 Error 组件
系统 SHALL 提供通用 Error 组件，显示错误信息和重试按钮。

#### Scenario: 错误展示
- **WHEN** 异步操作失败
- **THEN** MUST 显示错误图标 + 错误信息 + "重试"按钮

#### Scenario: 重试操作
- **WHEN** 用户点击"重试"按钮
- **THEN** MUST 重新触发失败的异步操作

### Requirement: 通用 Empty 组件
系统 SHALL 提供通用 Empty 组件，在列表数据为空时显示。

#### Scenario: 空数据展示
- **WHEN** 列表数据为空
- **THEN** MUST 显示空状态图标 + 提示文字（如"暂无数据"）

### Requirement: 响应式布局适配
系统 SHALL 适配不同屏幕尺寸，使用 `MediaQuery` 和 `LayoutBuilder` 实现响应式布局。

#### Scenario: 手机竖屏布局
- **WHEN** 屏幕宽度 < 600dp
- **THEN** MUST 使用单列布局

#### Scenario: 平板横屏布局
- **WHEN** 屏幕宽度 >= 600dp
- **THEN** MUST 使用多列布局或侧边导航
