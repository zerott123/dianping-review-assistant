# 评一下

Android WebView 大众点评评价写作工具。输入店名/菜名，AI 生成真实口语化的用餐评价。

## 截图

> TODO: 添加 App 截图

## 功能

- 输入店名或菜名，一键生成大众点评风格评价
- 好评 / 中评 / 差评三种评价类型
- 随性 / 客观 / 吃货三种写作风格
- 自定义字数（50 字 / 110 字 / 自由输入）
- 一键复制到剪贴板
- 暗黑模式自适应
- 设置项 localStorage 持久化

## 技术栈

- Kotlin + WebView + `addJavascriptInterface()` JS Bridge
- DeepSeek Chat Completions API（OpenAI 兼容）
- OkHttp 4.x + kotlinx.serialization
- 纯 HTML/CSS/JS 单文件前端（中式笔记「手账」设计）
- CSS 变量主题系统，支持 `prefers-color-scheme: dark`

## 项目结构

```
app/src/main/
├── assets/
│   └── index.html           # WebView 前端（单文件）
├── java/com/dianping/assistant/
│   ├── MainActivity.kt      # WebView 宿主 + JS Bridge
│   ├── PromptBuilder.kt     # System/User Prompt 构造
│   └── api/
│       └── DeepSeekApi.kt   # DeepSeek API 客户端
└── res/                     # Android 资源
```

## 构建

1. 在 `local.properties` 设置 Android SDK 路径：
   ```
   sdk.dir=C\:\\Users\\...\\AppData\\Local\\Android\\Sdk
   ```
2. 设置 JDK 17+：
   ```powershell
   $env:JAVA_HOME = "<JDK路径>"
   ```
3. 构建调试 APK：
   ```powershell
   .\gradlew.bat assembleDebug
   ```
4. APK 输出：`app/build/outputs/apk/debug/app-debug.apk`

## 设计

设计遵循三阶段流水线（awesome-design-html → frontend-design → ui-ux-pro-max），当前为**中式笔记「手账」**风格：

- 宣纸暖白底色 + 4px 点阵纹理
- 朱砂红 `#9D2B1F` 强调色
- 墨色暖黑文字 `#1C1814`
- Noto Serif SC 衬线标题
- 4px 克制圆角
- WCAG AA 对比度全项通过
- 触控目标 ≥44px，safe-area 适配

## License

MIT
