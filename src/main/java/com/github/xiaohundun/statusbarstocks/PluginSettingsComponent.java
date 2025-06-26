package com.github.xiaohundun.statusbarstocks;

import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

public class PluginSettingsComponent {
    private final JPanel myMainPanel;
    private final JBTextField stockCode = new JBTextField();
    private final JBCheckBox nameVisible = new JBCheckBox("展示名字");
    private final JBCheckBox codeVisible = new JBCheckBox("展示代码后3位");
    private final JBCheckBox pinyinVisible = new JBCheckBox("展示拼音首字母");
    private final JBCheckBox priceVisible = new JBCheckBox("展示价格");
    private final JBCheckBox changePercentageVisible = new JBCheckBox("展示涨跌幅");
    private final JBCheckBox lowProfileMode = new JBCheckBox("低调模式");
    private final JBCheckBox percentVisible = new JBCheckBox("展示 %");
    //    private final JBTextField refreshInterval = new JBTextField();
    private final JBIntSpinner refreshInterval = new JBIntSpinner(1, 1, 3600);
    private final JBCheckBox marketCloseVisible = new JBCheckBox("闭市可见");

    public PluginSettingsComponent() {
        stockCode.setText(AppSettingsState.getInstance().stockCode);
        refreshInterval.setNumber(AppSettingsState.getInstance().refreshInterval);
        priceVisible.setSelected(AppSettingsState.getInstance().priceVisible);
        changePercentageVisible.setSelected(AppSettingsState.getInstance().changePercentageVisible);
        lowProfileMode.setSelected(AppSettingsState.getInstance().lowProfileMode);
        nameVisible.setSelected(AppSettingsState.getInstance().nameVisible);
        codeVisible.setSelected(AppSettingsState.getInstance().codeVisible);
        pinyinVisible.setSelected(AppSettingsState.getInstance().pinyinVisible);
        percentVisible.setSelected(AppSettingsState.getInstance().percentVisible);
        marketCloseVisible.setSelected(AppSettingsState.getInstance().marketCloseVisible);
        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Stock code(comma-separated): "), stockCode, 1, false)
                .addLabeledComponent(new JBLabel("Refresh interval(seconds): "), refreshInterval, 1, false)
                .addComponent(nameVisible, 1)
                .addComponent(codeVisible, 1)
                .addComponent(pinyinVisible, 1)
                .addComponent(priceVisible, 1)
                .addComponent(changePercentageVisible, 1)
                .addComponent(lowProfileMode, 1)
                .addComponent(percentVisible, 1)
                .addComponent(marketCloseVisible, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return stockCode;
    }

    public String getStockCode() {
        return stockCode.getText();
    }

    public boolean getPriceVisible() {
        return priceVisible.isSelected();
    }

    public boolean getLowProfileMode() {
        return lowProfileMode.isSelected();
    }

    public boolean getChangePercentageVisible() {
        return changePercentageVisible.isSelected();
    }

    public boolean getNameVisible() {
        return nameVisible.isSelected();
    }

    public boolean getPinyinVisible() {
        return pinyinVisible.isSelected();
    }

    public boolean getCodeVisible() {
        return codeVisible.isSelected();
    }
    public boolean getPercentVisible() {
        return percentVisible.isSelected();
    }
    public boolean getMarketCloseVisible() {
        return marketCloseVisible.isSelected();
    }

    public int getRefreshInterval() {
        return refreshInterval.getNumber();
    }
}
