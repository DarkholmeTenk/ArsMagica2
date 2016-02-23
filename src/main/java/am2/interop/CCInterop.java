package am2.interop;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import am2.blocks.tileentities.TileEntityKeystoneRecepticle;
import am2.blocks.tileentities.TileEntityLectern;
import am2.items.ItemsCommonProxy;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

@Optional.Interface(iface="dan200.computercraft.api.peripheral.IPeripheralProvider",modid="ComputerCraft")
public class CCInterop implements IPeripheralProvider
{
	public static final CCInterop i = new CCInterop();
	public static boolean installed = false;

	public static void init()
	{
		installed = Loader.isModLoaded("ComputerCraft");
		if(installed)
			i.register();
	}

	private void register()
	{
		ComputerCraftAPI.registerPeripheralProvider(this);
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof TileEntityLectern)
			return new LecternPeripheral((TileEntityLectern)te);
		if(te instanceof TileEntityKeystoneRecepticle)
			return new GatewayPeripheral((TileEntityKeystoneRecepticle) te);
		return null;
	}

	@Optional.Interface(iface="dan200.computercraft.api.peripheral.IPeripheral",modid="ComputerCraft")
	private class GatewayPeripheral implements IPeripheral
	{
		private final TileEntityKeystoneRecepticle recepticle;

		private GatewayPeripheral(TileEntityKeystoneRecepticle rec)
		{
			recepticle = rec;
		}

		@Override
		public String getType()
		{
			return "am2.gateway";
		}

		private final String[] methods = new String[]{"isOpenable","isOpen","open","close"};
		@Override
		public String[] getMethodNames()
		{
			return methods;
		}

		private Object[] isOpenable()
		{
			return new Object[]{recepticle.canActivate()};
		}

		private Object[] isOpen()
		{
			return new Object[]{recepticle.isActive()};
		}

		private Object[] open(Object[] args)
		{
			if(args.length != 3) return new Object[]{"Expected 3 numeric args"};
			long k = 0;
			int i = 0;
			for(Object o : args)
			{
				if((o != null) && !(o instanceof Double)) return new Object[]{"All arguments must be nil or a number >=0, <=16"};
				if(o == null) continue;
				int a = (int)Math.floor((Double)o);
				if((a < 0) || (a >= 16)) return new Object[]{"Expected numeric args >=0, <=16"};
				long ki = ItemsCommonProxy.rune.getKeyIndex(a);
				ki = ki << ((i++) << 4);
				k |= ki;
			}
			if(recepticle.isActive()) return new Object[]{"Already open"};
			if(!recepticle.canActivate()) return new Object[]{"Invalid structure"};
			recepticle.setActive(k);
			return new Object[]{true};
		}

		private Object[] close()
		{
			if(recepticle.isActive())
			{
				recepticle.deactivate();
				return new Object[]{true};
			}
			else
				return new Object[]{false};
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException
		{
			switch(method)
			{
				case 0: return isOpenable();
				case 1: return isOpen();
				case 2: return open(arguments);
				case 3: return close();
				default: return null;
			}
		}

		@Override
		public void attach(IComputerAccess computer)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void detach(IComputerAccess computer)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public boolean equals(IPeripheral other)
		{
			if(other instanceof GatewayPeripheral)
				return ((GatewayPeripheral)other).recepticle.equals(recepticle);
			return false;
		}
	}

	@Optional.Interface(iface="dan200.computercraft.api.peripheral.IPeripheral",modid="ComputerCraft")
	private class LecternPeripheral implements IPeripheral
	{
		private final TileEntityLectern lectern;

		private LecternPeripheral(TileEntityLectern lect)
		{
			lectern = lect;
		}

		@Override
		public String getType()
		{
			return "am2.lectern";
		}

		private final String[] methods = { "hasBook", "isBookWritten", "readBook" };
		@Override
		public String[] getMethodNames()
		{
			return methods;
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException
		{
			ItemStack is = lectern.getStack();
			Item i = is != null ? is.getItem() : null;
			switch(method)
			{
				case 0: return hasBook(is);
				case 1: return isBookWritten(i);
				case 2: return readBook(is, i);
			}
			return null;
		}

		private Object[] hasBook(ItemStack is)
		{
			return new Object[]{is != null};
		}

		private Object[] isBookWritten(Item i)
		{
			return new Object[]{i == Items.written_book};
		}

		private Object[] readBook(ItemStack is, Item item)
		{
			if(item != Items.written_book) return null;
			NBTTagCompound nbt = is.stackTagCompound;
			if(nbt == null) return null;
			NBTTagList list = nbt.getTagList("pages", 8);
			String[][] data = new String[list.tagCount()][];
			int count = 0;
			for(int i = 0; i < data.length; i++)
			{
				String s = list.getStringTagAt(i);
				data[i] = s.split("\n");
				count += data[i].length;
			}
			Object[] returnData = new Object[count];
			int c = 0;
			for(int i = 0; i < data.length; i++)
				for(int j = 0; j < data[i].length; j++)
					returnData[c++] = data[i][j];
			return returnData;
		}

		@Override
		public void attach(IComputerAccess computer)
		{
		}

		@Override
		public void detach(IComputerAccess computer)
		{
		}

		@Override
		public boolean equals(IPeripheral other)
		{
			if(other instanceof LecternPeripheral)
				return lectern.equals(((LecternPeripheral)other).lectern);
			return false;
		}
	}

}
