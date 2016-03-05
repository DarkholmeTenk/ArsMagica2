package am2.blocks.liquid;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;

import java.util.Random;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import am2.configuration.AMConfig;
import am2.texture.ResourceManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLiquidEssence extends BlockFluidClassic{

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public static final Fluid liquidEssenceFluid = new FluidEssence();
	public static final Material liquidEssenceMaterial = new MaterialLiquid(MapColor.iceColor);

	public BlockLiquidEssence(){
		super(liquidEssenceFluid, liquidEssenceMaterial);
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z){
		return 9;
	}

	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister){
		icons = new IIcon[2];

		icons[0] = ResourceManager.RegisterTexture("liquidEssenceStill", par1IconRegister);
		icons[1] = ResourceManager.RegisterTexture("liquidEssenceFlowing", par1IconRegister);

		liquidEssenceFluid.setIcons(icons[0], icons[1]);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		if (side <= 1)
			return icons[0]; //still
		else
			return icons[1]; //flowing
	}

	@Override
	public void updateTick(World w, int x, int y, int z, Random r)
    {
		if(AMConfig.etheriumRefill > 1)
		{
			int c = 0;
			SimpleCoordStore scs = new SimpleCoordStore(w,x,y,z);
			if(scs.getMetadata() > 0)
			{
				for(ForgeDirection f : ForgeDirection.VALID_DIRECTIONS)
				{
					if((f == ForgeDirection.UP) || (f == ForgeDirection.DOWN)) continue;
					SimpleCoordStore n = scs.getNearby(f);
					if((n.getBlock() == this) && (n.getMetadata() == 0))
						c++;
				}
				if(c >= AMConfig.etheriumRefill)
				{
					scs.setMetadata(0, 3);
					//return;
				}
			}
		}
		super.updateTick(w, x, y, z, r);
    }

}
