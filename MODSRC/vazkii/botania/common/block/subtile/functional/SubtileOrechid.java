/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under a
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 License
 * (http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB)
 * 
 * File Created @ [Mar 11, 2014, 5:40:55 PM (GMT)]
 */
package vazkii.botania.common.block.subtile.functional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomItem;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.subtile.SubTileFunctional;

public class SubtileOrechid extends SubTileFunctional {

	private static final int COST = 17500;
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		if(!supertile.worldObj.isRemote && mana >= COST && supertile.worldObj.getTotalWorldTime() % 100 == 0) {
			ChunkCoordinates coords = getCoordsToPut();
			if(coords != null) {
				ItemStack stack = getOreToPut();
				if(stack != null) {
					supertile.worldObj.setBlock(coords.posX, coords.posY, coords.posZ, stack.itemID, stack.getItemDamage(), 1 | 2);
					
					mana -= COST;
					PacketDispatcher.sendPacketToAllInDimension(supertile.getDescriptionPacket(), supertile.worldObj.provider.dimensionId);
				}
			}
		}
	}
	
	public ItemStack getOreToPut() {
		Collection<WeightedRandomItem> values = new ArrayList();
		for(String s : BotaniaAPI.oreWeights.keySet())
			values.add(new StringRandomItem(BotaniaAPI.oreWeights.get(s), s));
			
		String ore = ((StringRandomItem) WeightedRandom.getRandomItem(supertile.worldObj.rand, values)).s;
		
		List<ItemStack> ores = OreDictionary.getOres(ore);
		if(ores.isEmpty())
			return getOreToPut();
		
		return ores.get(0);
	}
	
	public ChunkCoordinates getCoordsToPut() {
		List<ChunkCoordinates> possibleCoords = new ArrayList();
		int range = 5;
		int rangeY = 3;
		
		for(int i = -range; i < range; i++)
			for(int j = -rangeY; j < rangeY; j++)
				for(int k = -range; k < range; k++) {
					int x = supertile.xCoord + i;
					int y = supertile.yCoord + j;
					int z = supertile.zCoord + k;
					int id = supertile.worldObj.getBlockId(x, y, z);
					Block block = Block.blocksList[id];
					if(block != null && block.isGenMineableReplaceable(supertile.worldObj, x, y, z, Block.stone.blockID))
						possibleCoords.add(new ChunkCoordinates(x, y, z));
				}	
		
		if(possibleCoords.isEmpty())
			return null;
		return possibleCoords.get(supertile.worldObj.rand.nextInt(possibleCoords.size()));
	}
	
	@Override
	public int getColor() {
		return 0x818181;
	}
	
	@Override
	public int getMaxMana() {
		return COST;
	}
	
	private static class StringRandomItem extends WeightedRandomItem {

		public String s;
		
		public StringRandomItem(int par1, String s) {
			super(par1);
			this.s = s;
		}
		
	}
}
