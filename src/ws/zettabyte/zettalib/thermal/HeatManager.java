package ws.zettabyte.zettalib.thermal;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.ImmutablePair;

import cofh.util.BlockCoord;
import cofh.util.ChunkCoord;

public class HeatManager
{

    private final World world;

    public HeatManager(World w)
    {
        world = w;
    }

    //Apologies for the nested types. Was necessary in this case.
    //Key: Coordinate of the chunk to investigate.
    //ArrayList: Heat handlers in the chunk.
    //Immutable pair type: Coordinate of, and instance of, heat handler.
    private final HashMap<ChunkCoord, ArrayList<ImmutablePair<BlockCoord, IHeatHandler>>> handlerChunks = new HashMap<ChunkCoord, ArrayList<ImmutablePair<BlockCoord, IHeatHandler>>>(128);

    public final int getHeatAt (int x, int y, int z)
    {
        BlockCoord blockCoord = new BlockCoord(x, y, z);
        ChunkCoord chunkCoord = new ChunkCoord(blockCoord);
        //No entries for this chunk, therefore, no heat handlers within.
        if (!handlerChunks.containsKey(chunkCoord))
        {
            return getAmbientHeatAt(x, y, z);
        }
        else
        {
            int total = 0;
            //Get heat information for this chunk and adjacent.
            ChunkCoord current = new ChunkCoord(chunkCoord.chunkX, chunkCoord.chunkZ);
            for (int offsetX = -1; offsetX <= 1; ++offsetX)
            {
                for (int offsetZ = -1; offsetZ <= 1; ++offsetZ)
                {
                    current.chunkX = chunkCoord.chunkX + offsetX;
                    current.chunkZ = chunkCoord.chunkZ + offsetZ;
                    total += getHeatFromChunk(current, x, y, z);
                }
            }
            return total + getAmbientHeatAt(x, y, z);
        }
    }

    //Avoiding some copy+paste
    private final int getHeatFromChunk (ChunkCoord chunk, int x, int y, int z)
    {
        //Check to be sure that the target block is loaded.
        if (!world.blockExists(x, y, z))
        {
            throw new IllegalArgumentException("Attempted to get heat information for block at " + x + "," + y + "," + z + ", which does not exist.");
        }
        ArrayList<ImmutablePair<BlockCoord, IHeatHandler>> handlers = handlerChunks.get(chunk);
        int total = 0;
        //Iterate through aforementioned list.
        //Assume the IHeatHandler can deal with its own range mechanics.
        for (int i = 0; i < handlers.size(); ++i)
        {
            ImmutablePair<BlockCoord, IHeatHandler> current = handlers.get(i);

            //Make sure our heat producer is loaded.
            if (!world.blockExists(current.left.x, current.left.y, current.left.z))
            {
                throw new IllegalArgumentException("Attempted to get heat information produced by block at " + x + "," + y + "," + z + ", which does not exist.");
            }
            total += current.right.getHeatAt(x, y, z);
        }
        return total;
    }

    //Gets the ambient heat from the biome at the specified block location.
    public final int getAmbientHeatAt (int x, int y, int z)
    {
        //TODO
        return 18;
    }

    public final void RegisterHeatBlock (IHeatHandler toReg, int x, int y, int z)
    {
        BlockCoord blockCoord = new BlockCoord(x, y, z);
        ChunkCoord chunkCoord = new ChunkCoord(blockCoord);

        ArrayList<ImmutablePair<BlockCoord, IHeatHandler>> handlers = null;
        if (!handlerChunks.containsKey(chunkCoord))
        {
            handlers = new ArrayList<ImmutablePair<BlockCoord, IHeatHandler>>(32);
            handlerChunks.put(chunkCoord, handlers);
        }
        else
        {
            handlers = handlerChunks.get(chunkCoord);
        }

        //Notify potentially nearby heat handlers that another has been added.
        ChunkCoord current = new ChunkCoord(chunkCoord.chunkX, chunkCoord.chunkZ);
        for (int offsetX = -1; offsetX <= 1; ++offsetX)
        {
            for (int offsetZ = -1; offsetZ <= 1; ++offsetZ)
            {
                current.chunkX = chunkCoord.chunkX + offsetX;
                current.chunkZ = chunkCoord.chunkZ + offsetZ;
                //Are there heat handlers in this chunk?
                ArrayList<ImmutablePair<BlockCoord, IHeatHandler>> handlersNotify = null;
                if (handlerChunks.containsKey(chunkCoord))
                {
                    handlersNotify = handlerChunks.get(current);
                    //Notify each, being sure to dodge null pointer errors.
                    if(handlersNotify != null) 
                    {
                        for (ImmutablePair<BlockCoord, IHeatHandler> heater : handlersNotify)
                        {
                            //Register heat handlers with eachother.
                            toReg.NotifyNearby(heater.right);
                            heater.right.NotifyNearby(toReg);
                        }
                    }
                }
            }
        }

        handlers.add(new ImmutablePair<BlockCoord, IHeatHandler>(blockCoord, toReg));
    }

    public final void UnregisterHeatBlock (IHeatHandler toReg, int x, int y, int z)
    {
        BlockCoord blockCoord = new BlockCoord(x, y, z);
        ChunkCoord chunkCoord = new ChunkCoord(blockCoord);

        ArrayList<ImmutablePair<BlockCoord, IHeatHandler>> handlers = null;
        if (!handlerChunks.containsKey(chunkCoord))
        {
            //There's no way this exists to unreg to begin with.
            return;
        }
        else
        {
            handlers = handlerChunks.get(chunkCoord);
        }
        for (int i = 0; i < handlers.size(); ++i)
        {
            //Compare position
            if (((handlers.get(i).left.x == x) && (handlers.get(i).left.y == y)) && (handlers.get(i).left.x == z))
            {
                //Remove this element from the list.
                handlers.remove(i);
            }
        }

        //Notify potentially nearby heat handlers that this one has been removed.
        ChunkCoord current = new ChunkCoord(chunkCoord.chunkX, chunkCoord.chunkZ);
        for (int offsetX = -1; offsetX <= 1; ++offsetX)
        {
            for (int offsetZ = -1; offsetZ <= 1; ++offsetZ)
            {
                current.chunkX = chunkCoord.chunkX + offsetX;
                current.chunkZ = chunkCoord.chunkZ + offsetZ;
                //Are there heat handlers in this chunk?
                ArrayList<ImmutablePair<BlockCoord, IHeatHandler>> handlersNotify = null;
                if (handlerChunks.containsKey(chunkCoord))
                {
                    handlersNotify = handlerChunks.get(current);
                    //Notify each, making sure to avoid an NPE.
                    if(handlersNotify != null)
                    {
                        for (ImmutablePair<BlockCoord, IHeatHandler> heater : handlersNotify)
                        {
                            heater.right.NearbyRemoved(toReg);
                        }
                    }
                }
            }
        }
    }
}
