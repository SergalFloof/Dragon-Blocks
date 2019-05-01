package com.dragonblocks.util.handler;

import java.lang.reflect.Field;
import org.apache.logging.log4j.Level;
import com.dragonblocks.util.ModLogger;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class ShadersHandler {

    private static Class<?> ShadersClass;
    private static Field oldLightingField;
    public static boolean enableShadersModCoreIntegration = false;
    public static boolean oldLighting = true;

    public static void init()
    {
        try {
            ShadersClass = Class.forName("shadersmodcore.client.Shaders");
            oldLightingField = ShadersClass.getDeclaredField("configOldLighting");
            ModLogger.log(Level.INFO, "ShadersModCore integration successful.");
            enableShadersModCoreIntegration = true;
        } catch (Exception e) {}
    }

    /**
     * Updates fields based on ShadersModCore configuration.
     */
    public static void update()
    {
        try {
            oldLighting = oldLightingField.getBoolean(null);
        } catch (Exception e) {
            ModLogger.log(Level.WARN, "ShadersModCore integration failed: " + e.getMessage());
            enableShadersModCoreIntegration = false;
            oldLighting = true;
        }
    }

}
