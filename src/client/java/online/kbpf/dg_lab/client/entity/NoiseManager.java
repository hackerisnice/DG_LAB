package online.kbpf.dg_lab.client.entity;

import online.kbpf.dg_lab.client.Dg_labClient;
import online.kbpf.dg_lab.client.Config.SoundNoiseConfig;
import online.kbpf.dg_lab.client.webSocketServer.webSocketServer;
import online.kbpf.dg_lab.client.Config.StrengthConfig;

public class NoiseManager {
    public static float currentNoise = 0.0f;
    public static int ticksSinceLastNoise = 0;
    public static final float MAX_NOISE = 200.0f;

    // 当播放声音时被拦截调用
    public static void onSoundPlayed(String soundId) {
        int noiseValue = SoundNoiseConfig.getNoiseValue(soundId);
        if (noiseValue > 0) {
            currentNoise += noiseValue;
            ticksSinceLastNoise = 0; // 有新声音时，重置回落倒计时
            
            // 如果分贝满 200，触发电击
            if (currentNoise >= MAX_NOISE) {
                triggerShock();
                currentNoise -= MAX_NOISE; // 扣除200分贝 (或者设为0，视你需求而定)
            }
        }
    }

    // 每 tick 运行一次 (1秒=20tick)
    public static void tick() {
        ticksSinceLastNoise++;
        // 30秒 = 600 tick。超过30秒没声音，开始缓慢回落
        if (ticksSinceLastNoise >= 600 && currentNoise > 0) {
            currentNoise = Math.max(0, currentNoise - 0.5f); // 每tick回落0.5分贝，可微调
        }
    }

    private static void triggerShock() {
        webSocketServer server = Dg_labClient.webSocketServer;
        StrengthConfig strengthConfig = Dg_labClient.strengthConfig;
        if (server != null && server.getConnected()) {
            server.setDelayTime(strengthConfig.getADelayTime(), strengthConfig.getBDelayTime());
            
            // 每次满200分贝时发送的强度 (你可以根据需要自己调整发送的基础强度，这里假设为设置中的伤害乘数)
            if (strengthConfig.getADamageStrength() > 0)
                server.sendStrengthToClient(Math.max(1, (int)(10 * strengthConfig.getADamageStrength())), 1, 1);
            if (strengthConfig.getBDamageStrength() > 0)
                server.sendStrengthToClient(Math.max(1, (int)(10 * strengthConfig.getBDamageStrength())), 1, 2);
        }
    }
}
