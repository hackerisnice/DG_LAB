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
        MinecraftClient client = MinecraftClient.getInstance();
        
        // 【核心修改 1】：如果玩家还没加载，或者玩家是死亡状态（躺在地上还没点复活），直接忽略所有声音并锁死在0！
        if (client.player == null || client.player.isDead() || client.player.getHealth() <= 0) {
            currentNoise = 0.0f;
            return; // 提前结束，不增加任何分贝
        }

        int noiseValue = SoundNoiseConfig.getNoiseValue(soundId);
        if (noiseValue > 0) {
            currentNoise += noiseValue;
            ticksSinceLastNoise = 0; 
            
            if (currentNoise >= MAX_NOISE) {
                triggerShock();
                currentNoise = 0.0f; 
            }
        }
    }

    public static void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        
        // 【核心修改 2】：哪怕没有新声音，在每Tick的循环里，只要是死亡状态，也一直保持清零
        if (client.player == null || client.player.isDead() || client.player.getHealth() <= 0) {
            currentNoise = 0.0f;
            ticksSinceLastNoise = 0;
            return;
        }

        ticksSinceLastNoise++;
        // 保持你之前要的 5秒 (100 tick) 后开始下降
        if (ticksSinceLastNoise > 100 && currentNoise > 0) {
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
