package com.github.xiaohundun.statusbarstocks;

import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.components.*;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PluginSettingsComponent {
    private final JPanel myMainPanel;
    private final JBTextField stockCode = new JBTextField();
    private final JBCheckBox nameVisible = new JBCheckBox("显示名字");
    private final JBCheckBox codeVisible = new JBCheckBox("显示代码后3位");
    private final JBCheckBox pinyinVisible = new JBCheckBox("显示拼音首字母");
    private final JBCheckBox priceVisible = new JBCheckBox("显示价格");
    private final JBCheckBox changePercentageVisible = new JBCheckBox("显示涨跌幅");
    private final JBCheckBox lowProfileMode = new JBCheckBox("低调模式");
    private final JBCheckBox percentVisible = new JBCheckBox("显示 %");
    //    private final JBTextField refreshInterval = new JBTextField();
    private final JBIntSpinner refreshInterval = new JBIntSpinner(1, 1, 3600);
    private final JBCheckBox marketCloseVisible = new JBCheckBox("闭市可见");
    private final JBRadioButton nameDisabledRatioBtn = new JBRadioButton("不显示");
    private final JBRadioButton nameRatioBtn = new JBRadioButton("股票名", AppSettingsState.getInstance().nameVisible);
    private final JBRadioButton codeRadioBtn = new JBRadioButton("代码后3位", AppSettingsState.getInstance().codeVisible);
    private final JBRadioButton pinyinRatioBtn = new JBRadioButton("拼音首字母", AppSettingsState.getInstance().pinyinVisible);

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
                .addLabeledComponent(new JBLabel("股票列表(用,分隔): "), stockCode, 1, false)
                .addLabeledComponent(new JBLabel("刷新间隔(秒): "), refreshInterval, 1, false)
                .addLabeledComponent(new JBLabel("股票标识："), createStockIDPanel(), 1, false)
                /*.addComponent(nameVisible, 1)
                .addComponent(codeVisible, 1)
                .addComponent(pinyinVisible, 1)*/
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

    private JPanel createStockIDPanel() {
        // 使用水平布局的面板
        JBPanel<JBPanel> panel = new JBPanel<>(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false); // 透明背景

        // 创建标签
        /*JBLabel label = new JBLabel("股票标识：");
        panel.add(label);*/

        // 创建按钮组确保单选
        ButtonGroup placementGroup = new ButtonGroup();
        placementGroup.add(nameDisabledRatioBtn);
        placementGroup.add(nameRatioBtn);
        placementGroup.add(codeRadioBtn);
        placementGroup.add(pinyinRatioBtn);

        // 添加按钮到面板
        panel.add(nameDisabledRatioBtn);
        panel.add(nameRatioBtn);
        panel.add(codeRadioBtn);
        panel.add(pinyinRatioBtn);

        // 添加事件监听器
        ActionListener listener = e -> {
            if (nameDisabledRatioBtn.isSelected()) {
                nameVisible.setSelected(false);
                codeVisible.setSelected(false);
                pinyinVisible.setSelected(false);
            } else if (nameRatioBtn.isSelected()) {
                nameVisible.setSelected(true);
                codeVisible.setSelected(false);
                pinyinVisible.setSelected(false);
            } else if (codeRadioBtn.isSelected()) {
                nameVisible.setSelected(false);
                codeVisible.setSelected(true);
                pinyinVisible.setSelected(false);
            } else if (pinyinRatioBtn.isSelected()) {
                nameVisible.setSelected(false);
                codeVisible.setSelected(false);
                pinyinVisible.setSelected(true);
            }
        };

        nameDisabledRatioBtn.addActionListener(listener);
        nameRatioBtn.addActionListener(listener);
        codeRadioBtn.addActionListener(listener);
        pinyinRatioBtn.addActionListener(listener);
        return panel;
    }
}
