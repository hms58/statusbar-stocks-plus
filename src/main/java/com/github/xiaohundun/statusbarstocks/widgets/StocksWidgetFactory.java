package com.github.xiaohundun.statusbarstocks.widgets;

import com.github.xiaohundun.statusbarstocks.AppSettingsState;
import com.github.xiaohundun.statusbarstocks.EastmoneyService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.wm.CustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import com.intellij.openapi.wm.impl.status.TextPanel;
import com.intellij.util.concurrency.EdtExecutorService;
import com.intellij.util.ui.update.Activatable;
import com.intellij.util.ui.update.UiNotifyConnector;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import javax.swing.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class StocksWidgetFactory implements StatusBarWidgetFactory {
    public static final String ID = "StocksStatusBar";

    @Override
    public @NotNull @NonNls String getId() {
        return ID;
    }

    @Override
    public @NotNull @NlsContexts.ConfigurableName String getDisplayName() {
        return "Statusbar Stocks";
    }

    @Override
    public boolean isAvailable(@NotNull Project project) {
        return true;
    }

    @Override
    public @NotNull StatusBarWidget createWidget(@NotNull Project project) {
        return new StockWidget();
    }

    @Override
    public void disposeWidget(@NotNull StatusBarWidget widget) {
        Disposer.dispose(widget);
    }

    @Override
    public boolean canBeEnabledOn(@NotNull StatusBar statusBar) {
        return true;
    }



    private static final class StockWidget extends TextPanel implements CustomStatusBarWidget, Activatable {
        private java.util.concurrent.ScheduledFuture<?> myFuture;

        @Override
        public void showNotify() {
            myFuture = EdtExecutorService.getScheduledExecutorInstance().scheduleWithFixedDelay(
                    this::updateState, 5, 5, TimeUnit.SECONDS
            );
        }

        @Override
        public void hideNotify() {
            if (myFuture != null) {
                myFuture.cancel(true);
                myFuture = null;
            }
        }

        public StockWidget() {
            setText(getCodeText());
            new UiNotifyConnector(this, this);
        }

        @Override
        public JComponent getComponent() {
            return this;
        }

        @Override
        public @NotNull @NonNls String ID() {
            return ID;
        }

        @Override
        public void install(@NotNull StatusBar statusBar) {
        }

        @Override
        public void dispose() {
        }

        public void updateState(){
            LocalTime now = LocalTime.now();
            LocalTime nine = LocalTime.of(9, 0);
            LocalTime three = LocalTime.of(15, 0);
            if (now.isAfter(nine) && now.isBefore(three)) {
                setText(getCodeText());
            }
        }

        public String getCodeText(){
            String code = AppSettingsState.getInstance().stockCode;
            String[] codeList = code.replaceAll("，", ",").split(",");
            String text = "";
            for (String s : codeList) {
                JSONObject jsonObject = EastmoneyService.getDetail(s);
                JSONObject data = jsonObject.getJSONObject("data");
                String     name = data.getString("f58");
                Object f170 = data.get("f170");
                if (f170 instanceof BigDecimal) {
                    f170 = f170.toString();
                }
                text += String.format("%s: %s %% ", name, f170);
            }

            return text;
        }
    }
}
