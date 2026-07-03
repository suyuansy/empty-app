# FakeSlot

一个用于验证 One UI 桌面占位图标效果的极小 Android 项目。

它默认只有一个抽屉应用：`占位工具`。打开后可以做两件事：

- 生成多个 pinned shortcuts，观察 One UI 8.1 是否保留右下角来源 badge。
- 临时开启几个 `activity-alias` 分身，对比标准 launcher entry 的图标观感。

## 验证步骤

1. 用 Android Studio 打开这个文件夹：`D:\A_Codes\empty-app`。
2. 等待 Gradle Sync 完成。
3. 手机打开开发者选项和 USB 调试，用数据线连接电脑。
4. Android Studio 顶部设备选择你的手机，点击 Run。
5. 手机上打开 `占位工具`。
6. 先分别点击 `添加桌面图标：工具`、`添加桌面图标：笔记` 等按钮。
7. 回到 One UI 桌面，观察这些图标：
   - 是否被裁成和普通 app 一样的圆角形状。
   - 是否仍有右下角来源 badge。
   - 标签、留白、缩放是否自然。
8. 如果 shortcut 仍然露馅，回到 `占位工具`，点击 `开启 alias 测试入口`。
9. 打开抽屉，找到 `工具`、`笔记`、`空间` 三个分身，拖到桌面后对比。
10. 测试完点击 `关闭 alias 测试入口`，或者直接卸载 `占位工具`。

## 如果 Android Studio 提示没有 Gradle Wrapper

这个项目没有附带 `gradle-wrapper.jar`。如果 Android Studio 无法直接 Sync：

1. 在 Android Studio 新建一个 `Empty Views Activity` 项目，语言选 `Java`，Minimum SDK 选 `API 26` 或更高。
2. 关闭新项目。
3. 把本项目的 `app/src/main` 整个目录复制到新项目的 `app/src/main`。
4. 把新项目 `app/build.gradle` 里的 `namespace` 和 `applicationId` 改成 `com.example.fakeslot`。
5. 再打开新项目并 Run。

## 判断标准

如果 pinned shortcuts 没有右下角 badge，而且圆角、缩放、留白都自然，就走单 app + shortcuts。

如果 pinned shortcuts 仍然有 badge 或明显不受 One UI 主题管理，就走 `activity-alias`。它更像普通 app，但抽屉里会有多个分身。
