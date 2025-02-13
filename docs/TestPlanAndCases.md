# LexiVault 测试计划与用例

## 1. 测试目标

1. 验证词库管理功能的正确性
2. 验证提醒设置功能的正确性
3. 验证单词学习功能的正确性
4. 验证 AI 助手功能的正确性
5. 验证数据持久化功能的正确性

## 2. 测试范围与用例

### 2.1 单元测试

#### 2.1.1 仓库层测试
- VocabularyRepository
- ReminderSettingsRepository
- WordRepository
- AIRepository

##### 测试用例
1. VocabularyRepository
   - 测试CRUD操作
   - 测试词库统计功能
   - 测试词库导入导出
   - 测试事务处理

2. ReminderSettingsRepository
   - 测试提醒设置的保存和获取
   - 测试提醒时间计算
   - 测试提醒状态管理

3. WordRepository
   - 测试单词CRUD操作
   - 测试学习进度追踪
   - 测试复习计划生成
   - 测试批量操作性能

4. AIRepository
   - 测试记忆技巧生成
   - 测试例句生成
   - 测试单词推荐
   - 测试错误处理
   - 测试API调用重试机制

#### 2.1.2 DAO 层测试
- VocabularyDao
- ReminderSettingsDao
- WordDao

##### 测试用例
1. VocabularyDao
   - 测试基本CRUD操作
   - 测试关联查询
   - 测试事务操作

2. WordDao
   - 测试基本CRUD操作
   - 测试学习进度查询
   - 测试复习列表获取
   - 测试批量操作

3. ReminderSettingsDao
   - 测试提醒设置的存储
   - 测试提醒规则查询
   - 测试批量更新

#### 2.1.3 工具类测试
- DateTimeUtil
- StringUtil
- FileUtil

##### 测试用例
1. DateTimeUtil
   - 测试日期时间转换
   - 测试时间间隔计算
   - 测试格式化输出

2. StringUtil
   - 测试字符串处理
   - 测试文本格式化
   - 测试验证函数

3. FileUtil
   - 测试文件读写
   - 测试目录操作
   - 测试错误处理

### 2.2 集成测试

#### 2.2.1 数据库集成测试
- Room 数据库操作
- 事务处理
- 数据迁移

##### 测试用例：TC-1 词库管理测试

###### TC-1.1: 内置词库加载
**前置条件**：应用首次安装
**测试步骤**：
1. 启动应用
2. 进入词库页面
3. 检查内置词库列表

**预期结果**：
- 显示 CET-4 和 CET-6 词库
- 每个词库显示正确的单词数量
- 可以正常打开和浏览词库

###### TC-1.2: 创建自定义词库
**前置条件**：已登录应用
**测试步骤**：
1. 点击"创建词库"按钮
2. 输入词库名称"商务英语"
3. 选择词库类型"商务"
4. 点击确认创建

**预期结果**：
- 成功创建新词库
- 词库列表中显示新创建的词库
- 词库初始为空

#### 2.2.2 网络集成测试
- OpenAI API 调用
- 错误处理
- 重试机制

##### 测试用例：TC-5 AI功能测试

###### TC-5.1: 生成联想记忆
**前置条件**：查看单词详情
**测试步骤**：
1. 点击"AI联想"按钮
2. 等待生成结果
3. 查看联想内容

**预期结果**：
- 2秒内返回结果
- 联想内容与单词相关
- 记忆方法实用有趣

#### 2.2.3 UI 集成测试
- 导航流程
- 数据展示
- 用户交互

##### 测试用例：TC-2 记忆模式测试

###### TC-2.1: 卡片翻转
**前置条件**：进入记忆模式
**测试步骤**：
1. 查看单词卡片正面
2. 点击卡片中央
3. 观察翻转动画
4. 查看卡片背面

**预期结果**：
- 卡片流畅翻转
- 正面显示单词
- 背面显示释义和例句
- 再次点击可翻回正面

### 2.3 端到端测试

#### 2.3.1 词库管理流程
- 导入词库
- 删除词库
- 查看统计

##### 测试用例：TC-1.4 批量导入导出
**前置条件**：已有词库
**测试步骤**：
1. 选择目标词库
2. 点击"导出"按钮
3. 选择导出格式（CSV/Excel）
4. 修改导出的文件
5. 使用"导入"功能导入修改后的文件

**预期结果**：
- 成功导出所有单词数据
- 导出文件格式正确
- 成功导入修改后的数据
- 正确处理重复单词

#### 2.3.2 单词学习流程
- 开始学习
- 复习单词
- 查看进度

##### 测试用例：TC-3 测试模式测试

###### TC-3.1: 选择题测试
**前置条件**：进入测试模式
**测试步骤**：
1. 选择"选择题"模式
2. 查看题目和选项
3. 选择一个答案
4. 提交答案

**预期结果**：
- 显示正确的题目和4个选项
- 选中选项有视觉反馈
- 立即显示答案正误
- 显示正确答案解释

#### 2.3.3 提醒设置流程
- 设置提醒
- 取消提醒
- 接收通知

##### 测试用例：TC-4 复习提醒测试

###### TC-4.1: 设置提醒时间
**前置条件**：进入设置页面
**测试步骤**：
1. 点击"提醒设置"
2. 选择提醒时间"20:00"
3. 开启提醒开关
4. 保存设置

**预期结果**：
- 成功保存提醒时间
- 显示下次提醒时间
- 到时准时推送提醒

## 3. 测试策略

### 3.1 单元测试策略

1. 使用 JUnit 5 作为测试框架
2. 使用 MockK 进行依赖模拟
3. 使用 Turbine 测试协程流
4. 使用 Hilt 进行依赖注入
5. 使用 Robolectric 模拟 Android 环境

### 3.2 集成测试策略

1. 使用内存数据库进行数据库测试
2. 使用 MockWebServer 模拟网络请求
3. 使用 Espresso 进行 UI 测试
4. 使用 Hilt 提供测试依赖
5. 使用 AndroidX Test 提供测试工具

### 3.3 端到端测试策略

1. 使用真实设备或模拟器
2. 使用 UIAutomator 进行界面操作
3. 使用真实数据库
4. 使用真实网络请求
5. 使用真实系统服务

## 4. 测试环境

### 4.1 开发环境

1. Android Studio Hedgehog
2. JDK 17
3. Gradle 8.11
4. Android SDK 34
5. Android Build Tools 34.0.0

### 4.2 测试环境

1. 模拟器
   - API Level: 26-35
   - 屏幕尺寸: 多种尺寸
   - 系统版本: 多个版本

2. 真机
   - Android 8.0 及以上
   - 不同厂商设备
   - 不同屏幕尺寸

## 5. 测试时间表

### 5.1 第一阶段：单元测试（2025.02.13 - 2025.02.20）

1. 仓库层测试（2025.02.13 - 2025.02.15）
2. DAO 层测试（2025.02.16 - 2025.02.17）
3. 工具类测试（2025.02.18 - 2025.02.20）

### 5.2 第二阶段：集成测试（2025.02.21 - 2025.02.28）

1. 数据库集成测试（2025.02.21 - 2025.02.23）
2. 网络集成测试（2025.02.24 - 2025.02.25）
3. UI 集成测试（2025.02.26 - 2025.02.28）

### 5.3 第三阶段：端到端测试（2025.03.01 - 2025.03.07）

1. 词库管理流程（2025.03.01 - 2025.03.02）
2. 单词学习流程（2025.03.03 - 2025.03.04）
3. 提醒设置流程（2025.03.05 - 2025.03.07）

## 6. 测试资源

### 6.1 人力资源

1. 测试工程师：1 人
2. 开发工程师：1 人
3. UI 设计师：1 人

### 6.2 硬件资源

1. 开发机器：1 台
2. 测试设备：1 台
3. CI/CD 服务器：1 台

### 6.3 软件资源

1. 测试工具
   - Android Studio
   - Postman
   - Charles
   - Firebase Test Lab

2. 监控工具
   - Firebase Crashlytics
   - Firebase Analytics
   - LeakCanary

## 7. 风险管理

### 7.1 潜在风险

1. 测试环境不稳定
2. 测试数据不完整
3. 测试工具版本冲突
4. 测试时间不足

### 7.2 风险应对

1. 环境问题
   - 使用 Docker 容器化
   - 版本控制工具管理
   - 自动化环境搭建

2. 数据问题
   - 建立测试数据库
   - 数据备份机制
   - 定期数据同步

## 8. 测试执行命令

### 8.1 运行所有测试
```bash
# Windows
.\gradlew.bat test connectedAndroidTest

# Linux/Mac
./gradlew test connectedAndroidTest
```

### 8.2 运行特定类型的测试

#### 8.2.1 单元测试
```bash
# Windows
.\gradlew.bat test

# Linux/Mac
./gradlew test
```

#### 8.2.2 Android 测试
```bash
# Windows
.\gradlew.bat connectedAndroidTest

# Linux/Mac
./gradlew connectedAndroidTest
```

#### 8.2.3 特定模块的测试
```bash
# 运行 app 模块的单元测试
.\gradlew.bat :app:test

# 运行 app 模块的 Android 测试
.\gradlew.bat :app:connectedAndroidTest
```

### 8.3 运行特定测试类

#### 8.3.1 运行词库管理测试
```bash
.\gradlew.bat :app:connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.lexivault.e2e.VocabularyManagementTest
```

#### 8.3.2 运行单词学习测试
```bash
.\gradlew.bat :app:connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.lexivault.e2e.WordLearningTest
```

#### 8.3.3 运行提醒设置测试
```bash
.\gradlew.bat :app:connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.lexivault.e2e.ReminderSettingsTest
```

### 8.4 测试报告

测试执行完成后，可以在以下位置找到测试报告：

1. 单元测试报告：
   - HTML 报告：`app/build/reports/tests/testDebugUnitTest/index.html`
   - XML 报告：`app/build/test-results/testDebugUnitTest/`

2. Android 测试报告：
   - HTML 报告：`app/build/reports/androidTests/connected/index.html`
   - XML 报告：`app/build/outputs/androidTest-results/connected/`

### 8.5 持续集成注意事项

1. 在 CI 环境中运行测试时，建议添加以下参数：
```bash
.\gradlew.bat test connectedAndroidTest --no-daemon --max-workers=2
```

2. 对于 UI 测试，确保模拟器或真机已正确配置：
```bash
# 检查可用设备
adb devices

# 等待设备就绪
adb wait-for-device
```

3. 测试失败时的日志收集：
```bash
# 收集测试失败日志
adb logcat -d > test-failure.log
```
