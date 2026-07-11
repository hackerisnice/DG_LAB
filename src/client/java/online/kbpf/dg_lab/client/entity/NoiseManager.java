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
            ticksSinceLastNoise = 0; // 有新声音，重置静音计时
            
            if (currentNoise >= MAX_NOISE) {
                triggerShock();
            }
        }
    }

    public static void tick() {
        ticksSinceLastNoise++;
        // 只要当前没有新的声音 (ticksSinceLastNoise > 0)，就立刻开始降噪
        if (ticksSinceLastNoise > 0 && currentNoise > 0) {
            // 每 tick 扣减 0.5（即每秒降低 10 分贝），可按需修改
            currentNoise = Math.max(0, currentNoise - 0.5f); 
        }
    }

    private static void triggerShock() {
        // 计算超过200的溢出值（例如 250 - 200 = 50）
        float overshoot = Math.max(0, currentNoise - MAX_NOISE); 

        webSocketServer server = Dg_labClient.webSocketServer;
        StrengthConfig strengthConfig = Dg_labClient.strengthConfig;
        
        if (server != null && server.getConnected()) {
            // 设置电击波形的持续时间
            server.setDelayTime(strengthConfig.getADelayTime(), strengthConfig.getBDelayTime());
            
            // 为了不破坏原有的 JSON 存档结构，我们"借用"原本的配置字段：
            // ADamageStrength 作为 基础电击强度
            // ADeathStrength 作为 溢出倍率
            float baseA = strengthConfig.getADamageStrength() * 10; // 面板默认0-50，乘10放大
            float baseB = strengthConfig.getBDamageStrength() * 10;
            
            float multA = strengthConfig.getADeathStrength(); 
            float multB = strengthConfig.getBDeathStrength();
            
            // 最终强度 = 基础强度 + (溢出值 * 倍率)
            int finalStrengthA = (int)(baseA + overshoot * multA);
            int finalStrengthB = (int)(baseB + overshoot * multB);
            
            // 获取最高安全限制
            int maxA = server.getStrength().getAMaxStrength();
            int maxB = server.getStrength().getBMaxStrength();
            
            // 发送强度（确保不低于1，且不超过最高安全限制）
            server.sendStrengthToClient(Math.min(Math.max(1, finalStrengthA), maxA), 1, 1);
            server.sendStrengthToClient(Math.min(Math.max(1, finalStrengthB), maxB), 1, 2);
            Thread.sleep(500);
        }
    }
}
