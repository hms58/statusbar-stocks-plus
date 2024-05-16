package com.github.xiaohundun.statusbarstocks;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

public class PluginSettingsComponent {
    private final JPanel myMainPanel;
    private final JBTextField stockCode = new JBTextField();
    private final JBCheckBox priceVisible = new JBCheckBox("显示价格");
    private final JBCheckBox changePercentageVisible = new JBCheckBox("显示涨跌幅");


    public PluginSettingsComponent() {
        stockCode.setText(AppSettingsState.getInstance().stockCode);
        priceVisible.setSelected(AppSettingsState.getInstance().priceVisible);
        changePercentageVisible.setSelected(AppSettingsState.getInstance().changePercentageVisible);
        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Stock code(comma-separated): "), stockCode, 1, false)
                .addComponent(priceVisible,1)
                .addComponent(changePercentageVisible,1)
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

    public void setPriceVisible(boolean visible){
        priceVisible.setSelected(visible);
    }

    public boolean getChangePercentageVisible() {
        return changePercentageVisible.isSelected();
    }

    public void setChangePercentageVisible(boolean visible){
        changePercentageVisible.setSelected(visible);
    }
}
