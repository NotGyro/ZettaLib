package ws.zettabyte.zettalib.thermal;

import java.util.ArrayList;

import net.minecraft.world.World;
import cofh.util.BlockCoord;

//For use with tile entities and the like, which have instance-specific heat information.
public class BasicHeatLogic implements IHeatHandler
{

    protected int temperature = 18;
    protected int range = 8;
    protected int rangeSquared = range * range;
    protected BlockCoord position;

    protected ArrayList<IHeatHandler> withinRange = new ArrayList<IHeatHandler>(8);
    protected World world;

    public BasicHeatLogic()
    {
    };

    public BasicHeatLogic(BlockCoord coord)
    {
        position = coord.copy();
    }

    @Override
    public int getHeat ()
    {
        return temperature;
    }

    @Override
    public int getHeatAt (int targetX, int targetY, int targetZ)
    {
        int diffX = position.x - targetX;
        int diffY = position.y - targetY;
        int diffZ = position.z - targetZ;
        int squaredDist = (diffX * diffX + diffY * diffY + diffZ * diffZ);
        //Squared distance check, to make sure it's within range.
        if (squaredDist <= rangeSquared)
        {
            return (int) (((float) temperature) * getFalloffFactor((int) Math.sqrt(squaredDist)));
        }
        else
        {
            return 0;
        }
    }

    //For basic linear falloff.
    protected float getFalloffFactor (int distance)
    {
        return ((float) distance) / ((float) range);
    }

    @Override
    public int AmountAlterHeat (int maxAlter)
    {
        int amt = maxAlter;
        return amt;
    }

    @Override
    public int AlterHeat (int maxAlter)
    {
        temperature += AmountAlterHeat(maxAlter);
        return maxAlter;
    }

    public void setHeat (int temp)
    {
        temperature = temp;
    }

    //Wherein heat balancing code happens.
    public void TickHeat (World world)
    {
        //TODO
    }

    @Override
    public BlockCoord getPosition ()
    {
        return position;
    }

    @Override
    public void onKill ()
    {
        for (int i = 0; i < withinRange.size(); ++i)
        {
            withinRange.get(i).NearbyRemoved(this);
        }
    }

    @Override
    public void NotifyNearby (IHeatHandler other)
    {
        if (other != null)
        {
            if (!withinRange.contains(other))
            {
                withinRange.add(other);
            }
        }
    }

    @Override
    public void NearbyRemoved (IHeatHandler other)
    {
        if (withinRange.contains(other))
        {
            withinRange.remove(other);
        }
    }

    @Override
    public void setWorld (World w)
    {
        world = w;
    }

    public void setPosition (BlockCoord position)
    {
        this.position = position;
    }
}