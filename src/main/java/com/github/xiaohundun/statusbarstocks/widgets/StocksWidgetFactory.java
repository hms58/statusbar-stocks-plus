package com.github.xiaohundun.statusbarstocks.widgets;

import com.github.xiaohundun.statusbarstocks.AppSettingsState;
import com.github.xiaohundun.statusbarstocks.EastmoneyService;
import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.wm.CustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import com.intellij.openapi.wm.impl.status.TextPanel;
import com.intellij.ui.ExperimentalUI;
import com.intellij.ui.JBColor;
import com.intellij.util.concurrency.EdtExecutorService;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.update.Activatable;
import com.intellij.util.ui.update.UiNotifyConnector;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StocksWidgetFactory implements StatusBarWidgetFactory {
    public static final String ID = "StocksStatusBar";
    private static final ExecutorService pool = Executors.newFixedThreadPool(1);

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
        private final ArrayList<Object[]> codeDetailList = new ArrayList<>();
        private boolean init = false;
        private java.util.concurrent.ScheduledFuture<?> myFuture;

        public StockWidget() {
            new UiNotifyConnector(this, this);
        }

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

        public void updateState() {
            LocalTime now   = LocalTime.now();
            LocalTime nine  = LocalTime.of(9, 0);
            LocalTime three = LocalTime.of(15, 0);
            if ((now.isAfter(nine) && now.isBefore(three)) | !init) {
                // trigger repaint
                pool.execute(() -> setText(getCodeText()));
                init = true;
            }
        }

        public String getCodeText() {
            codeDetailList.clear();

            String   code         = AppSettingsState.getInstance().stockCode;
            boolean  priceVisible = AppSettingsState.getInstance().priceVisible;
            String[] codeList     = code.replaceAll("，", ",").split(",");
            String   text         = "";
            for (String s : codeList) {
                JSONObject jsonObject = EastmoneyService.getDetail(s);
                if (jsonObject == null) {
                    continue;
                }
                JSONObject data = jsonObject.getJSONObject("data");
                String     name = data.getString("f58");
                Object     f170 = data.get("f170");
                BigDecimal f43  = data.getBigDecimal("f43");
                BigDecimal f60  = data.getBigDecimal("f60");
                if (f170 instanceof BigDecimal) {
                    f170 = f170.toString();
                }
                if (priceVisible) {
                    text += String.format("%s: %s %s %%", name, f43, f170);
                } else {
                    text += String.format("%s: %s %%", name, f170);
                }

                var valueArray = new Object[4];
                valueArray[0] = name;
                valueArray[1] = f170;
                valueArray[2] = f43;
                valueArray[3] = f60;
                codeDetailList.add(valueArray);
            }

            return text;
        }

        @Override
        protected void paintComponent(Graphics g) {
            AppSettingsState appSettingsState = AppSettingsState.getInstance();

            @Nls String s           = getText();
            int         panelWidth  = getWidth();
            int         panelHeight = getHeight();
            if (s == null) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setFont(getFont());
            UISettings.setupAntialiasing(g);

            Rectangle   bounds = new Rectangle(panelWidth, panelHeight);
            FontMetrics fm     = g.getFontMetrics();
            int         x      = getInsets().left;

            int y = UIUtil.getStringY(s, bounds, g2);

            Color foreground;
            foreground = JBUI.CurrentTheme.StatusBar.Widget.FOREGROUND;

            for (Object[] values : codeDetailList) {
                var prefix             = values[0] + ": ";
                var changeInPercentage = values[1];
                var zx                 = values[2];
                var zs                 = values[3];
                var suffix             = "% ";
                g2.setColor(foreground);
                g2.drawString(prefix, x, y);
                x += fm.stringWidth(prefix);

                if (appSettingsState.priceVisible) {
                    if (((BigDecimal) zx).compareTo(((BigDecimal) zs)) >= 0) {
                        g2.setColor(JBColor.RED);
                    } else {
                        g2.setColor(JBColor.GREEN);
                    }
                    g2.drawString(zx + " ", x, y);
                    x += fm.stringWidth(zx + " ");
                }
                if (appSettingsState.changePercentageVisible) {
                    if (changeInPercentage.equals("-")) {
                        changeInPercentage = "0.0";
                    }
                    int compareTo = BigDecimal.valueOf(Double.parseDouble(((String) changeInPercentage))).compareTo(BigDecimal.ZERO);
                    if (compareTo > 0) {
                        g2.setColor(JBColor.RED);
                    } else if (compareTo < 0) {
                        g2.setColor(JBColor.GREEN);
                    } else {
                        g2.setColor(foreground);
                    }
                    g2.drawString(changeInPercentage + suffix, x, y);
                    x += fm.stringWidth(changeInPercentage + suffix);
                }
            }
        }
    }
}
