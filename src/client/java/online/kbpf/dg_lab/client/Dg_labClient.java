package online.kbpf.dg_lab.client;

import online.kbpf.dg_lab.client.Tool.DGWaveformTool;
import online.kbpf.dg_lab.client.command.Default;
import online.kbpf.dg_lab.client.Config.ModConfig;
import online.kbpf.dg_lab.client.Config.StrengthConfig;
import online.kbpf.dg_lab.client.Config.WaveformConfig;
import online.kbpf.dg_lab.client.entity.Waveform.Waveform;
import online.kbpf.dg_lab.client.screen.ConfigScreen;
import online.kbpf.dg_lab.client.webSocketServer.webSocketServer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.net.InetSocketAddress;
import java.util.Map;



public class Dg_labClient implements ClientModInitializer {

    public static webSocketServer webSocketServer = null;
    public static StrengthConfig strengthConfig = new StrengthConfig();
    public static final ModConfig modConfig = ModConfig.loadJson();
    public static Map<String, Waveform> waveformMap = WaveformConfig.LoadWaveform();
    public static boolean twoPlayerMode = false;
    public static String secondPlayer = "null";
    public static int secondPlayerQuitStrength = 200;

    private static KeyBinding keyBinding;
    private final Screen configScreen = new ConfigScreen();




    @Override
    public void onInitializeClient() {



        //注册连接的服务器
        webSocketServer = new webSocketServer(new InetSocketAddress(modConfig.getServerPort()));

        strengthConfig = online.kbpf.dg_lab.client.Config.StrengthConfig.loadJson();
        online.kbpf.dg_lab.client.Config.SoundNoiseConfig.loadOrGenerate();

        DGWaveformTool.updateDuration();

        HudRenderCallback.EVENT.register(this::onHudRender);

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "打开配置界面",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "DG_LAB"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            online.kbpf.dg_lab.client.entity.NoiseManager.tick();
            while (keyBinding.wasPressed()) {

                client.setScreen(configScreen);
            }
        });
        //指令定义
        Default.register(modConfig, strengthConfig, webSocketServer);

        if(modConfig.getAutoStartWebSocketServer()) webSocketServer.start();
    }



    //屏幕显示
    private void onHudRender(DrawContext drawContext, RenderTickCounter tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null && client.world != null) {
            
            // ================= 1. 左上角常驻强度文字 =================
            if (modConfig.getRenderingPositionX() < client.getWindow().getScaledWidth() || modConfig.getRenderingPositionY() < client.getWindow().getScaledHeight()) {
                int x = modConfig.getRenderingPositionX();
                int y = modConfig.getRenderingPositionY();

                Text strengthText;
                Text strengthText1;
                String A = "A", B = "B";
                if(twoPlayerMode){
                    A = client.getSession().getUsername() + ":";
                    B = secondPlayer + ":";
                }
                else {
                    A = "A:";
                    B = "B:";
                }
                
                if(modConfig.isRenderingMax()) {
                    strengthText = Text.literal(A + webSocketServer.getStrength().getAStrength() + ",Max:" + webSocketServer.getStrength().getAMaxStrength());
                    strengthText1 = Text.literal(B + webSocketServer.getStrength().getBStrength() + ",Max:" + webSocketServer.getStrength().getBMaxStrength());
                }
                else {
                    strengthText = Text.literal(A + webSocketServer.getStrength().getAStrength());
                    strengthText1 = Text.literal(B + webSocketServer.getStrength().getBStrength());
                }

                // 常驻渲染 A 和 B 的文字
                OrderedText orderedText = strengthText.asOrderedText();
                OrderedText orderedText1 = strengthText1.asOrderedText();
                drawContext.drawTextWithShadow(client.textRenderer, orderedText, x, y, 0xFFFFFF);
                drawContext.drawTextWithShadow(client.textRenderer, orderedText1, x, y + 9, 0xFFFFFF);

                // 如果没有连接，在第三行额外显示“未连接”
                if(!webSocketServer.getConnected()) {
                    Text disconnectText = Text.literal("未连接");
                    drawContext.drawTextWithShadow(client.textRenderer, disconnectText.asOrderedText(), x, y + 18, 0xFF0000); 
                }
            }

            // ================= 2. 右上角噪声条 =================
            int screenWidth = client.getWindow().getScaledWidth();
            int barWidth = 100;
            int barHeight = 10;
            int xPos = screenWidth - barWidth - 10; // 右上角边距10
            int yPos = 10;
            
            // 计算进度百分比
            float noiseRatio = Math.min(1.0f, online.kbpf.dg_lab.client.entity.NoiseManager.currentNoise / online.kbpf.dg_lab.client.entity.NoiseManager.MAX_NOISE);
            int currentBarWidth = (int)(barWidth * noiseRatio);
            
            // 绘制背景 (半透明黑)
            drawContext.fill(xPos, yPos, xPos + barWidth, yPos + barHeight, 0x80000000);
            
            // 绘制前景进度条 (噪声接近200时渐变为红色，否则为绿色)
            int barColor = noiseRatio > 0.8f ? 0xFFFF3333 : 0xFF33FF33;
            drawContext.fill(xPos, yPos, xPos + currentBarWidth, yPos + barHeight, barColor);
            
            // 绘制文字标尺
            String text = "Noise: " + (int)online.kbpf.dg_lab.client.entity.NoiseManager.currentNoise + "/200";
            drawContext.drawTextWithShadow(client.textRenderer, text, xPos, yPos + barHeight + 2, 0xFFFFFF);
        }
    }

}
