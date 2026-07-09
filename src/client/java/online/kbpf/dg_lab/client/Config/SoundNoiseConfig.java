package online.kbpf.dg_lab.client.Config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SoundNoiseConfig {
    private static Map<String, Integer> soundMap = new HashMap<>();
    private static final File CONFIG_FILE = new File("config/dg-lab/SoundNoiseConfig.json");

    public static void loadOrGenerate() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (CONFIG_FILE.exists()) {
            try (Reader reader = new FileReader(CONFIG_FILE)) {
                Type type = new TypeToken<Map<String, Integer>>(){}.getType();
                soundMap = gson.fromJson(reader, type);
                if (soundMap == null) soundMap = new HashMap<>();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 文件不存在时，自动遍历 1.21 所有声音列表，生成空的默认JSON
            CONFIG_FILE.getParentFile().mkdirs();
            for (Identifier id : Registries.SOUND_EVENT.getIds()) {
                soundMap.put(id.toString(), 0); // 默认所有声音分贝为0 (留空)
            }
            // 预设玩家要求的值
            soundMap.put("minecraft:entity.player.hurt", 5);
            soundMap.put("minecraft:entity.generic.explode", 10);
            soundMap.put("minecraft:entity.creeper.primed", 10);

            try (Writer writer = new FileWriter(CONFIG_FILE)) {
                gson.toJson(soundMap, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getNoiseValue(String soundId) {
        return soundMap.getOrDefault(soundId, 0);
    }
}
