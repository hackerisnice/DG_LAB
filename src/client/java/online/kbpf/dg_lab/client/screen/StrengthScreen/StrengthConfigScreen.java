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
    private SliderWidget ADeathDelay, BDeathDelay;
    private ButtonWidget DeathDelay;
    private SliderWidget AMin, BMin;
    private ButtonWidget Min;

    public StrengthConfigScreen() {
        super(Text.literal("强度配置界面"));
    }

    @Override
    public void close() {
        Screen configScreen = new ConfigScreen();
        client.setScreen(configScreen);
    }

    @Override
    protected void init() {
        StrengthConfig strengthConfig = Dg_labClient.strengthConfig;

        // 重新计算7行组件的Y坐标
        int startY = 15;
        int gap = ButtonHeight + 4; // 紧凑型行距

        // ================= 第1行：噪声基础惩罚 =================
        ADamageStrength = new SliderWidget(width / 2 - 205, startY, 100, ButtonHeight, Text.literal("A噪声惩罚 " + String.format("%.2f", strengthConfig.getADamageStrength())), strengthConfig.getADamageStrength() / 20) {
            @Override protected void updateMessage() {}
            @Override protected void applyValue() {
                float val = (float) (this.value * 20);
                strengthConfig.setADamageStrength(val);
                this.setMessage(Text.literal("A噪声惩罚 " + String.format("%.2f", strengthConfig.getADamageStrength())));
            }
        };

        BDamageStrength = new SliderWidget(width / 2 - 105, startY, 100, ButtonHeight, Text.literal("B噪声惩罚 " + String.format("%.2f", strengthConfig.getBDamageStrength())), strengthConfig.getBDamageStrength() / 20) {
            @Override protected void updateMessage() {}
            @Override protected void applyValue() {
                float val = (float) (this.value * 20);
                strengthConfig.setBDamageStrength(val);
                this.setMessage(Text.literal("B噪声惩罚 " + String.format("%.2f", strengthConfig.getBDamageStrength())));
            }
        };

        DamageStrength = ButtonWidget.builder(Text.literal("?"), button -> {}).dimensions(width / 2 - 215, startY, 10, ButtonHeight)
                .tooltip(Tooltip.of(Text.literal("【噪声基础惩罚】\n当环境噪声累计达到200分贝时触发的基础电击强度。\n超出的分贝数会额外换算成强度附加。"))).build();

        // ================= 第2行：噪声峰值维持 =================
        ADelayTime = new SliderWidget(width / 2 - 205, startY + gap, 100, ButtonHeight, Text.literal("A峰值维持 " + strengthConfig.getADelayTime() * 50 + "ms"), (double) strengthConfig.getADelayTime() / 120) {
            @Override protected void updateMessage() {}
            @Override protected void applyValue() {
                int tmp = (int) (this.value * 120);
                this.setMessage(Text.literal("A峰值维持 " + tmp * 50 + "ms"));
                strengthConfig.setADelayTime(tmp);
            }
        };

        BDelayTime = new SliderWidget(width / 2 - 105, startY + gap, 100, ButtonHeight, Text.literal("B峰值维持 " + strengthConfig.getBDelayTime() * 50 + "ms"), (double) strengthConfig.getBDelayTime() / 120) {
            @Override protected void updateMessage() {}
            @Override protected void applyValue() {
                int tmp = (int) (this.value * 120);
                this.setMessage(Text.literal("B峰值维持 " + tmp * 50 + "ms"));
                strengthConfig.setBDelayTime(tmp);
            }
        };

        DelayTime = ButtonWidget.builder(Text.literal("?"), button -> {}).dimensions(width / 2 - 215, startY + gap, 10, ButtonHeight)
                .tooltip(Tooltip.of(Text.literal("【电击峰值维持】\n触发噪声惩罚后，最高电击强度将会持续的时间。"))).build();

        // ================= 第3行：波形衰减间隔 =================
        ADownTime = new SliderWidget(width / 2 - 205, startY + gap * 2, 100, ButtonHeight, Text.literal("A衰减间隔 " + strengthConfig.getADownTime() * 50 + "ms"), (double) strengthConfig.getADownTime() / 120) {
            @Override protected void updateMessage() {}
            @Override protected void applyValue() {
                int tmp = (int) (this.value * 120);
                if (tmp == 0) tmp = 1;
                this.setMessage(Text.literal("A衰减间隔 " + tmp * 50 + "ms"));
                strengthConfig.setADownTime(tmp);
            }
        };

        BDownTime = new SliderWidget(width / 2 - 105, startY + gap * 2, 100, ButtonHeight, Text.literal("B衰减间隔 " + strengthConfig.getBDownTime() * 50 + "ms"), (double) strengthConfig.getBDownTime() / 120) {
            @Override protected void updateMessage() {}
            @Override protected void applyValue() {
                int tmp = (int) (this.value * 120);
                if (tmp == 0) tmp = 1;
                this.setMessage(Text.literal("B衰减间隔 " + tmp * 50 + "ms"));
                strengthConfig.setBDownTime(tmp);
            }
        };

        DownTime = ButtonWidget.builder(Text.literal("?"), button -> {}).dimensions(width / 2 - 215, startY + gap * 2, 10, ButtonHeight)
                .tooltip(Tooltip.of(Text.literal("【波形衰减间隔】\n峰值维持结束后，每隔多久降低一次电击强度。"))).build();

        // ================= 第4行：波形单次衰减 =================
        ADownValue = new SliderWidget(width / 2 - 205, startY + gap * 3, 100, ButtonHeight, Text.literal("A单次衰减 " + strengthConfig.getADownValue()), (double) strengthConfig.getADownValue() / 20) {
            @Override protected void updateMessage() {}
            @Override protected void applyValue() {
                int tmp = (int) (this.value * 20);
                this.setMessage(Text.literal("A单次衰减 " + tmp));
                strengthConfig.setADownValue(tmp);
            }
        };

        BDownValue = new SliderWidget(width / 2 - 105, startY + gap * 3, 100, ButtonHeight, Text.literal("B单次衰减 " + strengthConfig.getBDownValue()), (double) strengthConfig.getBDownValue() / 20) {
            @Override protected void updateMessage() {}
            @Override protected void applyValue() {
                int tmp = (int) (this.value * 20);
                this.setMessage(Text.literal("B单次衰减 " + tmp));
                strengthConfig.setBDownValue(tmp);
            }
        };

        DownValue = ButtonWidget.builder(Text.literal("?"), button -> {}).dimensions(width / 2 - 215, startY + gap * 3, 10, ButtonHeight)
                .tooltip(Tooltip.of(Text.literal("【单次衰减数值】\n每次触发衰减时，下降的电击强度数值。"))).build();

        // ================= 第5行：死亡附加惩罚 =================
        ADeathStrength = new SliderWidget(width / 2 + 5, startY, 100, ButtonHeight, Text.literal("A死亡惩罚 " + strengthConfig.getADeathStrength()), (double) strengthConfig.getADeathStrength() / 200) {
            @Override protected void updateMessage() {}
            @Override protected void applyValue() {
                int tmp = (int) (this.value * 200);
                strengthConfig.setADeathStrength(tmp);
                this.setMessage(Text.literal("A死亡惩罚 " + tmp));
            }
        };

        BDeathStrength = new SliderWidget(width / 2 + 105, startY, 100, ButtonHeight, Text.literal("B死亡惩罚 " + strengthConfig.getBDeathStrength()), (double) strengthConfig.getBDeathStrength() / 200) {
            @Override protected void updateMessage() {}
            @Override protected void applyValue() {
                int tmp = (int) (this.value * 200);
                strengthConfig.setBDeathStrength(tmp);
                this.setMessage(Text.literal("B死亡惩罚 " + tmp));
            }
        };

        DeathStrength = ButtonWidget.builder(Text.literal("?"), button -> {}).dimensions(width / 2 + 205, startY, 10, ButtonHeight)
                .tooltip(Tooltip.of(Text.literal("【死亡附加惩罚】\n玩家死亡时触发的巨量额外惩罚，无视噪声条直接生效。"))).build();

        // ================= 第6行：死亡峰值维持 =================
        ADeathDelay = new SliderWidget(width / 2 + 5, startY + gap, 100, ButtonHeight, Text.literal("A死后维持 " + strengthConfig.getADeathDelay() * 50 + "ms"), (double) strengthConfig.getADeathDelay() / 120) {
            @Override protected void updateMessage() {}
            @Override protected void applyValue() {
                int tmp = (int) (this.value * 120);
                this.setMessage(Text.literal("A死后维持 " + tmp * 50 + "ms"));
                strengthConfig.setADeathDelay(tmp);
            }
        };

        BDeathDelay = new SliderWidget(width / 2 + 105, startY + gap, 100, ButtonHeight, Text.literal("B死后维持 " + strengthConfig.getBDeathDelay() * 50 + "ms"), (double) strengthConfig.getBDeathDelay() / 120) {
            @Override protected void updateMessage() {}
            @Override protected void applyValue() {
                int tmp = (int) (this.value * 120);
                this.setMessage(Text.literal("B死后维持 " + tmp * 50 + "ms"));
                strengthConfig.setBDeathDelay(tmp);
            }
        };

        DeathDelay = ButtonWidget.builder(Text.literal("?"), button -> {}).dimensions(width / 2 + 205, startY + gap, 10, ButtonHeight)
                .tooltip(Tooltip.of(Text.literal("【死亡峰值维持】\n死亡电击最高强度的持续时间，时间结束后才开始衰减。"))).build();

        // ================= 第7行：衰减下限保护 =================
        AMin = new SliderWidget(width / 2 + 5, startY + gap * 2, 100, ButtonHeight, Text.literal("A衰减下限 " + strengthConfig.getAMin()), (double) strengthConfig.getAMin() / 200) {
            @Override protected void updateMessage() {}
            @Override protected void applyValue() {
                int tmp = (int) (value * 200);
                setMessage(Text.literal("A衰减下限 " + tmp));
                strengthConfig.setAMin(tmp);
            }
        };

        BMin = new SliderWidget(width / 2 + 105, startY + gap * 2, 100, ButtonHeight, Text.literal("B衰减下限 " + strengthConfig.getBMin()), (double) strengthConfig.getBMin() / 200) {
            @Override protected void updateMessage() {}
            @Override protected void applyValue() {
                int tmp = (int) (value * 200);
                setMessage(Text.literal("B衰减下限 " + tmp));
                strengthConfig.setBMin(tmp);
            }
        };

        Min = ButtonWidget.builder(Text.literal("?"), button -> {}).dimensions(width / 2 + 205, startY + gap * 2, 10, ButtonHeight)
                .tooltip(Tooltip.of(Text.literal("【波形衰减下限】\n波形不断衰减时，将不会低于此数值，受血量比例影响。"))).build();


        // 将所有组件添加到屏幕
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
        addDrawableChild(ADeathDelay);
        addDrawableChild(BDeathDelay);
        addDrawable(DeathDelay);
        addDrawableChild(AMin);
        addDrawableChild(BMin);
        addDrawable(Min);
    }
}
