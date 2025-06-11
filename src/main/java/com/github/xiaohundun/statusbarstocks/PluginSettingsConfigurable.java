package com.github.xiaohundun.statusbarstocks;

import com.github.xiaohundun.statusbarstocks.widgets.StocksWidgetFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PluginSettingsConfigurable implements Configurable {

    private PluginSettingsComponent pluginSettingsComponent;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Statusbar Stock Plus";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return pluginSettingsComponent.getPreferredFocusedComponent();
    }

    @Override
    public @Nullable JComponent createComponent() {
        pluginSettingsComponent = new PluginSettingsComponent();
        return pluginSettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState settings = AppSettingsState.getInstance();
        return !pluginSettingsComponent.getStockCode().equals(settings.stockCode)
                || !pluginSettingsComponent.getPriceVisible() == settings.priceVisible
                || !pluginSettingsComponent.getChangePercentageVisible() == settings.changePercentageVisible
                || !pluginSettingsComponent.getNameVisible() == settings.nameVisible
                || !pluginSettingsComponent.getCodeVisible() == settings.codeVisible
                || !pluginSettingsComponent.getPercentVisible() == settings.percentVisible
                || !(pluginSettingsComponent.getRefreshInterval() == settings.refreshInterval)
                || !pluginSettingsComponent.getMarketCloseVisible() == settings.marketCloseVisible
                || !pluginSettingsComponent.getLowProfileMode() == settings.lowProfileMode;
    }

    @Override
    public void apply() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.stockCode               = pluginSettingsComponent.getStockCode();
        settings.refreshInterval         = pluginSettingsComponent.getRefreshInterval();
        settings.priceVisible            = pluginSettingsComponent.getPriceVisible();
        settings.changePercentageVisible = pluginSettingsComponent.getChangePercentageVisible();
        settings.lowProfileMode          = pluginSettingsComponent.getLowProfileMode();
        settings.nameVisible             = pluginSettingsComponent.getNameVisible();
        settings.codeVisible             = pluginSettingsComponent.getCodeVisible();
        settings.percentVisible          = pluginSettingsComponent.getPercentVisible();
        settings.marketCloseVisible      = pluginSettingsComponent.getMarketCloseVisible();
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
            if (statusBar != null) {
                statusBar.updateWidget(StocksWidgetFactory.ID);
            }
        }
    }
}
