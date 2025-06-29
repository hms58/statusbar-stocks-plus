# statusbar-stocks

[![GitHub latest commit](https://badgen.net/github/last-commit/hms58/statusbar-stocks-plus)](https://github.com/hms58/statusbar-stocks-plus/commit/)
![Build](https://github.com/hms58/statusbar-stocks-plus/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/27234-statusbar-stocks-plus.svg)](https://plugins.jetbrains.com/plugin/27234-statusbar-stocks-plus)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/27234-statusbar-stocks-plus.svg)](https://plugins.jetbrains.com/plugin/27234-statusbar-stocks-plus)
[![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/r/rating/27234-statusbar-stocks-plus)](https://plugins.jetbrains.com/plugin/27234-statusbar-stocks-plus.svg)
[![GitHub issues](https://img.shields.io/github/issues/hms58/statusbar-stocks-plus.svg)](https://github.com/hms58/statusbar-stocks-plus/issues/)
[![GitHub forks](https://img.shields.io/github/forks/hms58/statusbar-stocks-plus.svg?style=social&label=Fork&maxAge=2592000)](https://github.com/hms58/statusbar-stocks-plus/network/)
[![GitHub stars](https://img.shields.io/github/stars/hms58/statusbar-stocks-plus.svg?style=social&label=Star&maxAge=2592000)](https://github.com/hms58/statusbar-stocks-plus/stargazers/)
[![GitHub watchers](https://img.shields.io/github/watchers/hms58/statusbar-stocks-plus.svg?style=social&label=Watch&maxAge=2592000)](https://github.com/hms58/statusbar-stocks-plus/watchers/)

## 支持的特性
- [x] 支持A股、港股和美股等股票行情展示
- [x] 低调模式：涨跌幅背景色展示。默认：涨-红色，跌-绿色
- [x] 闭市显示：在非交易时间（9:00-16:30）不显示股票行情
- [x] 当左击股票行情状态栏时，影藏股票行情，只显示图标![image](https://github.com/user-attachments/assets/6103a519-24f6-4097-a997-8dda1a891fd2)（v2.2.0版本支持）

## Template ToDo list
- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [ ] Get familiar with the [template documentation][template].
- [ ] Adjust the [pluginGroup](./gradle.properties), [plugin ID](./src/main/resources/META-INF/plugin.xml) and [sources package](./src/main/kotlin).
- [ ] Adjust the plugin description in `README` (see [Tips][docs:plugin-description])
- [ ] Review the [Legal Agreements](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html?from=IJPluginTemplate).
- [ ] [Publish a plugin manually](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate) for the first time.
- [ ] Set the `PLUGIN_ID` in the above README badges.
- [ ] Set the [Plugin Signing](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html?from=IJPluginTemplate) related [secrets](https://github.com/JetBrains/intellij-platform-plugin-template#environment-variables).
- [ ] Set the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html?from=IJPluginTemplate).
- [ ] Click the <kbd>Watch</kbd> button on the top of the [IntelliJ Platform Plugin Template][template] to be notified about releases containing new features and fixes.

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "statusbar-stocks-plus"</kbd> >
  <kbd>Install</kbd>

- Manually:

  Download the [latest release](https://github.com/hms58/statusbar-stocks-plus/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation

## Stargazers over time
[![Stargazers over time](https://starchart.cc/hms58/statusbar-stocks-plus.svg)](https://starchart.cc/hms58/statusbar-stocks-plus)

## Github Stars Sparklines
[![Sparkline](https://stars.medv.io/hms58/statusbar-stocks-plus.svg)](https://stars.medv.io/hms58/statusbar-stocks-plus)

## Plugin description
<!-- Plugin description -->
<div>
    <p>
      Supports displaying real-time stock quotes in <b>the bottom status bar</b>. 
      Supports <b>stocks</b> and <b>funds</b>. Stocks include <b>A-shares</b>, <b>Hong Kong stocks</b>, and <b>US stocks</b>.
    </p>
    <h2>Donation</h2>
    <p>If you like this plugin, you can <a href="https://ifdian.net/order/create?user_id=af5669aafee611ef988f5254001e7c00&remark=&affiliate_code=" target="_blank">buy me a cup of coffee</a>. Thank you!</p>
</div>
<!-- Plugin description end -->
