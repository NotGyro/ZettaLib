package ws.zettabyte.zettalib.thermal;

import java.util.HashMap;

import net.minecraft.world.World;

//A singleton.
public class MasterHeatManager
{
    private final static MasterHeatManager instance = new MasterHeatManager();

    public static final MasterHeatManager getInstance ()
    {
        return instance;
    };

    //Maps dimension ID to HeatHandler
    private final HashMap<Integer, HeatManager> ManagerMap = new HashMap<Integer, HeatManager>(4);

    public final HeatManager getHandlerForWorld (World world)
    {
        Integer dimID = world.provider.dimensionId;
        //If a handler exists for this world, return it.
        if (ManagerMap.containsKey(dimID))
        {
            return ManagerMap.get(dimID);
        }
        //... otherwise, create one, put it, and return that.
        else
        {
            HeatManager newMan = new HeatManager(world);
            ManagerMap.put(dimID, newMan);
            return newMan;
        }
    }
}