package com.dragonblocks.util.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import org.apache.logging.log4j.Level;

import com.dragonblocks.Main;
import com.dragonblocks.network.ICarpentersPacket;
import com.dragonblocks.network.PacketActivateBlock;
import com.dragonblocks.network.PacketEnrichPlant;
import com.dragonblocks.network.PacketSlopeSelect;
import com.dragonblocks.util.ModLogger;
import com.dragonblocks.util.Reference;




import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;

public class PacketHandler {

    private final static List<Class> packetCarrier;
    static {
        packetCarrier = new ArrayList<Class>();
        packetCarrier.add(PacketActivateBlock.class);
        packetCarrier.add(PacketEnrichPlant.class);
        packetCarrier.add(PacketSlopeSelect.class);
    }

    @SubscribeEvent
    public void onServerPacket(ServerCustomPacketEvent event) throws IOException
    {
        ByteBufInputStream bbis = new ByteBufInputStream(event.packet.payload());
        EntityPlayer entityPlayer = ((NetHandlerPlayServer) event.handler).player;
        int packetId = bbis.readInt();
        if (packetId < packetCarrier.size()) {
            try {
                ICarpentersPacket packetClass = (ICarpentersPacket) packetCarrier.get(packetId).newInstance();
                packetClass.processData(entityPlayer, bbis);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ModLogger.log(Level.WARN, "Encountered out of range packet Id: " + packetId);
        }
        bbis.close();
    }

    public static void sendPacketToServer(ICarpentersPacket packet)
    {
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeInt(packetCarrier.indexOf(packet.getClass()));

        try {
            packet.appendData(buffer);
        } catch (IOException e) { }

        Main.channel.sendToServer(new FMLProxyPacket(new CPacketCustomPayload()));
    }

}
