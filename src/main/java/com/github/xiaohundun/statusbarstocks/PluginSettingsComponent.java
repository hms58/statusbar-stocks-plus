package com.github.xiaohundun.statusbarstocks;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

public class PluginSettingsComponent {
    private final JPanel myMainPanel;
    private final JBTextField stockCode = new JBTextField();
    private final JBCheckBox nameVisible = new JBCheckBox("show name");
    private final JBCheckBox codeVisible = new JBCheckBox("show code");
    private final JBCheckBox priceVisible = new JBCheckBox("show price");
    private final JBCheckBox changePercentageVisible = new JBCheckBox("show change");
    private final JBCheckBox lowProfileMode = new JBCheckBox("low mode");
    private final JBCheckBox percentVisible = new JBCheckBox("show %");


    public PluginSettingsComponent() {
        stockCode.setText(AppSettingsState.getInstance().stockCode);
        priceVisible.setSelected(AppSettingsState.getInstance().priceVisible);
        changePercentageVisible.setSelected(AppSettingsState.getInstance().changePercentageVisible);
        lowProfileMode.setSelected(AppSettingsState.getInstance().lowProfileMode);
        nameVisible.setSelected(AppSettingsState.getInstance().nameVisible);
        codeVisible.setSelected(AppSettingsState.getInstance().codeVisible);
        percentVisible.setSelected(AppSettingsState.getInstance().percentVisible);
        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Stock code(comma-separated): "), stockCode, 1, false)
                .addComponent(nameVisible, 1)
                .addComponent(codeVisible, 1)
                .addComponent(priceVisible, 1)
                .addComponent(changePercentageVisible, 1)
                .addComponent(lowProfileMode, 1)
                .addComponent(percentVisible, 1)
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

    public boolean getCodeVisible() {
        return codeVisible.isSelected();
    }
    public boolean getPercentVisible() {
        return percentVisible.isSelected();
    }
}
