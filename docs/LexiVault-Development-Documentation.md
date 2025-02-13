# **LexiVault - 英语单词记忆安卓APP 开发文档**

## **1. 概述**
### **1.1 项目简介**
LexiVault 是一款基于 **SQLite 本地存储** 和 **OpenAI AI 技术** 的英语单词记忆 Android 应用。它支持 **词库管理、记忆模式、测试模式、复习提醒**，并结合 AI 实现 **智能单词推荐、AI 例句生成、联想记忆**，帮助用户更高效地学习英语单词。

### **1.2 技术栈**
- **编程语言**：Kotlin
- **UI 框架**：Jetpack Compose
- **数据库**：Room (基于 SQLite)
- **AI 服务**：OpenAI API (GPT-4)
- **网络请求**：Retrofit + OkHttp
- **依赖管理**：Gradle
- **开发环境**：Android Studio
- **本地存储**：SharedPreferences（用于存储用户偏好设置）

---

## **2. 系统架构**
LexiVault 采用 **前端+本地数据库+AI API** 的架构，主要模块如下：

### **2.1 架构设计**
1. **UI 层（Jetpack Compose）**
   - 主页（显示学习进度、推荐单词、快捷测试入口）
   - 词库管理（查看、添加、删除单词）
   - 记忆模式（卡片翻转学习）
   - 测试模式（选择题、填空测试）
   - 复习提醒（定期推送通知）

2. **数据层（Room + Repository）**
   - 词库表 `WordEntity`
   - 生词本表 `WordBookEntity`
   - 学习记录表 `StudyRecordEntity`
   - 用户设置表 `UserSettingsEntity`

3. **AI 交互层（OpenAI API + Retrofit）**
   - AI 单词联想记忆生成
   - AI 例句生成
   - 智能单词推荐
   - 语法检查与单词纠错

4. **后台服务层（WorkManager）**
   - 定期提醒用户复习
   - 记录学习进度

5. **安全与性能优化**
   - **本地数据库加密**：采用 EncryptedSharedPreferences 保护敏感数据。
   - **网络请求缓存**：使用 OkHttp 缓存机制减少 API 调用。
   - **AI 结果本地存储**：降低 API 请求次数，提高响应速度。

---

## **3. 数据库设计**
### **3.1 数据表设计**
#### **`WordEntity`（单词表）**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int (Primary Key) | 唯一ID |
| word | String | 单词 |
| meaning | String | 释义 |
| phonetic | String | 音标 |
| example | String | 例句 |
| category | String | 分类（考试/商务/日常） |
| created_at | Long | 添加时间 |
| ai_generated | Boolean | 是否由 AI 生成 |

#### **`WordBookEntity`（生词本表）**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int (Primary Key) | 唯一ID |
| word_id | Int | 关联 `WordEntity` ID |
| added_at | Long | 添加时间 |

#### **`StudyRecordEntity`（学习记录表）**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int (Primary Key) | 唯一ID |
| word_id | Int | 关联 `WordEntity` ID |
| last_reviewed | Long | 上次复习时间 |
| review_count | Int | 复习次数 |
| success_rate | Float | 正确率 |

#### **`UserSettingsEntity`（用户设置表）**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int (Primary Key) | 唯一ID |
| reminder_time | String | 复习提醒时间 |
| night_mode | Boolean | 夜间模式开关 |
| ai_suggestions | Boolean | 是否启用 AI 推荐 |

---

## **4. API 设计**
### **4.1 OpenAI API 请求**
#### **获取 AI 生成单词联想记忆**
- **Endpoint**: `POST https://api.openai.com/v1/completions`
- **Headers**:
  - `Authorization: Bearer {API_KEY}`
  - `Content-Type: application/json`
- **Request Body**:
```json
{
  "model": "gpt-4",
  "prompt": "为单词 'apple' 生成联想记忆技巧。",
  "max_tokens": 50
}
```
- **Response**:
```json
{
  "choices": [
    {
      "text": "想象一颗苹果掉在你的头上，就像牛顿发现万有引力一样！"
    }
  ]
}
```

---

## **5. 关键功能实现**
### **5.1 词库管理（Room Database）**
#### **创建数据库表**
```kotlin
@Entity(tableName = "word_table")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,
    val meaning: String,
    val phonetic: String,
    val example: String,
    val category: String,
    val aiGenerated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
```

#### **DAO 接口**
```kotlin
@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity)

    @Query("SELECT * FROM word_table ORDER BY createdAt DESC")
    fun getAllWords(): Flow<List<WordEntity>>
}
```

---

## **6. 未来优化**
- **自适应 AI 复习算法**：根据用户记忆曲线智能调整复习间隔。
- **本地缓存 AI 结果**：减少 API 调用成本，提高响应速度。
- **多平台适配（iOS & Web）**：扩展到更多设备。
- **AI 语音识别**：结合 OpenAI Whisper 进行单词发音训练。

---

## **7. 结论**
LexiVault 通过本地存储结合 AI **优化学习体验**，帮助用户高效背单词。本开发文档详细描述了系统架构、数据库、API 交互、关键功能实现和 UI 代码示例，为后续开发提供完整指导。
