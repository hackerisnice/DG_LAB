package online.kbpf.dg_lab.client.screen.StrengthScreen;

import online.kbpf.dg_lab.client.Dg_labClient;
import online.kbpf.dg_lab.client.Config.StrengthConfig;
import online.kbpf.dg_lab.client.screen.ConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import static online.kbpf.dg_lab.client.screen.ConfigScreen.*;

@Environment(EnvType.CLIENT)
public class StrengthConfigScreen extends Screen {

    private SliderWidget ADamageStrength, BDamageStrength;
    private ButtonWidget DamageStrength;
    private SliderWidget ADelayTime, BDelayTime;
    private ButtonWidget DelayTime;
    private SliderWidget ADownTime, BDownTime;
    private ButtonWidget DownTime;
    private SliderWidget ADownValue, BDownValue;
    private ButtonWidget DownValue;
    private SliderWidget ADeathStrength, BDeathStrength;
    private ButtonWidget DeathStrength;
    private SliderWidget AMin, BMin;
    private ButtonWidget Min;

    public StrengthConfigScreen() {
        super(Text.literal("噪声惩罚强度配置"));
    }

    @Override
    public void close() {
        Screen configScreen = new ConfigScreen();
        client.setScreen(configScreen);
    }

    @Override
    protected void init() {
        StrengthConfig strengthConfig = Dg_labClient.strengthConfig;

        int row1 = 20;
        int row2 = ButtonHeight + ButtonDistance + 20;
        int row3 = 2 * (ButtonHeight + ButtonDistance) + 20;

        // ================= 第一行 左侧：基础惩罚强度 (原 DamageStrength) =================
        ADamageStrength = new SliderWidget(width / 2 - 205, row1, 100, ButtonHeight, Text.literal("A基础惩罚 " + String.format("%.2f", strengthConfig.getADamageStrength())), strengthConfig.getADamageStrength() / 20) {
            @Override
            protected void updateMessage() {}
            @Override
            protected void applyValue() {
                float val = (float) (this.value * 20);
                strengthConfig.setADamageStrength(val);
                this.setMessage(Text.literal("A基础惩罚 " + String.format("%.2f", strengthConfig.getADamageStrength())));
            }
        };

        BDamageStrength = new SliderWidget(width / 2 - 105, row1, 100, ButtonHeight, Text.literal("B基础惩罚 " + String.format("%.2f", strengthConfig.getBDamageStrength())), strengthConfig.getBDamageStrength() / 20) {
            @Override
            protected void updateMessage() {}
            @Override
            protected void applyValue() {
                float val = (float) (this.value * 20);
                strengthConfig.setBDamageStrength(val);
                this.setMessage(Text.literal("B基础惩罚 " + String.format("%.2f", strengthConfig.getBDamageStrength())));
            }
        };

        DamageStrength = ButtonWidget.builder(Text.literal("?"), button -> {}).dimensions(width / 2 - 215, row1, 10, ButtonHeight)
                .tooltip(Tooltip.of(Text.literal("【基础惩罚强度】\n当噪声达到200分贝时触发的最基础电击强度。"))).build();


        // ================= 第一行 右侧：超限放大倍率 (原 DeathStrength) =================
        ADeathStrength = new SliderWidget(width / 2 + 5, row1, 100, ButtonHeight, Text.literal("A超限倍率 " + strengthConfig.getADeathStrength()), (double) strengthConfig.getADeathStrength() / 200) {
            @Override
            protected void updateMessage() {}
            @Override
            protected void applyValue() {
                int tmp = (int) (this.value * 200);
                strengthConfig.setADeathStrength(tmp);
                this.setMessage(Text.literal("A超限倍率 " + tmp));
            }
        };

        BDeathStrength = new SliderWidget(width / 2 + 105, row1, 100, ButtonHeight, Text.literal("B超限倍率 " + strengthConfig.getBDeathStrength()), (double) strengthConfig.getBDeathStrength() / 200) {
            @Override
            protected void updateMessage() {}
            @Override
            protected void applyValue() {
                int tmp = (int) (this.value * 200);
                strengthConfig.setBDeathStrength(tmp);
                this.setMessage(Text.literal("B超限倍率 " + tmp));
            }
        };

        DeathStrength = ButtonWidget.builder(Text.literal("?"), button -> {}).dimensions(width / 2 + 205, row1, 10, ButtonHeight)
                .tooltip(Tooltip.of(Text.literal("【超限放大倍率】\n噪声超过200分贝时（例如极近距离大爆炸），\n超出的分贝数将乘以该倍率附加到基础强度上。"))).build();


        // ================= 第二行 左侧：电击波形维持时间 (原 DelayTime) =================
        ADelayTime = new SliderWidget(width / 2 - 205, row2, 100, ButtonHeight, Text.literal("A峰值维持 " + strengthConfig.getADelayTime() * 50 + "ms"), (double) strengthConfig.getADelayTime() / 120) {
            @Override
            protected void updateMessage() {}
            @Override
            protected void applyValue() {
                int tmp = (int) (this.value * 120);
                this.setMessage(Text.literal("A峰值维持 " + tmp * 50 + "ms"));
                strengthConfig.setADelayTime(tmp);
            }
        };

        BDelayTime = new SliderWidget(width / 2 - 105, row2, 100, ButtonHeight, Text.literal("B峰值维持 " + strengthConfig.getBDelayTime() * 50 + "ms"), (double) strengthConfig.getBDelayTime() / 120) {
            @Override
            protected void updateMessage() {}
            @Override
            protected void applyValue() {
                int tmp = (int) (this.value * 120);
                this.setMessage(Text.literal("B峰值维持 " + tmp * 50 + "ms"));
                strengthConfig.setBDelayTime(tmp);
            }
        };

        DelayTime = ButtonWidget.builder(Text.literal("?"), button -> {}).dimensions(width / 2 - 215, row2, 10, ButtonHeight)
                .tooltip(Tooltip.of(Text.literal("【电击峰值维持时间】\n触发惩罚后，最高电击强度将会持续的时间，\n倒计时结束后强度才会开始衰减。"))).build();


        // ================= 第二行 右侧：波形衰减间隔 (原 DownTime) =================
        ADownTime = new SliderWidget(width / 2 + 5, row2, 100, ButtonHeight, Text.literal("A衰减间隔 " + strengthConfig.getADownTime() * 50 + "ms"), (double) strengthConfig.getADownTime() / 120) {
            @Override
            protected void updateMessage() {}
            @Override
            protected void applyValue() {
                int tmp = (int) (this.value * 120);
                if (tmp == 0) tmp = 1;
                this.setMessage(Text.literal("A衰减间隔 " + tmp * 50 + "ms"));
                strengthConfig.setADownTime(tmp);
            }
        };

        BDownTime = new SliderWidget(width / 2 + 105, row2, 100, ButtonHeight, Text.literal("B衰减间隔 " + strengthConfig.getBDownTime() * 50 + "ms"), (double) strengthConfig.getBDownTime() / 120) {
            @Override
            protected void updateMessage() {}
            @Override
            protected void applyValue() {
                int tmp = (int) (this.value * 120);
                if (tmp == 0) tmp = 1;
                this.setMessage(Text.literal("B衰减间隔 " + tmp * 50 + "ms"));
                strengthConfig.setBDownTime(tmp);
            }
        };

        DownTime = ButtonWidget.builder(Text.literal("?"), button -> {}).dimensions(width / 2 + 205, row2, 10, ButtonHeight)
                .tooltip(Tooltip.of(Text.literal("【波形衰减间隔】\n峰值维持结束后，每隔多久降低一次电击强度。"))).build();


        // ================= 第三行 左侧：每次衰减数值 (原 DownValue) =================
        ADownValue = new SliderWidget(width / 2 - 205, row3, 100, ButtonHeight, Text.literal("A单次衰减 " + strengthConfig.getADownValue()), (double) strengthConfig.getADownValue() / 20) {
            @Override
            protected void updateMessage() {}
            @Override
            protected void applyValue() {
                int tmp = (int) (this.value * 20);
                this.setMessage(Text.literal("A单次衰减 " + tmp));
                strengthConfig.setADownValue(tmp);
            }
        };

        BDownValue = new SliderWidget(width / 2 - 105, row3, 100, ButtonHeight, Text.literal("B单次衰减 " + strengthConfig.getBDownValue()), (double) strengthConfig.getBDownValue() / 20) {
            @Override
            protected void updateMessage() {}
            @Override
            protected void applyValue() {
                int tmp = (int) (this.value * 20);
                this.setMessage(Text.literal("B单次衰减 " + tmp));
                strengthConfig.setBDownValue(tmp);
            }
        };

        DownValue = ButtonWidget.builder(Text.literal("?"), button -> {}).dimensions(width / 2 - 215, row3, 10, ButtonHeight)
                .tooltip(Tooltip.of(Text.literal("【单次衰减数值】\n每次触发衰减时，下降的电击强度数值。"))).build();


        // ================= 第三行 右侧：衰减下限保护 (原 Min) =================
        AMin = new SliderWidget(width / 2 + 5, row3, 100, ButtonHeight, Text.literal("A衰减下限 " + strengthConfig.getAMin()), (double) strengthConfig.getAMin() / 200) {
            @Override
            protected void updateMessage() {}
            @Override
            protected void applyValue() {
                int tmp = (int) (value * 200);
                setMessage(Text.literal("A衰减下限 " + tmp));
                strengthConfig.setAMin(tmp);
            }
        };

        BMin = new SliderWidget(width / 2 + 105, row3, 100, ButtonHeight, Text.literal("B衰减下限 " + strengthConfig.getBMin()), (double) strengthConfig.getBMin() / 200) {
            @Override
            protected void updateMessage() {}
            @Override
            protected void applyValue() {
                int tmp = (int) (value * 200);
                setMessage(Text.literal("B衰减下限 " + tmp));
                strengthConfig.setBMin(tmp);
            }
        };

        Min = ButtonWidget.builder(Text.literal("?"), button -> {}).dimensions(width / 2 + 205, row3, 10, ButtonHeight)
                .tooltip(Tooltip.of(Text.literal("【波形衰减下限】\n波形不断衰减时，将不会低于此数值，直至本次电击指令结束。"))).build();

        // 统一添加到屏幕组件列表
        addDrawableChild(ADamageStrength);
        addDrawableChild(BDamageStrength);
        addDrawable(DamageStrength);
        addDrawableChild(ADelayTime);
        addDrawableChild(BDelayTime);
        addDrawable(DelayTime);
        addDrawableChild(ADownTime);
        addDrawableChild(BDownTime);
        addDrawable(DownTime);
        addDrawableChild(ADownValue);
        addDrawableChild(BDownValue);
        addDrawable(DownValue);
        addDrawableChild(ADeathStrength);
        addDrawableChild(BDeathStrength);
        addDrawable(DeathStrength);
        addDrawableChild(AMin);
        addDrawableChild(BMin);
        addDrawable(Min);
    }
}
