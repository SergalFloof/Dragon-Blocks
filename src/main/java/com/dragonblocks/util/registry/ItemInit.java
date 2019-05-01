package com.dragonblocks.util.registry;

import java.util.ArrayList;
import java.util.List;

import com.dragonblocks.objects.items.ItemBed;
import com.dragonblocks.objects.items.ItemChisel;
import com.dragonblocks.objects.items.ItemDoor;
import com.dragonblocks.objects.items.ItemHammer;
import com.dragonblocks.objects.items.ItemTile;

import net.minecraft.item.Item;

public class ItemInit {
	
	public static final List<Item> ITEMS = new ArrayList<Item>();
	
	public static boolean enableHammer                  = true;
    public static boolean enableChisel                  = true;
    public static boolean enableTile                    = true;
    public static int     itemCarpentersToolsUses       = 400;
    public static boolean itemCarpentersToolsDamageable = true;

    public static double itemHammerDamageChanceFromSlopes      = 0.75D;
    public static double itemHammerDamageChanceFromStairs      = 1.0D;
    public static double itemHammerDamageChanceFromCollapsible = 0.2D;
	
	//Tools
	public static final Item HAMMER = new ItemHammer("hammer");
	public static final Item CHISEL = new ItemChisel("chisel");
	public static final Item DOOR = new ItemDoor("door");
	public static final Item BED = new ItemBed("bed");
	public static final Item TILE = new ItemTile("tile");
}
