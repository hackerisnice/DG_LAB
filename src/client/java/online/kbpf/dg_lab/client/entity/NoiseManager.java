package online.kbpf.dg_lab.client.entity;

import online.kbpf.dg_lab.client.Dg_labClient;
import online.kbpf.dg_lab.client.Config.SoundNoiseConfig;
import online.kbpf.dg_lab.client.webSocketServer.webSocketServer;
import online.kbpf.dg_lab.client.Config.StrengthConfig;

public class NoiseManager {
    public static float currentNoise = 0.0f;
    public static int ticksSinceLastNoise = 0;
    public static final float MAX_NOISE = 200.0f;

    public static void onSoundPlayed(String soundId) {
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
        ticksSinceLastNoise++;
        if (ticksSinceLastNoise > 100 && currentNoise > 0) {
            currentNoise = Math.max(0, currentNoise - 0.5f); 
        }
    }

    private static void triggerShock() {
        // 计算超过200的溢出值
        float overshoot = Math.max(0, currentNoise - MAX_NOISE); 

        webSocketServer server = Dg_labClient.webSocketServer;
        StrengthConfig strengthConfig = Dg_labClient.strengthConfig;
        
        if (server != null && server.getConnected()) {
            server.setDelayTime(strengthConfig.getADelayTime(), strengthConfig.getBDelayTime());
            
            float baseA = strengthConfig.getADamageStrength() * 10;
            float baseB = strengthConfig.getBDamageStrength() * 10;
            
            // 使用固定倍率：超出的分贝数 * 0.2 = 附加惩罚强度
            int finalStrengthA = (int)(baseA + overshoot * 0.2f);
            int finalStrengthB = (int)(baseB + overshoot * 0.2f);
            
            int maxA = server.getStrength().getAMaxStrength();
            int maxB = server.getStrength().getBMaxStrength();
            
            server.sendStrengthToClient(Math.min(Math.max(1, finalStrengthA), maxA), 1, 1);
            server.sendStrengthToClient(Math.min(Math.max(1, finalStrengthB), maxB), 1, 2);
        }
    }
}
