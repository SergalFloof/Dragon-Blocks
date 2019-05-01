package com.dragonblocks.network;

import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.dragonblocks.tileentity.TEBase;
import com.dragonblocks.util.EntityLivingUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

public class PacketEnrichPlant extends TilePacket {

    private int hexColor;

    public PacketEnrichPlant() {}

    /**
     * For the server to examine plant color, since it's a client-side only property.
     */
    public PacketEnrichPlant(int x, int y, int z, int hexColor)
    {
        super(x, y, z);
        this.hexColor = hexColor;
    }

    @Override
    public void processData(EntityPlayer entityPlayer, ByteBufInputStream bbis) throws IOException
    {
    	BlockPos pos = new BlockPos(x, y, z);
        super.processData(entityPlayer, bbis);
        World world = entityPlayer.world;
        hexColor = bbis.readInt();

        TEBase TE = (TEBase) world.getTileEntity(pos);

        if (TE != null) {
            if (hexColor != 16777215 && !TE.hasAttribute(TE.ATTR_FERTILIZER)) {
                TE.addAttribute(TE.ATTR_FERTILIZER, new ItemStack(Items.DYE, 1, 15));
                EntityLivingUtil.decrementCurrentSlot(entityPlayer);
            }
        }
    }

    @Override
    public void appendData(ByteBuf buffer) throws IOException
    {
        super.appendData(buffer);
        buffer.writeInt(hexColor);
    }

}
