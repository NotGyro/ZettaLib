package ws.zettabyte.zettalib.interfaces;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface ISolidFuelInfo {
	ItemStack getFuel();
	int getEnergyPer();
	ItemStack getByproduct();
	FluidStack getExhaust();
	float getByproductMult();
}
