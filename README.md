
# Android Washer Locator App

这是一个Android应用程序，用于选择不同的楼栋并显示当前可用的洗衣机。用户可以选择楼栋，应用会记住用户选择的楼栋，下次打开应用时默认选择上次选择的楼栋。

## 功能

- 显示一个Spinner，用户可以选择不同的楼栋
- 使用RecyclerView展示当前可用的洗衣机
- 记住用户选择的楼栋，下次打开应用时默认选择上次选择的楼栋
- 根据系统主题自动决定状态栏颜色

## 依赖

- OkHttp: 用于进行网络请求
- Gson: 用于解析JSON数据

## 安装和运行

1. 克隆这个仓库：
    ```bash
    git clone https://github.com/your-username/washer-locator-app.git
    ```
2. 打开Android Studio，选择 `File -> Open` 并选择克隆的项目目录。
3. 确保你的项目级别和应用级别的 `build.gradle` 文件中包含以下依赖：
    ```gradle
    dependencies {
        implementation 'com.squareup.okhttp3:okhttp:4.9.1'
        implementation 'com.google.code.gson:gson:2.8.6'
    }
    ```
4. 连接你的Android设备或启动Android模拟器。
5. 点击运行按钮，启动应用程序。

## 使用方法

1. 启动应用程序。
2. 默认情况下，应用程序会选择楼栋 "403" 幢。
3. 点击Spinner选择不同的楼栋，应用程序会显示该楼栋下可用的洗衣机。
4. 应用会记住用户选择的楼栋，下次打开应用时默认选择上次选择的楼栋。

## 项目结构

- `MainActivity.java`: 包含主要的业务逻辑，包括加载数据、处理用户交互等。
- `WasherAdapter.java`: RecyclerView适配器，用于展示洗衣机列表。
- `activity_main.xml`: 包含Spinner和RecyclerView的布局文件。
- `res/values/styles.xml`: 包含应用主题设置。
- `res/values-night/styles.xml`: 包含夜间模式的应用主题设置。
