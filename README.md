# LexiVault - 智能英语词汇学习应用

LexiVault 是一个智能的英语词汇学习应用，它使用 AI 技术来帮助用户更有效地学习和记忆英语单词。

## 功能特点

- 智能单词记忆方法生成
- 上下文相关的例句生成
- 个性化单词推荐
- 生词本管理
- 学习进度追踪
- 定时复习提醒

## 开发环境设置

1. 克隆仓库：
```bash
git clone https://github.com/dennyops666/Android-LexiVault.git
```

2. 在项目根目录创建 `secrets.properties` 文件，添加以下内容：
```properties
openai.api.key=your_openai_api_key_here
```

3. 在 Android Studio 中打开项目
4. 同步 Gradle 依赖
5. 运行应用

## 技术栈

- Kotlin
- Android Jetpack Components
- Room Database
- Retrofit
- Hilt
- OpenAI API
- JUnit 5
- Mockito

## 测试

运行单元测试：
```bash
./gradlew test
```

运行 UI 测试：
```bash
./gradlew connectedAndroidTest
```

## 贡献指南

1. Fork 仓库
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 注意事项

- 不要将 `secrets.properties` 文件提交到版本控制系统
- 定期更新 OpenAI API 密钥
- 遵循项目的代码风格指南

## 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件