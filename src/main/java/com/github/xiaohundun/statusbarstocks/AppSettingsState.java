package com.github.xiaohundun.statusbarstocks;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Supports storing the application settings in a persistent way.
 * The {@link State} and {@link Storage} annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
        name = "org.intellij.sdk.settings.AppSettingsState",
        storages = @Storage("StatusbarStockPlusStorage.xml")
)
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

    public String stockCode = "sh000001,sz399001,hkHSI,fuCN";
    public boolean changePercentageVisible = true;
    public boolean priceVisible = true;
    public boolean lowProfileMode = false;
    public boolean nameVisible = true;
    public boolean codeVisible = false;
    public boolean percentVisible = true; // 显示百分比符号
    public int  refreshInterval = 5; // 刷新间隔，单位秒
    public boolean marketCloseVisible = true; // 市场闭市是否可见
    public boolean pinyinVisible = false; // 展示股票名拼音首字母
    public boolean showingStock = true; // 默认展示股票信息，而不是图标

    public static AppSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(AppSettingsState.class);
    }

    @Override
    public AppSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}