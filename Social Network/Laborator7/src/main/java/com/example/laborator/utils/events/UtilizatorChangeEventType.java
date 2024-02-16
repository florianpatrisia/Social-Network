package  com.example.laborator.utils.events;
import com.example.laborator.domain.Entity;

public class UtilizatorChangeEventType implements Event{
    private ChangeEventType type;
    private Entity data, oldData;

    public UtilizatorChangeEventType(ChangeEventType type, Entity data) {
        this.type = type;
        this.data = data;
    }

    public UtilizatorChangeEventType(ChangeEventType type, Entity data, Entity oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Entity getData() {
        return data;
    }

    public Entity getOldData() {
        return oldData;
    }
}
