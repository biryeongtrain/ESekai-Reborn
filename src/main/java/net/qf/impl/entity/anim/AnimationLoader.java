package net.qf.impl.entity.anim;

import com.google.gson.*;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.AffineTransformation;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Matrix4f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.IntFunction;

public class AnimationLoader {
    private static final Set<String> MOD_CONTAINER = new HashSet<>();
        private static final Object2ObjectOpenHashMap<String, Object2ObjectOpenHashMap<String, AnimationContainer>> ANIMATIONS = new Object2ObjectOpenHashMap<>();
    @SuppressWarnings("FieldCanBeLocal")
    private static boolean isBuilt = false;
    public static String DEFAULT_POSE_KEY = "default_pose";
    @SuppressWarnings("UnusedReturnValue")
    public static boolean registerMod(String modID) {
        return MOD_CONTAINER.add(modID);
    }

    public static void build() {
        isBuilt = true;
        var instance = FabricLoader.getInstance();
        for (String MOD_ID : MOD_CONTAINER) {
            var mod = instance.getModContainer(MOD_ID);
            if (mod.isPresent()) {
                var container = mod.get();
                var paths = container.getRootPaths();
                try {
                    for (Path path : paths) {
                        var animPath = path.resolve("anim");
                        if (Files.exists(animPath)) {
                            Files.walk(animPath).forEach(file -> {
                                var relative = animPath.relativize(file);
                                if (Files.isRegularFile(file)) {
                                    try {
                                        getInfo(file.getFileName().toString().replace(".json", ""), Files.readAllBytes(file));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void getInfo(String id, byte[] bytes) {
        Object2ObjectOpenHashMap<String, AnimationContainer> containers = new Object2ObjectOpenHashMap<>();
        String data = new String(bytes);
        JsonObject jsonObject = (JsonObject) JsonParser.parseString(data);

        JsonObject rigs = jsonObject.getAsJsonObject("rig");
        JsonObject nodeMap = rigs.getAsJsonObject("node_map");
        JsonArray defaultPose = rigs.getAsJsonArray("default_pose");
        JsonObject animationsRoot = jsonObject.getAsJsonObject("animations");
        Object2ObjectArrayMap<UUID, String> uuidToNameMap = getUuidToNameMap(nodeMap);
        Object2ObjectOpenHashMap<String, Int2ObjectMap<AffineTransformation>> defaultPoseMap = new Object2ObjectOpenHashMap<>();

        defaultPose.forEach(element -> {
            Int2ObjectMap<AffineTransformation> defaultPosePartMap = new Int2ObjectOpenHashMap<>();
            JsonObject object = element.getAsJsonObject();
            UUID modelUuid = UUID.fromString(object.get("uuid").getAsString());
            var matrix = element.getAsJsonObject().getAsJsonArray("matrix");

            var posePartMatrix = matrix.asList().stream().map(JsonElement::getAsFloat).toList();
            var array = posePartMatrix.toArray(new Float[posePartMatrix.size()]);

            String name = uuidToNameMap.get(modelUuid);
            defaultPosePartMap.put(0, new AffineTransformation(new Matrix4f().set(ArrayUtils.toPrimitive(array))));

            defaultPoseMap.put(name, defaultPosePartMap);
        });

        containers.put(DEFAULT_POSE_KEY, new AnimationContainer(defaultPoseMap, 0));

        animationsRoot.entrySet().forEach(entry -> {
            String name = entry.getKey();
            Object2ObjectOpenHashMap<String, Int2ObjectMap<AffineTransformation>> poseMap = new Object2ObjectOpenHashMap<>();
            var frames = entry.getValue().getAsJsonObject().getAsJsonArray("frames");
            int durationTick = entry.getValue().getAsJsonObject().get("duration").getAsInt() - 1;
            frames.forEach(element -> {
                JsonArray nodes = element.getAsJsonObject().getAsJsonArray("nodes");
                int tick = (int) (element.getAsJsonObject().get("time").getAsFloat() * 20);

                nodes.forEach(node -> {
                    JsonObject nodeObject = node.getAsJsonObject();
                    var nodeName = uuidToNameMap.get(UUID.fromString(nodeObject.get("uuid").getAsString()));

                    Matrix4f matrix = getMatrixViaJsonArray(nodeObject.getAsJsonArray("matrix"));
                    Int2ObjectMap<AffineTransformation> frameMap = new Int2ObjectOpenHashMap<>();
                    frameMap.put(tick, new AffineTransformation(matrix));
                    poseMap.merge(nodeName, frameMap, (map1, map2) -> {
                        map1.putAll(map2);
                        return map1;
                    });
                });

                containers.put(name, new AnimationContainer(poseMap, durationTick));
            });
        });

        ANIMATIONS.put(id, containers);
    }

    private static Object2ObjectArrayMap<UUID, String> getUuidToNameMap(JsonObject nodeMap) {
        Object2ObjectArrayMap<UUID, String> uuidToNameMap = new Object2ObjectArrayMap<>();
        nodeMap.entrySet().forEach(entry -> {
            UUID uuid = UUID.fromString(entry.getKey());
            String name = entry.getValue().getAsJsonObject().get("name").getAsString();
            uuidToNameMap.put(uuid, name);
        });

        return uuidToNameMap;
    }

    private static Matrix4f getMatrixViaJsonArray(JsonArray array) {
        var posePartMatrix = array.asList().stream().map(JsonElement::getAsFloat).toList();
        var floatArray = posePartMatrix.toArray(new Float[posePartMatrix.size()]);

        return new Matrix4f().set(ArrayUtils.toPrimitive(floatArray));
    }

    public static Object2ObjectOpenHashMap<String, AnimationContainer> getContainer(String s) {
        return ANIMATIONS.get(s);
    }

}