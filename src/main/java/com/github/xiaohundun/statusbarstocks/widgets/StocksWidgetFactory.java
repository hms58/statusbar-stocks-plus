package com.github.xiaohundun.statusbarstocks.widgets;

import com.github.xiaohundun.statusbarstocks.*;
import com.intellij.ide.ui.UISettings;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.wm.CustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import com.intellij.openapi.wm.impl.status.TextPanel;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StocksWidgetFactory implements StatusBarWidgetFactory {
    public static final String ID = "StocksPlusStatusBar";
    private static final ExecutorService pool = Executors.newFixedThreadPool(1);

    @Override
    public @NotNull @NonNls String getId() {
        return ID;
    }

    @Override
    public @NotNull @NlsContexts.ConfigurableName String getDisplayName() {
        return "Statusbar Stocks Plus";
    }

    @Override
    public boolean isAvailable(@NotNull Project project) {
        return true;
    }

    @Override
    public @NotNull StatusBarWidget createWidget(@NotNull Project project) {
        return new StockWidget(project);
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
        private boolean showingStock;
        // private final Icon stockIcon = AllIcons.Toolwindows.ToolWindowAnt;//
        // AllIcons.Plugins.Disabled / AllIcons.Toolwindows.ToolWindowAnalyzeDataflow
        private final Icon stockIconDefault = IconLoader.getIcon("/icons/toggle.png", getClass());
        private final Icon stockIconLow = IconLoader.getIcon("/icons/toggle_low.svg", getClass());
        private String lastCodeText = "";
        private List<Object[]> lastCodeDetailList = new ArrayList<>();

        public StockWidget(Project project) {
            new UiNotifyConnector(this, this);
            // 初始化显示状态
            showingStock = AppSettingsState.getInstance().showingStock;
            this.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    // 只响应右键
                    if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                        // 仅在只显示图标时响应（可选）
                        handlePopup(e);
                        return;
                    }

                    // 只响应左键单击
                    if (e.getButton() != java.awt.event.MouseEvent.BUTTON1) {
                        return;
                    }
                    showingStock = !showingStock;
                    // 保存设置
                    AppSettingsState.getInstance().showingStock = showingStock;

                    revalidate();
                    Container parent = getParent();
                    if (parent != null) {
                        parent.revalidate();
                        parent.doLayout();
                    }
                    repaint();

                    if (showingStock) {
                        // 立即刷新股票信息
                        updateState();
                    }
                }

                private void handlePopup(java.awt.event.MouseEvent e) {
                    // 判断是否右键且当前为只显示图标
                    if (/* e.isPopupTrigger() && */ !showingStock) {
                        JPopupMenu menu = new JPopupMenu();
                        JMenuItem configItem = new JMenuItem("打开设置");
                        configItem.addActionListener(ev -> {
                            ShowSettingsUtil.getInstance().showSettingsDialog(
                                    project,
                                    "Statusbar Stocks Plus" // 这里是你的Configurable显示名或类
                            );
                        });
                        menu.add(configItem);
                        menu.show(e.getComponent(), e.getX(), e.getY());
                        // 阻止事件继续传递（父状态栏）
                        e.consume();
                    }
                }
            });
        }

        @Override
        public void showNotify() {
            long refreshInterval = AppSettingsState.getInstance().refreshInterval;
            if (refreshInterval <= 0) {
                refreshInterval = 5;
            }
            myFuture = EdtExecutorService.getScheduledExecutorInstance().scheduleWithFixedDelay(
                    this::updateState, refreshInterval, refreshInterval, TimeUnit.SECONDS);
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
            if (!showingStock) {
                // 隐藏时不刷新股票信息
                return;
            }

            LocalTime now = LocalTime.now();
            DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();

            // 判断是否为工作日（周一到周五）
            boolean isWeekday = dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;

            LocalTime nine = LocalTime.of(9, 0);
            LocalTime three = LocalTime.of(16, 30);
            // 是否为交易时间
            boolean isTradeTime = isWeekday && now.isAfter(nine) && now.isBefore(three);

            boolean marketCloseVisible = AppSettingsState.getInstance().marketCloseVisible;
            if (isTradeTime || (marketCloseVisible && !init)) {
                // trigger repaint
                pool.execute(() -> setText(getCodeText()));
                init = true;
            } else if (!marketCloseVisible) {
                // 非工作时间隐藏组件
                String text = getText();
                if (text != null && !text.isEmpty()) {
                    pool.execute(() -> setText(""));
                    init = false;
                }
            }
        }

        public String getCodeText() {
            codeDetailList.clear();

            String code = AppSettingsState.getInstance().stockCode;
            boolean priceVisible = AppSettingsState.getInstance().priceVisible;
            boolean nameVisible = AppSettingsState.getInstance().nameVisible;
            boolean codeVisible = AppSettingsState.getInstance().codeVisible;
            boolean pinyinVisible = AppSettingsState.getInstance().pinyinVisible;
            boolean percentVisible = AppSettingsState.getInstance().percentVisible;
            // 替换全角逗号为英文逗号，并去除多余空格
            String[] codeList = code.replaceAll("，", ",").split(",");
            String text = "";
            String[] removeSuffixes = {
                    "-W", "ETF",
            };
            // 使用腾讯接口
            boolean useTencent = true;
            boolean fetchSuccess = false;

            if (codeList.length == 0) {
                return "";
            }
            if (useTencent) {
                List<JSONObject> list = TencentService.getDetail(codeList);
                if (list != null && !list.isEmpty()) {
                    for (JSONObject jsonObject : list) {
                        if (jsonObject == null) {
                            continue;
                        }
                        String prefix = "";
                        String name = jsonObject.getString("name");
                        String retCode = jsonObject.getString("code");
                        String percent = jsonObject.getString("percent");
                        String price = jsonObject.getString("price");
                        String yesterday = jsonObject.getString("yesterday");

                        if (nameVisible) {
                            prefix = String.format("%s: ", name);
                        } else if (codeVisible) {
                            if (retCode.length() > 3) {
                                prefix = String.format("%s: ", retCode.substring(retCode.length() - 3));
                            } else {
                                prefix = String.format("%s: ", retCode);
                            }
                        } else if (pinyinVisible) {
                            String pinyin = PinyinUtils.toFirstCharUpperCase(name);
                            pinyin = StringUtils.removeFirstSuffix(pinyin, removeSuffixes);
                            prefix = String.format("%s: ", pinyin);
                        }
                        text += prefix;

                        if (priceVisible) {
                            text += String.format("%s %s", price, percent);
                        } else {
                            text += String.format("%s", percent);
                        }

                        if (percentVisible) {
                            text += "% ";
                        } else {
                            text += " ";
                        }

                        var valueArray = new Object[4];
                        valueArray[0] = prefix;
                        valueArray[1] = percent;
                        valueArray[2] = new BigDecimal(price); // 字符串需符合数值格式
                        valueArray[3] = new BigDecimal(yesterday); // 昨天收盘价
                        codeDetailList.add(valueArray);
                    }

                    // 成功获取，更新缓存
                    lastCodeText = text;
                    lastCodeDetailList = new ArrayList<>(codeDetailList);
                    fetchSuccess = true;
                }
                if (!fetchSuccess) {
                    // 获取失败，返回上次缓存
                    codeDetailList.clear();
                    codeDetailList.addAll(lastCodeDetailList);
                    return lastCodeText;
                }
                return text;
            }
            for (String s : codeList) {
                JSONObject jsonObject = EastmoneyService.getDetail(s);
                if (jsonObject == null) {
                    continue;
                }
                JSONObject data = jsonObject.getJSONObject("data");
                String name = data.getString("f58");
                Object f170 = data.get("f170");
                BigDecimal f43 = data.getBigDecimal("f43"); // 当前最新价
                BigDecimal f60 = data.getBigDecimal("f60"); // 昨天收盘价
                String retCode = data.getString("f57");
                String prefix = "";
                if (f170 instanceof BigDecimal) {
                    f170 = f170.toString();
                }
                if (nameVisible) {
                    prefix = String.format("%s: ", name);
                } else if (codeVisible) {
                    if (retCode.length() > 3) {
                        prefix = String.format("%s: ", retCode.substring(retCode.length() - 3));
                    } else {
                        prefix = String.format("%s: ", retCode);
                    }
                } else if (pinyinVisible) {
                    String pinyin = PinyinUtils.toFirstCharUpperCase(name);
                    pinyin = StringUtils.removeFirstSuffix(pinyin, removeSuffixes);
                    prefix = String.format("%s: ", pinyin);
                }
                text += prefix;

                if (priceVisible) {
                    text += String.format("%s %s", f43, f170);
                } else {
                    text += String.format("%s", f170);
                }

                if (percentVisible) {
                    text += "% ";
                } else {
                    text += " ";
                }

                var valueArray = new Object[4];
                valueArray[0] = prefix;
                valueArray[1] = f170;
                valueArray[2] = f43;
                valueArray[3] = f60;
                codeDetailList.add(valueArray);

                fetchSuccess = true;
            }

            if (fetchSuccess) {
                lastCodeText = text;
                lastCodeDetailList = new ArrayList<>(codeDetailList);
                return text;
            } else {
                codeDetailList.clear();
                codeDetailList.addAll(lastCodeDetailList);
                return lastCodeText;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (!showingStock) {
                Icon stockIcon = getStockIcon();
                // 只画图标在中间
                int x = (getWidth() - stockIcon.getIconWidth()) / 2;
                int y = (getHeight() - stockIcon.getIconHeight()) / 2;
                stockIcon.paintIcon(this, g, x, y);
                return;
            }

            AppSettingsState appSettingsState = AppSettingsState.getInstance();

            if (appSettingsState.lowProfileMode) {
                super.paintComponent(g);
                return;
            }
            @Nls
            String s = getText();
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            if (s == null)
                return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setFont(getFont());
            UISettings.setupAntialiasing(g);

            Rectangle bounds = new Rectangle(panelWidth, panelHeight);
            FontMetrics fm = g.getFontMetrics();
            int x = getInsets().left;

            int y = UIUtil.getStringY(s, bounds, g2);

            Color foreground;
            foreground = JBUI.CurrentTheme.StatusBar.Widget.FOREGROUND;
            var suffix = " ";
            if (appSettingsState.percentVisible) {
                suffix = "% ";
            }

            for (Object[] values : codeDetailList) {
                var prefix = values[0].toString();
                var changeInPercentage = values[1];
                var zx = values[2]; // 最新价格
                var zs = values[3]; // 昨天收盘价

                g2.setColor(foreground);
                g2.drawString(prefix, x, y);

                x += fm.stringWidth(prefix);

                if (appSettingsState.priceVisible) {
                    // 若涨跌幅突出展示了，则价格不突出展示
                    if (appSettingsState.changePercentageVisible) {
                        g2.setColor(foreground);
                    } else {
                        int compareTo = ((BigDecimal) zx).compareTo(((BigDecimal) zs));
                        if (compareTo > 0) {
                            g2.setColor(JBColor.RED);
                        } else if (compareTo < 0) {
                            g2.setColor(JBColor.GREEN);
                        } else {
                            g2.setColor(foreground);
                        }
                    }
                    g2.drawString(zx + " ", x, y);
                    x += fm.stringWidth(zx + " ");
                }
                if (appSettingsState.changePercentageVisible) {
                    if (changeInPercentage.equals("-")) {
                        changeInPercentage = "0.0";
                    }
                    int compareTo = BigDecimal.valueOf(Double.parseDouble(((String) changeInPercentage)))
                            .compareTo(BigDecimal.ZERO);
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

        @Override
        public Dimension getPreferredSize() {
            if (!showingStock) {
                Icon stockIcon = getStockIcon();
                return new Dimension(stockIcon.getIconWidth() + 4, stockIcon.getIconHeight() + 4);
            }
            String text = getText();
            if (text == null || text.isEmpty()) {
                return new Dimension(0, super.getPreferredSize().height);
            }
            return super.getPreferredSize();
        }

        private Icon getStockIcon() {
            // 根据 lowProfileMode 设置 stockIcon
            return AppSettingsState.getInstance().lowProfileMode ? stockIconLow : stockIconDefault;
        }
    }
}
