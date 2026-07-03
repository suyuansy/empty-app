# FakeSlot

一个用于 One UI 桌面占位的极小 Android 项目。

它包含一个真实本体应用 `占位工具`，以及四个固定的 `activity-alias` 分身：

- `Lorem`
- `Ipsum`
- `Amet`
- `Vita`

四个分身会像普通应用一样出现在启动器和抽屉中，使用 adaptive icon 交给 One UI Home 裁切图标形状。点击任意分身会打开同一个极简空白页面，后续可以继续迭代页面内容。

## 使用

1. 用 Android Studio 打开这个文件夹：`D:\A_Codes\empty-app`。
2. 等待 Gradle Sync 完成。
3. 手机打开开发者选项和 USB 调试，用数据线连接电脑。
4. Android Studio 顶部设备选择你的手机，点击 Run。
5. 在 One UI 抽屉中找到 `Lorem`、`Ipsum`、`Amet`、`Vita`，拖到桌面占位。
