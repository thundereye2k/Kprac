package us.centile.practice.manager.managers;

import java.util.*;
import us.centile.practice.player.*;
import us.centile.practice.manager.*;
import java.util.concurrent.*;
import us.centile.practice.util.*;

public class InventorySnapshotManager extends Manager
{
    private Map<UUID, PlayerInventorySnapshot> snapshotMap;
    
    public InventorySnapshotManager(final ManagerHandler handler) {
        super(handler);
        this.snapshotMap = new TtlHashMap<UUID, PlayerInventorySnapshot>(TimeUnit.MINUTES, 1L);
    }
    
    public void addSnapshot(final UUID uuid, final PlayerInventorySnapshot playerInventorySnapshot) {
        this.snapshotMap.put(uuid, playerInventorySnapshot);
    }
    
    public PlayerInventorySnapshot getSnapshotFromUUID(final UUID uuid) {
        return this.snapshotMap.get(uuid);
    }
}
