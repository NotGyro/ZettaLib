package ws.zettabyte.zettalib.thermal;

import net.minecraft.world.World;
import cofh.util.BlockCoord;

/**
* Note: "heat" is used where "temperature" would be more appropriate
* to save on code length and thereby improve readability.
*/
public interface IHeatHandler
{
    /**
    * @return Absolute, not relative to ambient temperature in biome.
    */
    int getHeat ();

    /**
    * The coordinates are the position of the block
    * we are checking - the block affected by this one's heat.
    */
    int getHeatAt (int targetX, int targetY, int targetZ);

    /**
    * Takes in the largest (absolute, distance-from-zero) value to
    * look at, to see if we can alter this block's temperature by
    * that value.
    * @return The greatest (distance-from-zero) value up to maxAlter
    * we can shift this IHeatHandler's temperature by.
    */
    int AmountAlterHeat (int maxAlter);

    /**
    * Attempts to shift our IHeatHandler's temperature
    * by maxAlter.
    * Is implied to call AmountAlterHeat under the hood.
    * @return The value by which our temperature was
    * ultimately altered.
    */
    int AlterHeat (int maxAlter);

    /**
    * Used by the heat manager classes to inform heat handler tiles
    * of the existence of other heat handler tiles. Add to a cache
    * of heat handlers within range here.
    */
    void NotifyNearby (IHeatHandler other);

    /**
    * Clean-up function: When a heat handler dies, notify other
    * heat handlers that it is no longer supposed to exist.
    */
    void NearbyRemoved (IHeatHandler other);

    /**
    * Not guaranteed to be valid with Redstone in Motion frames;
    * needs testing.
    */
    BlockCoord getPosition ();

    /**
    * Called when removed.
    */
    void onKill ();
    
    void setWorld (World w);
}