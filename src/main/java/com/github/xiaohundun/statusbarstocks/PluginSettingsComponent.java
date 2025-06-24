package com.github.xiaohundun.statusbarstocks;

import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.io.Console;

public class PluginSettingsComponent {
    private final JPanel myMainPanel;
    private final JBTextField stockCode = new JBTextField();
    private final JBCheckBox nameVisible = new JBCheckBox("名称");
    private final JBCheckBox pyMode = new JBCheckBox("拼音");
    private final JBCheckBox codeVisible = new JBCheckBox("代码");

    private final JBCheckBox priceVisible = new JBCheckBox("显示价格");
    private final JBCheckBox changePercentageVisible = new JBCheckBox("显示幅度");
    private final JBCheckBox coloredFont = new JBCheckBox("彩色显示");
    private final JBCheckBox lowProfileMode = new JBCheckBox("low mode");
    private final JBCheckBox percentVisible = new JBCheckBox("带上%");
    private final JBIntSpinner refreshInterval = new JBIntSpinner(2, 1, 3600);
    private final JBCheckBox marketCloseVisible = new JBCheckBox("休市显示");

    public PluginSettingsComponent() {
        AppSettingsState settings = AppSettingsState.getInstance();
        stockCode.setText(settings.stockCode);
        refreshInterval.setNumber(settings.refreshInterval);
        priceVisible.setSelected(settings.priceVisible);
        changePercentageVisible.setSelected(settings.changePercentageVisible);
        coloredFont.setSelected(settings.coloredFont);
        pyMode.setSelected(settings.pyMode);
        lowProfileMode.setSelected(settings.lowProfileMode);
        nameVisible.setSelected(settings.nameVisible);
        codeVisible.setSelected(settings.codeVisible);
        percentVisible.setSelected(settings.percentVisible);
        marketCloseVisible.setSelected(settings.marketCloseVisible);

        // 创建互斥组面板
        JPanel exclusiveGroupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        exclusiveGroupPanel.add(pyMode);
        exclusiveGroupPanel.add(nameVisible);
        exclusiveGroupPanel.add(codeVisible);

        // 添加互斥逻辑
        ItemListener exclusiveListener = e -> {
            JBCheckBox source = (JBCheckBox) e.getSource();
            if (source.isSelected()) {
                // 取消其他两个复选框的选择
                if (source == nameVisible) {
                    pyMode.setSelected(false);
                    codeVisible.setSelected(false);
                } else if (source == pyMode) {
                    nameVisible.setSelected(false);
                    codeVisible.setSelected(false);
                } else if (source == codeVisible) {
                    nameVisible.setSelected(false);
                    pyMode.setSelected(false);
                }
            }
        };

        nameVisible.addItemListener(exclusiveListener);
        pyMode.addItemListener(exclusiveListener);
        codeVisible.addItemListener(exclusiveListener);

        // 创建"显示幅度"和"彩色显示"的面板
        JPanel amplitudeColorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        amplitudeColorPanel.add(changePercentageVisible);
        amplitudeColorPanel.add(coloredFont);

        // 设置彩色显示的初始可见性
        coloredFont.setVisible(changePercentageVisible.isSelected());

        // 添加事件监听器，当"显示幅度"状态改变时，控制"彩色显示"的可见性
        changePercentageVisible.addItemListener(e -> {
            coloredFont.setVisible(changePercentageVisible.isSelected());
        });

        // 构建主面板
        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("代码列表(, 分割): "), stockCode, 1, false)
                .addLabeledComponent(new JBLabel("刷新间隔(s): "), refreshInterval, 1, false)
                .addLabeledComponent(new JBLabel("显示模式:"), exclusiveGroupPanel, 1, false)
                .addComponent(priceVisible, 1)
                .addComponent(amplitudeColorPanel, 1) // 将两个控件放在同一行
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
    public boolean getColoredFont() {
        return coloredFont.isSelected();
    }
    public boolean getPyMode() {
        return pyMode.isSelected();
    }

    public boolean getNameVisible() {
        return nameVisible.isSelected();
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
