package com.github.xiaohundun.statusbarstocks.widgets;

import com.github.xiaohundun.statusbarstocks.AppSettingsState;
import com.github.xiaohundun.statusbarstocks.EastmoneyService;
import com.github.xiaohundun.statusbarstocks.TencentService;
import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
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
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
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
            long refreshInterval = AppSettingsState.getInstance().refreshInterval;
            if (refreshInterval <= 0){
                refreshInterval = 5;
            }
            myFuture = EdtExecutorService.getScheduledExecutorInstance().scheduleWithFixedDelay(
                    this::updateState, refreshInterval, refreshInterval, TimeUnit.SECONDS
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
            DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();

            // 判断是否为工作日（周一到周五）
            boolean isWeekday = dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;

            LocalTime nine  = LocalTime.of(9, 0);
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

            String   code         = AppSettingsState.getInstance().stockCode;
            boolean  priceVisible = AppSettingsState.getInstance().priceVisible;
            boolean  nameVisible = AppSettingsState.getInstance().nameVisible;
            boolean  codeVisible = AppSettingsState.getInstance().codeVisible;
            boolean  percentVisible = AppSettingsState.getInstance().percentVisible;
            String[] codeList     = code.replaceAll("，", ",").split(",");
            String   text         = "";

            // 使用腾讯接口
            boolean useTencent = true;
            if (useTencent) {
                List<JSONObject> list =  TencentService.getDetail(codeList);
                if (list != null) {
                    for (JSONObject jsonObject : list) {
                        if (jsonObject == null) {
                            continue;
                        }
                        String prefix = "";
                        String name   = jsonObject.getString("name");
                        String namePy = getFirstLetters(name);
                        String retCode   = jsonObject.getString("code");
                        String percent   = jsonObject.getString("percent");
                        String price     = jsonObject.getString("price");
                        String yesterday = jsonObject.getString("yesterday");

                        if (nameVisible) {
                            prefix = String.format("%s: ", name);
                        }
                        if (codeVisible) {
                            if (retCode.length()>3) {
                                prefix = String.format("%s: ", retCode.substring(retCode.length()-3));
                            } else {
                                prefix = String.format("%s: ", retCode);
                            }
                        }
                        if (AppSettingsState.getInstance().pyMode) {
                            prefix = String.format("%s: ", namePy);
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
                        valueArray[2] = new BigDecimal(price);  // 字符串需符合数值格式
                        valueArray[3] = new BigDecimal(yesterday); // 昨天收盘价
                        codeDetailList.add(valueArray);
                    }
                }
                return text;
            }
            for (String s : codeList) {
                JSONObject jsonObject = EastmoneyService.getDetail(s);
                if (jsonObject == null) {
                    continue;
                }
                JSONObject data = jsonObject.getJSONObject("data");
                String     name = data.getString("f58");
                Object     f170 = data.get("f170");
                BigDecimal f43  = data.getBigDecimal("f43"); // 当前最新价
                BigDecimal f60  = data.getBigDecimal("f60"); // 昨天收盘价
                String retCode  = data.getString("f57");
                String   prefix = "";
                if (f170 instanceof BigDecimal) {
                    f170 = f170.toString();
                }
                if (nameVisible) {
                    prefix = String.format("%s: ", name);
                } else if (codeVisible) {
                    if (retCode.length()>3) {
                        prefix = String.format("%s: ", retCode.substring(retCode.length()-3));
                    } else {
                        prefix = String.format("%s: ", retCode);
                    }
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
            }

            return text;
        }

        @Override
        protected void paintComponent(Graphics g) {
            AppSettingsState appSettingsState = AppSettingsState.getInstance();

            if (appSettingsState.lowProfileMode) {
                super.paintComponent(g);
                return;
            }
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
            var suffix             = " ";
            if (appSettingsState.percentVisible) {
                suffix = "% ";
            }

            for (Object[] values : codeDetailList) {
                var prefix             = values[0].toString();
                var changeInPercentage = values[1];
                var zx                 = values[2]; // 最新价格
                var zs                 = values[3]; // 昨天收盘价

                g2.setColor(foreground);
                g2.drawString(prefix, x, y);

                x += fm.stringWidth(prefix);

                if (appSettingsState.priceVisible) {
                    // 若涨跌幅突出展示了，则价格不突出展示
                    if (appSettingsState.changePercentageVisible) {
                        g2.setColor(foreground);
                    } else {
                        int compareTo =((BigDecimal) zx).compareTo(((BigDecimal) zs));
                        if (compareTo > 0 && appSettingsState.coloredFont) {
                            g2.setColor(JBColor.RED);
                        } else if (compareTo < 0 && appSettingsState.coloredFont){
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
                    int compareTo = BigDecimal.valueOf(Double.parseDouble(((String) changeInPercentage))).compareTo(BigDecimal.ZERO);
                    if (compareTo > 0 && appSettingsState.coloredFont) {
                        g2.setColor(JBColor.RED);
                    } else if (compareTo < 0 && appSettingsState.coloredFont) {
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
            String text = getText();
            if (text == null || text.isEmpty()) {
                return new Dimension(0, super.getPreferredSize().height);
            }
            return super.getPreferredSize();
        }
        /**
         * 获取字符串中每个汉字的拼音首字母组成的字符串
         * 例如："上证指数" -> "SZZS"
         */
        public   String getFirstLetters(String input) {
            if (input == null || input.isEmpty()) {
                return "";
            }

            StringBuilder result = new StringBuilder();
            HanyuPinyinOutputFormat format = createPinyinFormat();

            for (char c : input.toCharArray()) {
                // 判断是否为中文字符
                if (isChineseCharacter(c)) {
                    try {
                        // 获取拼音数组（可能有多个读音，取第一个）
                        String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
                        if (pinyinArray != null && pinyinArray.length > 0) {
                            result.append(pinyinArray[0].charAt(0)); // 取拼音首字母
                        }
                    } catch (BadHanyuPinyinOutputFormatCombination e) {
                        e.printStackTrace();
                    }
                } else {
                    // 非中文字符直接添加
                    result.append(Character.toUpperCase(c));
                }
            }

            return result.toString();
        }

        /**
         * 创建拼音输出格式配置
         */
        private   HanyuPinyinOutputFormat createPinyinFormat() {
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.UPPERCASE); // 大写
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE); // 无音调
            format.setVCharType(HanyuPinyinVCharType.WITH_V); // ü用v表示
            return format;
        }

        /**
         * 判断字符是否为中文字符
         */
        private   boolean isChineseCharacter(char c) {
            // 中文字符范围：0x4E00 - 0x9FA5
            return c >= 0x4E00 && c <= 0x9FA5;
        }
    }
}
