package net.gotzi.minestrum.connection;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.gotzi.minestrum.protocol.Property;

@Data
@AllArgsConstructor
public class LoginResult
{

    private String id;
    private String name;
    private Property[] properties;
}
