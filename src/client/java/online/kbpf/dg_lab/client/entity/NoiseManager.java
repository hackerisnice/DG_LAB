package online.kbpf.dg_lab.client.entity;

import net.minecraft.client.MinecraftClient;
import online.kbpf.dg_lab.client.Dg_labClient;
import online.kbpf.dg_lab.client.Config.SoundNoiseConfig;
import online.kbpf.dg_lab.client.webSocketServer.webSocketServer;
import online.kbpf.dg_lab.client.Config.StrengthConfig;

public class NoiseManager {
    public static float currentNoise = 0.0f;
    public static int ticksSinceLastNoise = 0;
    public static final float MAX_NOISE = 200.0f;

    public static void onSoundPlayed(String soundId) {
        // 【核心修改1】：点名拉黑死亡音效！一旦检测到死亡音效，强制清空噪声条，并立刻终止计算。
        if (soundId.equals("minecraft:entity.player.death")) {
            currentNoise = 0.0f;
            ticksSinceLastNoise = 0;
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        
        // 【核心修改2】：退出游戏、尚未加载、或者已经死亡（躺地状态），锁死为0。
        if (client.player == null || client.player.isDead() || client.player.getHealth() <= 0) {
            currentNoise = 0.0f;
            return; 
        }

        int noiseValue = SoundNoiseConfig.getNoiseValue(soundId);
        if (noiseValue > 0) {
            currentNoise += noiseValue;
            ticksSinceLastNoise = 0; 
            
            if (currentNoise >= MAX_NOISE) {
                triggerShock();
            }
        }
    }

    public static void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (client.player == null || client.player.isDead() || client.player.getHealth() <= 0) {
            currentNoise = 0.0f;
            ticksSinceLastNoise = 0;
            return;
        }

        ticksSinceLastNoise++;
        // 50 tick = 2.5秒无声音开始慢慢回落
        if (ticksSinceLastNoise > 50 && currentNoise > 0) {
            currentNoise = Math.max(0, currentNoise - 0.5f); 
        }
    }

    private static void triggerShock() {
        float overshoot = Math.max(0, currentNoise - MAX_NOISE); 

        webSocketServer server = Dg_labClient.webSocketServer;
        StrengthConfig strengthConfig = Dg_labClient.strengthConfig;
        
        if (server != null && server.getConnected()) {
            server.setDelayTime(strengthConfig.getADelayTime(), strengthConfig.getBDelayTime());
            
            float baseA = strengthConfig.getADamageStrength() * 10;
            float baseB = strengthConfig.getBDamageStrength() * 10;
            
            int finalStrengthA = (int)(baseA + overshoot * strengthConfig.getADeathStrength());
            int finalStrengthB = (int)(baseB + overshoot * strengthConfig.getBDeathStrength());
            
            int maxA = server.getStrength().getAMaxStrength();
            int maxB = server.getStrength().getBMaxStrength();
            
            server.sendStrengthToClient(Math.min(Math.max(1, finalStrengthA), maxA), 1, 1);
            server.sendStrengthToClient(Math.min(Math.max(1, finalStrengthB), maxB), 1, 2);
        }
    }
}
