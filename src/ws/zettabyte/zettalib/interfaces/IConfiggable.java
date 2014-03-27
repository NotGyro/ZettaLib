package ws.zettabyte.zettalib.interfaces;
import ws.zettabyte.zettalib.ContentRegistry;
import net.minecraftforge.common.Configuration;

public interface IConfiggable {
	void doConfig(Configuration config, ContentRegistry cr);
}
